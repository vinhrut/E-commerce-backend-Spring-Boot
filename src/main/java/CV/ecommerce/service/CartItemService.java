package CV.ecommerce.service;

import CV.ecommerce.dto.request.cart.CartItemCreate;
import CV.ecommerce.dto.request.cart.CartItemFilterRequest;
import CV.ecommerce.entity.Cart;
import CV.ecommerce.entity.CartItem;
import CV.ecommerce.entity.Product;
import CV.ecommerce.exception.AppException;
import CV.ecommerce.repository.CartItemRepository;
import CV.ecommerce.repository.ProductRepository;
import CV.ecommerce.validation.CartValidator;
import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CartItemService {

    private final CartItemRepository cartItemRepository;
    private final CartService cartService;
    private final ProductRepository productRepository;
    private final CartValidator cartValidator;

    public CartItem addToCart(CartItemCreate request) {
        cartValidator.validateCreateCartItem(request);

        Cart cart = cartService.getCartByUserId(request.getUserId());

        Product product = productRepository.findById(request.getProductId())
                .orElseThrow(() -> new AppException(404, "Không tìm thấy sản phẩm"));

        CartItem existingItem = cartItemRepository.findByCartIdAndProductIdAndSize(
                cart.getId(), product.getId(), request.getSize());

        if (existingItem != null) {
            int newQuantity = existingItem.getQuantity() + request.getQuantity();
            existingItem.setQuantity(newQuantity);
            existingItem.setTotalPrice(product.getPrice() * newQuantity);
            return cartItemRepository.save(existingItem);
        } else {
            CartItem newItem = CartItem.builder()
                    .cart(cart)
                    .product(product)
                    .quantity(request.getQuantity())
                    .size(request.getSize())
                    .totalPrice(product.getPrice() * request.getQuantity())
                    .build();
            return cartItemRepository.save(newItem);
        }
    }

    public List<CartItem> getCartItemsByUserId(String userId) {
        return cartItemRepository.findByCartUserId(userId);
    }

    public List<CartItem> getCartItemsByCartId(Long cartId) {
        return cartItemRepository.findByCartId(cartId);
    }

    public void deleteCartItem(Long cartItemId) {
        if (!cartItemRepository.existsById(cartItemId)) {
            throw new AppException(404, "Không tìm thấy cart item để xoá");
        }
        cartItemRepository.deleteById(cartItemId);
    }

    public void clearCart(Long cartId) {
        cartItemRepository.deleteByCartId(cartId);
    }

    public Page<CartItem> filterCartItems(CartItemFilterRequest filter, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);

        Specification<CartItem> spec = Specification.where(null);

        if (filter.getUserId() != null && !filter.getUserId().isBlank()) {
            spec = spec.and((root, query, cb) -> cb.equal(root.get("cart").get("user").get("id"), filter.getUserId()));
        }

        if (filter.getProductName() != null && !filter.getProductName().isBlank()) {
            spec = spec.and((root, query, cb) -> cb.like(cb.lower(root.get("product").get("name")),
                    "%" + filter.getProductName().toLowerCase() + "%"));
        }

        if (filter.getSize() != null && !filter.getSize().isBlank()) {
            spec = spec.and((root, query, cb) -> cb.equal(root.get("size"), filter.getSize()));
        }

        if (filter.getMinPrice() != null) {
            spec = spec.and((root, query, cb) -> cb.greaterThanOrEqualTo(root.get("totalPrice"), filter.getMinPrice()));
        }

        if (filter.getMaxPrice() != null) {
            spec = spec.and((root, query, cb) -> cb.lessThanOrEqualTo(root.get("totalPrice"), filter.getMaxPrice()));
        }

        return cartItemRepository.findAll(spec, pageable);
    }

}
