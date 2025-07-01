package CV.ecommerce.service;

import CV.ecommerce.dto.request.order.OrderFilterRequest;
import CV.ecommerce.dto.response.APIResponse;
import CV.ecommerce.dto.response.OrderResponse;
import CV.ecommerce.entity.*;
import CV.ecommerce.enums.OrderStatus;
import CV.ecommerce.mapper.OrderMapper;
import CV.ecommerce.repository.OrderRepository;
import CV.ecommerce.repository.ShippingInfoRepository;
import jakarta.persistence.criteria.Predicate;
import CV.ecommerce.repository.CartItemRepository;
import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final CartItemRepository cartItemRepository;
    private final CartItemService cartItemService;
    private final ShippingInfoRepository shippingInfoRepository;
    private final OrderMapper orderMapper;

    public APIResponse<OrderResponse> createOrder(String userId, Long shippingInfoId) {
        List<CartItem> cartItems = cartItemService.getCartItemsByUserId(userId);
        if (cartItems == null || cartItems.isEmpty()) {
            return new APIResponse<>(1002, "Cart is empty!", null);
        }

        User user = cartItems.get(0).getCart().getUser();

        ShippingInfo shippingInfo;

        if (shippingInfoId != null) {
            shippingInfo = shippingInfoRepository.findById(shippingInfoId)
                    .filter(info -> info.getUser().getId().equals(userId))
                    .orElse(null);

            if (shippingInfo == null) {
                return new APIResponse<>(1003, "Can not find shipping info!", null);
            } else if (Boolean.TRUE.equals(shippingInfo.getDeleted())) {
                return new APIResponse<>(1003, "Shipping info is deleted!", null);
            }
        } else {
            shippingInfo = shippingInfoRepository.findByUserIdAndIsDefaultTrueAndDeletedFalse(userId).orElse(null);

            if (shippingInfo == null) {
                return new APIResponse<>(1004, "Can not find default shipping info!", null);
            }
        }

        // Tạo order
        Order order = new Order();
        order.setUser(user);
        order.setStatus(OrderStatus.PENDING);
        order.setShippingInfo(shippingInfo);

        List<OrderItem> orderItems = cartItems.stream().map(cartItem -> OrderItem.builder()
                .product(cartItem.getProduct())
                .size(cartItem.getSize())
                .quantity(cartItem.getQuantity())
                .unitPrice(cartItem.getProduct().getPrice())
                .totalPrice(cartItem.getTotalPrice())
                .order(order)
                .build()).toList();
        order.setOrderItems(orderItems);

        double totalAmount = orderItems.stream()
                .mapToDouble(item -> item.getQuantity() * item.getUnitPrice())
                .sum();
        order.setTotalAmount(totalAmount);

        Order savedOrder = orderRepository.save(order);
        cartItemRepository.deleteAll(cartItems);

        OrderResponse orderResponse = orderMapper.toOrderResponse(savedOrder);
        return new APIResponse<>(1000, "Create order success!", orderResponse);
    }

    public List<Order> getOrdersByUserId(String userId) {
        return orderRepository.findByUserIdOrderByCreatedAtDesc(userId);
    }

    public Order updateOrderStatus(String orderId, OrderStatus newStatus) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("Can not find order" + orderId));
        order.setStatus(newStatus);
        order.setUpdatedAt(LocalDateTime.now());
        return orderRepository.save(order);
    }

    public Order getOrderById(String orderId) {
        return orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("Can not find order with id: " + orderId));
    }

    public Page<OrderResponse> filterOrders(OrderFilterRequest filter, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Specification<Order> spec = buildSpecification(filter);
        Page<Order> orders = orderRepository.findAll(spec, pageable);
        return orders.map(orderMapper::toOrderResponse);
    }

    private Specification<Order> buildSpecification(OrderFilterRequest filter) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (filter.getStatus() != null && !filter.getStatus().isEmpty()) {
                predicates.add(cb.equal(root.get("status"), OrderStatus.valueOf(filter.getStatus())));
            }

            if (filter.getUserId() != null && !filter.getUserId().isEmpty()) {
                predicates.add(cb.equal(root.get("user").get("id"), filter.getUserId()));
            }

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

            if (filter.getFromDate() != null && !filter.getFromDate().isEmpty()) {
                LocalDate from = LocalDate.parse(filter.getFromDate(), formatter);
                predicates.add(cb.greaterThanOrEqualTo(root.get("createdAt"), from.atStartOfDay()));
            }

            if (filter.getToDate() != null && !filter.getToDate().isEmpty()) {
                LocalDate to = LocalDate.parse(filter.getToDate(), formatter);
                predicates.add(cb.lessThanOrEqualTo(root.get("createdAt"), to.atTime(LocalTime.MAX)));
            }

            if (filter.getMinTotalAmount() != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("totalAmount"), filter.getMinTotalAmount()));
            }
            if (filter.getMaxTotalAmount() != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("totalAmount"), filter.getMaxTotalAmount()));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };

    }

}