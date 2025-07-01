package CV.ecommerce.controller;

import CV.ecommerce.dto.request.cart.CartItemCreate;
import CV.ecommerce.dto.request.cart.CartItemFilterRequest;
import CV.ecommerce.dto.response.APIResponse;
import CV.ecommerce.dto.response.CartItemResponse;
import CV.ecommerce.entity.CartItem;
import CV.ecommerce.mapper.CartMapper;
import CV.ecommerce.service.CartItemService;
import CV.ecommerce.service.SecurityService;
import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/cart-items")
@RequiredArgsConstructor
public class CartItemController {

    private final CartItemService cartItemService;
    private final CartMapper cartMapper;
    private final SecurityService securityService;

    @PreAuthorize("#request.userId == authentication.name or hasAnyAuthority('STAFF','ADMIN')")
    @PostMapping
    public APIResponse<CartItemResponse> addToCart(@RequestBody CartItemCreate request) {
        CartItem result = cartItemService.addToCart(request);
        CartItemResponse response = cartMapper.toCartItemResponse(result);
        return new APIResponse<>(1000, "Add to cart success !", response);
    }

    @PreAuthorize("@securityService.isCartOwner(#cartId) or hasAnyAuthority('STAFF','ADMIN')")
    @GetMapping("/{cartId}")
    public ResponseEntity<List<CartItem>> getCartItems(@PathVariable Long cartId) {
        List<CartItem> items = cartItemService.getCartItemsByCartId(cartId);
        return ResponseEntity.ok(items);
    }

    @PreAuthorize("@securityService.isCartOwner(#cartItemId) or hasAnyAuthority('STAFF','ADMIN')")
    @DeleteMapping("/{cartItemId}")
    public ResponseEntity<Void> deleteCartItem(@PathVariable Long cartItemId) {
        cartItemService.deleteCartItem(cartItemId);
        return ResponseEntity.noContent().build();
    }

    @PreAuthorize("@securityService.isCartOwner(#cartId) or hasAnyAuthority('STAFF','ADMIN')")
    @DeleteMapping("/cart/{cartId}")
    public ResponseEntity<Void> clearCart(@PathVariable Long cartId) {
        cartItemService.clearCart(cartId);
        return ResponseEntity.noContent().build();
    }

    @PreAuthorize("hasAnyAuthority('ADMIN', 'STAFF')")
    @PostMapping("/filter")
    public APIResponse<?> filterCartItems(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestBody CartItemFilterRequest filter) {

        Page<CartItem> result = cartItemService.filterCartItems(filter, page, size);

        Page<CartItemResponse> mapped = result.map(cartMapper::toCartItemResponse);

        return new APIResponse<>(1000, "Filter cart items success", mapped);
    }

}
