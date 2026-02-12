package CV.ecommerce.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import CV.ecommerce.entity.Cart;
import CV.ecommerce.exception.AppException;
import CV.ecommerce.repository.CartRepository;

import CV.ecommerce.repository.UserRepository;

@Service
@RequiredArgsConstructor
public class CartService {
    private final CartRepository cartRepository;
    private final UserRepository userRepository;

    public Cart getCartByUserId(String userId) {
        return cartRepository.findByUser_Id(userId).orElseGet(() -> {
            var user = userRepository.findById(userId)
                    .orElseThrow(() -> new AppException(404, "Không tìm thấy người dùng"));
            Cart newCart = new Cart();
            newCart.setUser(user);
            return cartRepository.save(newCart);
        });
    }
}
