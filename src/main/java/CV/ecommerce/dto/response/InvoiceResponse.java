package CV.ecommerce.dto.response;

import java.time.LocalDateTime;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class InvoiceResponse {
    private String id;
    private String invoiceCode;
    private String orderId;
    private String billingName;
    private String billingEmail;
    private String billingPhone;
    private String paymentMethod;
    private String paymentStatus;
    private Double paymentAmount;
    private LocalDateTime paymentDate;
    private String filePath;
    private String shippingAddress;
    private LocalDateTime issuedAt;
}
