package CV.ecommerce.dto.response;

import java.util.List;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ProductResponse {
    private String id;
    private String name;
    private Double price;
    private String description;
    private String type;
    private String material;
    private List<String> images;
    private List<ProductSizeResponse> sizes;

    @Data
    @Builder
    public static class ProductSizeResponse {
        private String sizeName;
        private int quantity;
    }
}
