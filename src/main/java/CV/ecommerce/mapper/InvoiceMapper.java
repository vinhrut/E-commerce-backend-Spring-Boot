package CV.ecommerce.mapper;

import CV.ecommerce.dto.response.InvoiceResponse;
import CV.ecommerce.entity.Invoice;
import org.springframework.stereotype.Component;

@Component
public class InvoiceMapper {

    public InvoiceResponse toResponse(Invoice invoice) {
        return InvoiceResponse.builder()
                .id(invoice.getId())
                .invoiceCode(invoice.getInvoiceCode())
                .orderId(invoice.getOrder().getId())
                .billingName(invoice.getBillingName())
                .billingEmail(invoice.getBillingEmail())
                .billingPhone(invoice.getBillingPhone())
                .paymentMethod(invoice.getPaymentMethod())
                .paymentStatus(invoice.getPaymentStatus())
                .paymentAmount(invoice.getOrder().getTotalAmount())
                .paymentDate(invoice.getPaymentDate())
                .filePath(invoice.getFilePath())
                .shippingAddress(invoice.getShippingAddress())
                .issuedAt(invoice.getIssuedAt())
                .build();
    }
}
