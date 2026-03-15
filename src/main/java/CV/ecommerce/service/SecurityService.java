package CV.ecommerce.service;

import CV.ecommerce.repository.CartItemRepository;
import CV.ecommerce.repository.CartRepository;
import CV.ecommerce.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component("securityService")
@RequiredArgsConstructor
public class SecurityService {

    private final OrderRepository orderRepository;
    private final CartItemRepository cartItemRepository;
    private final CartRepository cartRepository;

    public boolean isOrderOwner(String orderId) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String currentUserId = auth.getName();

        return orderRepository.findById(orderId)
                .map(order -> order.getUser().getId().equals(currentUserId))
                .orElse(false);
    }

    /**
     * Kiểm tra xem user hiện tại có phải chủ của cartItem không.
     * Dùng cho DELETE /api/cart-items/{cartItemId}
     */
    public boolean isCartOwner(Long cartItemId) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String currentUserId = auth.getName();

        return cartItemRepository.findById(cartItemId)
                .map(cartItem -> cartItem.getCart().getUser().getId().equals(currentUserId))
                .orElse(false);
    }

    /**
     * Kiểm tra xem user hiện tại có phải chủ của Cart không (theo Cart entity ID).
     * Dùng cho DELETE /api/cart-items/cart/{cartId} (clearCart)
     */
    public boolean isCartByCartId(Long cartId) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String currentUserId = auth.getName();

        return cartRepository.findById(cartId)
                .map(cart -> cart.getUser().getId().equals(currentUserId))
                .orElse(false);
    }
}
