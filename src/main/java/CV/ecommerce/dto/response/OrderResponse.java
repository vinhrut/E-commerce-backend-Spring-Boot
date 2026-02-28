package CV.ecommerce.dto.response;

import java.time.LocalDateTime;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OrderResponse {
    private String orderId;
    private String status;
    private double totalAmount;
    private LocalDateTime createdAt;
    private List<OrderItemResponse> items;
    private ShippingInfoResponse shippingInfo;

}
