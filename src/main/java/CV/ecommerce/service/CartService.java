package CV.ecommerce.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import CV.ecommerce.entity.Cart;
import CV.ecommerce.exception.AppException;
import CV.ecommerce.repository.CartRepository;

@Service
@RequiredArgsConstructor
public class CartService {
    private final CartRepository cartRepository;

    public Cart getCartByUserId(String userId) {
        return cartRepository.findByUser_Id(userId).orElseThrow(() -> new AppException(404, "Không tìm thấy giỏ hàng"));
    }
}
