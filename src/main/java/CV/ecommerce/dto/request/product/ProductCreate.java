package CV.ecommerce.dto.request.product;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ProductCreate {

    @NotBlank(message = "NAME_REQUIRED")
    String name;

    @NotNull(message = "PRICE_REQUIRED")
    @Positive(message = "PRICE_MUST_BE_POSITIVE")
    Double price;

    @NotBlank(message = "DESCRIPTION_REQUIRED")
    String description;

    @NotBlank(message = "TYPE_REQUIRED")
    String type;

    @NotBlank(message = "MATERIAL_REQUIRED")
    String material;

    @NotBlank(message = "SIZES_REQUIRED")
    String sizes;

    @Data
    public static class ProductSizeRequest {
        @NotBlank(message = "SIZE_NAME_REQUIRED")
        String sizeName;

        @Positive(message = "QUANTITY_MUST_BE_POSITIVE")
        Integer quantity;
    }
}
