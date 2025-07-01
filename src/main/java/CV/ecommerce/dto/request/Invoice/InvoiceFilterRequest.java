package CV.ecommerce.dto.request.Invoice;

import java.time.LocalDateTime;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class InvoiceFilterRequest {
    private final String status;
    private final String orderId;
    private final String billingEmail;
    private final LocalDateTime from;
    private final LocalDateTime to;
}
