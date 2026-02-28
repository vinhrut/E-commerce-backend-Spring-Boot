package CV.ecommerce.dto.response;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OrderItemResponse {
    private String productName;
    private int quantity;
    private double totalPrice;
    private String sizeName;
    private List<String> imageUrl;
}