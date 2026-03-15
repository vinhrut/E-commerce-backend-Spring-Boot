package CV.ecommerce.mapper;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;

import CV.ecommerce.dto.request.product.ProductCreate;
import CV.ecommerce.dto.request.product.ProductUpdate;
import CV.ecommerce.dto.response.ProductResponse;
import CV.ecommerce.entity.Product;
import CV.ecommerce.entity.ProductSize;
import CV.ecommerce.service.MinioService;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class ProductMapper {
    private final MinioService minioService;

    public ProductResponse mapToProductResponse(Product product) {
        List<ProductResponse.ProductSizeResponse> sizeResponses = new ArrayList<>();
        for (ProductSize size : product.getSizes()) {
            sizeResponses.add(
                    ProductResponse.ProductSizeResponse.builder()
                            .sizeName(size.getSizeName())
                            .quantity(size.getQuantity())
                            .build());
        }

        List<String> imageUrls = new ArrayList<>();
        for (String imageUrl : product.getImages()) {
            imageUrls.add(minioService.getFileUrl(imageUrl));
        }

        return ProductResponse.builder()
                .id(product.getId())
                .name(product.getName())
                .price(product.getPrice())
                .description(product.getDescription())
                .type(product.getType())
                .material(product.getMaterial())
                .images(imageUrls)
                .sizes(sizeResponses)
                .build();
    }

    public Product mapCreateToProduct(ProductCreate request) {
        Product product = new Product();
        product.setName(request.getName());
        product.setPrice(request.getPrice());
        product.setDescription(request.getDescription());
        product.setType(request.getType());
        product.setMaterial(request.getMaterial());
        return product;
    }

    public Product mapUpdateToProduct(ProductUpdate request, Product product) {
        product.setName(request.getName());
        product.setPrice(request.getPrice());
        product.setDescription(request.getDescription());
        product.setType(request.getType());
        product.setMaterial(request.getMaterial());
        return product;
    }

}
