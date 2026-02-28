package CV.ecommerce.dto.request.cart;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CartItemFilterRequest {
    private final String userId;
    private final String productName;
    private final String size;
    private final Double minPrice;
    private final Double maxPrice;
}
