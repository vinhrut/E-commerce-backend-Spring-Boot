package CV.ecommerce.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PaymentResponse {
    private String paymentUrl;
    private String txnRef;
    private Long amount;
    private String message;
    private String status;
}
