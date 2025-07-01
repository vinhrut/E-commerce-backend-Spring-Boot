package CV.ecommerce.repository;

import CV.ecommerce.entity.Order;
import CV.ecommerce.entity.User;
import CV.ecommerce.enums.OrderStatus;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, String>, JpaSpecificationExecutor<Order> {

    // Lấy tất cả đơn hàng theo người dùng
    List<Order> findByUser(User user);

    // Hoặc lấy theo userId nếu không dùng đối tượng User đầy đủ
    List<Order> findByUserId(String userId);

    // Tìm theo status
    List<Order> findByStatus(OrderStatus status);

    // Tìm theo user và trạng thái
    List<Order> findByUserAndStatus(User user, OrderStatus status);

    List<Order> findByUserIdOrderByCreatedAtDesc(String userId);

    @Query("SELECT COALESCE(SUM(o.totalAmount), 0) FROM Order o WHERE o.status = :status")
    double sumRevenueByStatus(@Param("status") OrderStatus status);

}
