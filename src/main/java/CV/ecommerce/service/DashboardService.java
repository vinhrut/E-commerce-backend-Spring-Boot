package CV.ecommerce.service;

import CV.ecommerce.dto.response.DashboardSummaryResponse;
import CV.ecommerce.enums.OrderStatus;
import CV.ecommerce.repository.OrderItemRepository;
import CV.ecommerce.repository.OrderRepository;
import CV.ecommerce.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DashboardService {

    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final ProductRepository productRepository;

    public DashboardSummaryResponse getDashboardSummary() {
        long totalOrders = orderRepository.count();
        double totalRevenue = orderRepository.sumRevenueByStatus(OrderStatus.COMPLETED);
        long totalProducts = productRepository.countByDeletedFalse();
        long totalSoldItems = orderItemRepository.sumTotalSoldItems();

        return DashboardSummaryResponse.builder()
                .totalOrders(totalOrders)
                .totalRevenue(totalRevenue)
                .totalProducts(totalProducts)
                .totalSoldItems(totalSoldItems)
                .build();
    }
}
