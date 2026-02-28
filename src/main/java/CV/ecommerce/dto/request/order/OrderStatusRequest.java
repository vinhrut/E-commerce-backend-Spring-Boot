package CV.ecommerce.dto.request.order;

import CV.ecommerce.enums.OrderStatus;
import lombok.Data;

@Data
public class OrderStatusRequest {
    private OrderStatus status;
}
