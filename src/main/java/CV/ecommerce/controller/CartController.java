package CV.ecommerce.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import CV.ecommerce.dto.response.APIResponse;
import CV.ecommerce.dto.response.CartItemResponse;
import CV.ecommerce.entity.CartItem;
import CV.ecommerce.mapper.CartMapper;
import CV.ecommerce.service.CartService;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/cart")
@RequiredArgsConstructor
public class CartController {
    private final CartService cartService;

    private final CartMapper cartMapper;

    @GetMapping("/{userId}")
    @PreAuthorize("#userId == authentication.name or hasAnyRole('STAFF','ADMIN')")
    public APIResponse<List<CartItemResponse>> getCartByUserId(@PathVariable String userId) {
        List<CartItem> listCartItems = cartService.getCartByUserId(userId).getCartItems();
        List<CartItemResponse> cartItemResponses = new ArrayList<>();
        for (CartItem cartItem : listCartItems) {
            cartItemResponses.add(cartMapper.toCartItemResponse(cartItem));
        }
        return new APIResponse<>(1000, "Get cart by user id success !", cartItemResponses);
    }
}
