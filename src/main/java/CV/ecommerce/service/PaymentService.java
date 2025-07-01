package CV.ecommerce.service;

import CV.ecommerce.configuration.VnPayConfig;
import CV.ecommerce.dto.response.PaymentResponse;
import CV.ecommerce.entity.Order;
import CV.ecommerce.entity.OrderItem;
import CV.ecommerce.entity.Payment;
import CV.ecommerce.entity.ProductSize;
import CV.ecommerce.enums.OrderStatus;
import CV.ecommerce.repository.InvoiceRepository;
import CV.ecommerce.repository.OrderRepository;
import CV.ecommerce.repository.PaymentRepository;
import CV.ecommerce.repository.ProductSizeRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

import org.springframework.stereotype.Service;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class PaymentService {

    PaymentRepository paymentRepository;
    OrderRepository orderRepository;
    ProductSizeRepository productSizeRepository;
    VnPayConfig vnPayConfig;
    InvoiceService invoiceService;
    InvoiceRepository invoiceRepository;

    public PaymentResponse createPaymentForOrder(String orderId, String clientIp) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        if (order.getStatus() != OrderStatus.PENDING) {
            return PaymentResponse.builder()
                    .txnRef(null)
                    .paymentUrl(null)
                    .amount(null)
                    .status("ERROR")
                    .message("Order is not pending, can not create payment")
                    .build();
        }

        Optional<Payment> existing = paymentRepository.findByOrderAndStatus(order, "PENDING");
        if (existing.isPresent()) {
            Payment old = existing.get();
            String url = buildVnpayUrl(old.getAmount(), old.getTxnRef(), clientIp, order.getId());
            return PaymentResponse.builder()
                    .txnRef(old.getTxnRef())
                    .paymentUrl(url)
                    .amount(old.getAmount())
                    .status("PENDING")
                    .message("Already have a pending payment for this order")
                    .build();
        }

        String txnRef = System.currentTimeMillis() + "-" + UUID.randomUUID().toString().substring(0, 8);
        while (paymentRepository.findByTxnRef(txnRef).isPresent()) {
            txnRef = System.currentTimeMillis() + "-" + UUID.randomUUID().toString().substring(0, 8);
        }

        Long amount = order.getTotalAmount().longValue();
        Payment payment = Payment.builder()
                .order(order)
                .amount(amount)
                .txnRef(txnRef)
                .status("PENDING")
                .paymentMethod("vnpay")
                .build();

        paymentRepository.save(payment);
        String url = buildVnpayUrl(amount, txnRef, clientIp, order.getId());

        return PaymentResponse.builder()
                .txnRef(txnRef)
                .paymentUrl(url)
                .amount(amount)
                .status("PENDING")
                .message("Create payment successfully")
                .build();
    }

    public PaymentResponse handleVnpayReturn(Map<String, String> params) {
        String receivedHash = params.remove("vnp_SecureHash");
        params.remove("vnp_SecureHashType");

        String calculatedHash = vnPayConfig.hashAllFields(params);

        String txnRef = params.get("vnp_TxnRef");
        String responseCode = params.get("vnp_ResponseCode");
        String bankCode = params.get("vnp_BankCode");
        String payDate = params.get("vnp_PayDate");

        boolean isValidSignature = receivedHash != null && receivedHash.equals(calculatedHash);
        boolean isSuccess = "00".equals(responseCode) && isValidSignature;

        if (isSuccess) {
            updatePaymentStatus(txnRef, "SUCCESS", bankCode, payDate);
        } else {
            updatePaymentStatus(txnRef, "FAILED", bankCode, null);
        }

        return PaymentResponse.builder()
                .txnRef(txnRef)
                .status(isSuccess ? "SUCCESS" : "FAILED")
                .message(getErrorMessage(responseCode, isValidSignature))
                .build();
    }

    private void updatePaymentStatus(String txnRef, String status, String bankCode, String payDate) {
        Payment payment = paymentRepository.findByTxnRef(txnRef)
                .orElseThrow(() -> new RuntimeException("Payment not found"));

        payment.setStatus(status);
        payment.setBankCode(bankCode);

        if ("SUCCESS".equals(status) && payDate != null) {

            payment.setPaymentDate(LocalDateTime.parse(payDate, DateTimeFormatter.ofPattern("yyyyMMddHHmmss")));

            Order order = payment.getOrder();
            order.setStatus(OrderStatus.COMPLETED);

            for (OrderItem item : order.getOrderItems()) {
                String productId = item.getProduct().getId();
                String sizeName = item.getSize();
                int quantity = item.getQuantity();

                ProductSize productSize = productSizeRepository
                        .findByProductIdAndSizeName(productId, sizeName)
                        .orElseThrow(() -> new RuntimeException(
                                "Product size not found for product " + productId + ", size " + sizeName));

                if (productSize.getQuantity() < quantity) {
                    throw new RuntimeException("Not enough quantity for product " + productId + ", size " + sizeName);
                }

                productSize.setQuantity(productSize.getQuantity() - quantity);
                productSizeRepository.save(productSize);
            }

            orderRepository.save(order);

            if (invoiceRepository.findByOrderId(order.getId()).isEmpty()) {
                invoiceService.createInvoiceAfterPayment(order);
            }
        }

        paymentRepository.save(payment);
    }

    private String buildVnpayUrl(Long amount, String txnRef, String clientIp, String orderId) {
        String vnp_CreateDate = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));

        Map<String, String> vnp_Params = new HashMap<>();
        vnp_Params.put("vnp_Version", "2.1.0");
        vnp_Params.put("vnp_Command", "pay");
        vnp_Params.put("vnp_TmnCode", vnPayConfig.getVnpTmnCode());
        vnp_Params.put("vnp_Amount", String.valueOf(amount * 100));
        vnp_Params.put("vnp_CurrCode", "VND");
        vnp_Params.put("vnp_TxnRef", txnRef);
        vnp_Params.put("vnp_OrderInfo", "Payment for order: " + orderId);
        vnp_Params.put("vnp_OrderType", "other");
        vnp_Params.put("vnp_Locale", "vn");
        vnp_Params.put("vnp_ReturnUrl", vnPayConfig.getVnpReturnUrl());
        vnp_Params.put("vnp_IpAddr", clientIp);
        vnp_Params.put("vnp_CreateDate", vnp_CreateDate);

        List<String> fieldNames = new ArrayList<>(vnp_Params.keySet());
        Collections.sort(fieldNames);

        StringBuilder hashData = new StringBuilder();
        StringBuilder query = new StringBuilder();

        for (int i = 0; i < fieldNames.size(); i++) {
            String key = fieldNames.get(i);
            String value = vnp_Params.get(key);
            if (value != null && !value.isEmpty()) {
                String encodedKey = URLEncoder.encode(key, StandardCharsets.US_ASCII);
                String encodedValue = URLEncoder.encode(value, StandardCharsets.US_ASCII);

                hashData.append(encodedKey).append('=').append(encodedValue);
                query.append(encodedKey).append('=').append(encodedValue);

                if (i < fieldNames.size() - 1) {
                    hashData.append('&');
                    query.append('&');
                }
            }
        }

        String secureHash = vnPayConfig.hmacSHA512(vnPayConfig.getSecretKey(), hashData.toString());
        query.append("&vnp_SecureHash=").append(secureHash);

        return vnPayConfig.getVnpPayUrl() + "?" + query;
    }

    private String getErrorMessage(String code, boolean isValidSignature) {
        if (!isValidSignature)
            return "Signature is not valid.";
        return switch (code) {
            case "00" -> "Payment is successful";
            case "07" -> "Suspected fraudulent transaction";
            case "09" -> "Internet Banking transaction failed";
            case "10" -> "Verification failed";
            case "11" -> "Time out transaction";
            case "12" -> "Account is locked";
            case "24" -> "User cancel transaction";
            case "51" -> "Balance is not enough";
            case "65" -> "Over transaction limit";
            default -> "Transaction fail with code: " + code;
        };
    }
}
