package CV.ecommerce.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import CV.ecommerce.dto.request.order.OrderFilterRequest;
import CV.ecommerce.dto.request.order.OrderStatusRequest;
import CV.ecommerce.dto.response.APIResponse;
import CV.ecommerce.dto.response.OrderResponse;
import CV.ecommerce.entity.Order;
import CV.ecommerce.mapper.OrderMapper;
import CV.ecommerce.service.OrderService;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {
    private final OrderService orderService;
    private final OrderMapper orderMapper;

    @PreAuthorize("#userId == authentication.name or hasAnyAuthority('ADMIN', 'STAFF')")
    @PostMapping("/{userId}")
    public APIResponse<OrderResponse> createOrder(
            @PathVariable String userId,
            @RequestParam(required = false) Long shippingInfoId) {
        return orderService.createOrder(userId, shippingInfoId);
    }

    @PreAuthorize("#userId == authentication.name or hasAnyAuthority('ADMIN', 'STAFF')")
    @GetMapping("/user/{userId}")
    public APIResponse<List<OrderResponse>> getOrdersByUser(@PathVariable String userId) {
        List<Order> orders = orderService.getOrdersByUserId(userId);
        List<OrderResponse> orderResponses = new ArrayList<>();
        for (Order order : orders) {
            orderResponses.add(orderMapper.toOrderResponse(order));
        }
        return new APIResponse<>(1000, "Get orders by user id success !", orderResponses);
    }

    @PreAuthorize("hasAnyAuthority('ADMIN', 'STAFF')")
    @PutMapping("/status/{orderId}")
    public APIResponse<OrderResponse> updateOrderStatus(
            @PathVariable String orderId,
            @RequestBody OrderStatusRequest request) {
        Order updatedOrder = orderService.updateOrderStatus(orderId, request.getStatus());
        OrderResponse orderResponse = orderMapper.toOrderResponse(updatedOrder);
        return new APIResponse<>(1000, "Update order status success !", orderResponse);
    }

    @PreAuthorize("@securityService.isOrderOwner(#orderId) or hasAnyAuthority('ADMIN', 'STAFF')")
    @GetMapping("/{orderId}")
    public APIResponse<OrderResponse> getOrderById(@PathVariable String orderId) {
        Order order = orderService.getOrderById(orderId);
        OrderResponse orderResponse = orderMapper.toOrderResponse(order);
        return new APIResponse<>(1000, "Get order by id success !", orderResponse);
    }

    @PostMapping("/filter")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'STAFF')")
    public APIResponse<?> filterOrdersPost(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestBody OrderFilterRequest filter) {
        return new APIResponse<>(1000, "Filter orders success !",
                orderService.filterOrders(filter, page, size));
    }

}
