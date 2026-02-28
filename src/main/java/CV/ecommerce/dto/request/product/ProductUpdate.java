package CV.ecommerce.dto.request.product;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ProductUpdate {

    String name;

    Double price;

    String description;

    String type;

    String material;

    String sizes;
}
