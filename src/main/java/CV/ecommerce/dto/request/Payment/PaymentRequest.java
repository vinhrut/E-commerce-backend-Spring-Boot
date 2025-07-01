package CV.ecommerce.dto.request.Payment;

import lombok.Data;

@Data
public class PaymentRequest {
    private String orderId;
    private String paymentMethod;
}
