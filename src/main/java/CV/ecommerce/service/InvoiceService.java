package CV.ecommerce.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import CV.ecommerce.dto.request.Invoice.InvoiceFilterRequest;
import CV.ecommerce.dto.response.InvoiceResponse;
import CV.ecommerce.entity.Invoice;
import CV.ecommerce.entity.Order;
import CV.ecommerce.entity.Payment;
import CV.ecommerce.entity.ShippingInfo;
import CV.ecommerce.exception.AppException;
import CV.ecommerce.mapper.InvoiceMapper;
import CV.ecommerce.repository.InvoiceRepository;
import CV.ecommerce.repository.PaymentRepository;
import jakarta.persistence.criteria.Predicate;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class InvoiceService {

    InvoiceRepository invoiceRepository;
    PaymentRepository paymentRepository;
    InvoicePdfExporter invoicePdfExporter;
    MinioService minioService;
    InvoiceMapper invoiceMapper;
    SecurityService securityService;

    public Invoice createInvoiceAfterPayment(Order order) {
        Payment payment = paymentRepository.findByOrderId(order.getId())
                .orElseThrow(() -> new RuntimeException("Payment not found"));

        String invoiceCode = generateInvoiceCode();
        ShippingInfo info = order.getShippingInfo();

        String shippingAddress = String.format(
                "%s, %s, %s, %s",
                info.getStreetAddress(),
                info.getWard(),
                info.getDistrict(),
                info.getProvince());

        Invoice invoice = Invoice.builder()
                .invoiceCode(invoiceCode)
                .order(order)
                .issuedAt(LocalDateTime.now())
                .billingPhone(order.getUser().getPhone())
                .billingName(order.getUser().getFullName())
                .billingEmail(order.getUser().getEmail())
                .shippingAddress(shippingAddress)
                .paymentMethod(payment.getPaymentMethod())
                .paymentStatus(payment.getStatus())
                .paymentDate(payment.getPaymentDate())
                .status("GENERATED")
                .build();

        return invoiceRepository.save(invoice);
    }

    private String generateInvoiceCode() {
        String date = java.time.LocalDate.now().toString().replace("-", "");
        String random = UUID.randomUUID().toString().substring(0, 6).toUpperCase();
        return "INV-" + date + "-" + random;
    }

    public Optional<Invoice> getByOrderId(String orderId) {
        return invoiceRepository.findByOrderId(orderId);
    }

    public Page<Invoice> filterInvoices(InvoiceFilterRequest filter, Pageable pageable) {
        return invoiceRepository.findAll((root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (filter.getStatus() != null && !filter.getStatus().isEmpty()) {
                predicates.add(cb.equal(root.get("status"), filter.getStatus()));
            }

            if (filter.getOrderId() != null && !filter.getOrderId().isEmpty()) {
                predicates.add(cb.equal(root.get("order").get("id"), filter.getOrderId()));
            }

            if (filter.getBillingEmail() != null && !filter.getBillingEmail().isEmpty()) {
                predicates.add(cb.like(cb.lower(root.get("billingEmail")),
                        "%" + filter.getBillingEmail().toLowerCase() + "%"));
            }

            if (filter.getFrom() != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("issuedAt"), filter.getFrom()));
            }

            if (filter.getTo() != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("issuedAt"), filter.getTo()));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        }, pageable);
    }

    public Optional<Invoice> getById(String id) {
        return invoiceRepository.findById(id);
    }

    public Invoice updateFilePath(String invoiceId, String filePath) {
        Invoice invoice = invoiceRepository.findById(invoiceId)
                .orElseThrow(() -> new RuntimeException("Invoice not found"));

        invoice.setFilePath(filePath);
        return invoiceRepository.save(invoice);
    }

    public List<Invoice> getAll() {
        return invoiceRepository.findAll();
    }

    public List<Invoice> getByUser(String userId) {
        return invoiceRepository
                .findAll((root, query, cb) -> cb.equal(root.get("order").get("user").get("id"), userId));
    }

    public String generateAndUploadInvoicePdf(String invoiceId) throws Exception {
        Invoice invoice = invoiceRepository.findById(invoiceId)
                .orElseThrow(() -> new AppException(404, "Invoice not found"));

        if (invoice.getFilePath() != null && !invoice.getFilePath().isBlank()) {
            throw new AppException(409, "Hóa đơn đã được xuất trước đó.");
        }

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUserId = authentication.getName();
        String role = authentication.getAuthorities().stream()
                .map(auth -> auth.getAuthority().replace("ROLE_", ""))
                .findFirst().orElse("");

        if (!role.equals("ADMIN") && !role.equals("STAFF")) {
            String orderId = invoice.getOrder().getId();
            boolean isOwner = securityService.isOrderOwner(orderId);
            if (!isOwner) {
                throw new AppException(403, "Bạn không có quyền xuất hóa đơn này.");
            }
        }

        InvoiceResponse dto = invoiceMapper.toResponse(invoice);
        byte[] pdfBytes = invoicePdfExporter.export(dto);
        String fileName = "invoice/" + dto.getInvoiceCode() + ".pdf";

        minioService.uploadFile(fileName, pdfBytes, "application/pdf");

        String fileUrl = minioService.getFileUrl(fileName);
        invoice.setFilePath(fileUrl);
        invoiceRepository.save(invoice);

        return fileUrl;
    }

}
