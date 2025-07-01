package CV.ecommerce.mapper;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;

import CV.ecommerce.dto.response.CartItemResponse;
import CV.ecommerce.entity.CartItem;
import CV.ecommerce.service.MinioService;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class CartMapper {
    private final MinioService minioService;

    public CartItemResponse toCartItemResponse(CartItem cartItem) {
        List<String> imageUrls = new ArrayList<>();
        for (String imageUrl : cartItem.getProduct().getImages()) {
            imageUrls.add(minioService.getFileUrl(imageUrl));
        }

        return CartItemResponse.builder()
                .userId(cartItem.getCart().getUser().getId())
                .productId(cartItem.getProduct().getId())
                .quantity(cartItem.getQuantity())
                .size(cartItem.getSize())
                .totalPrice(cartItem.getTotalPrice())
                .images(imageUrls)
                .build();
    }
}
