package CV.ecommerce.dto.response;

import java.util.List;

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
public class CartItemResponse {
    Long id;
    Long cartId;
    String userId;
    String productId;
    String productName;
    Integer quantity;
    String size;
    double totalPrice;
    List<String> images;
}
