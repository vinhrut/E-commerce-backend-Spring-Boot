package CV.ecommerce.mapper;

import CV.ecommerce.dto.response.OrderItemResponse;
import CV.ecommerce.dto.response.OrderResponse;
import CV.ecommerce.dto.response.ShippingInfoResponse;
import CV.ecommerce.entity.Order;
import CV.ecommerce.entity.OrderItem;
import CV.ecommerce.service.MinioService;
import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class OrderMapper {
    private final MinioService minioService;

    public OrderResponse toOrderResponse(Order order) {
        // B1: Tạo danh sách OrderItemResponse
        List<OrderItemResponse> itemResponses = new ArrayList<>();

        for (OrderItem item : order.getOrderItems()) {
            OrderItemResponse itemResponse = new OrderItemResponse();

            itemResponse.setProductName(item.getProduct().getName());
            itemResponse.setQuantity(item.getQuantity());
            itemResponse.setTotalPrice(item.getQuantity() * item.getUnitPrice());
            List<String> imageUrls = new ArrayList<>();
            for (String imageUrl : item.getProduct().getImages()) {
                imageUrls.add(minioService.getFileUrl(imageUrl));
            }
            itemResponse.setImageUrl(imageUrls);

            // Nếu có size lưu trong Product hoặc trong CartItem thì set ở đây
            itemResponse.setSizeName(item.getSize());

            itemResponses.add(itemResponse);
        }

        // B2: Tạo OrderResponse và set các trường
        OrderResponse response = new OrderResponse();
        response.setOrderId(order.getId());
        response.setStatus(order.getStatus().name()); // nếu status là Enum
        response.setTotalAmount(order.getTotalAmount());
        response.setCreatedAt(order.getCreatedAt());
        response.setItems(itemResponses);

        if (order.getShippingInfo() != null) {
            ShippingInfoResponse shippingInfoResponse = new ShippingInfoResponse();
            shippingInfoResponse.setFullName(order.getShippingInfo().getFullName());
            shippingInfoResponse.setPhone(order.getShippingInfo().getPhone());
            shippingInfoResponse.setProvince(order.getShippingInfo().getProvince());
            shippingInfoResponse.setDistrict(order.getShippingInfo().getDistrict());
            shippingInfoResponse.setWard(order.getShippingInfo().getWard());
            shippingInfoResponse.setStreetAddress(order.getShippingInfo().getStreetAddress());
            shippingInfoResponse.setNote(order.getShippingInfo().getNote());
            shippingInfoResponse.setDefault(order.getShippingInfo().getIsDefault());
            response.setShippingInfo(shippingInfoResponse);
        }

        return response;
    }
}
