package CV.ecommerce.repository;

import CV.ecommerce.entity.Order;
import CV.ecommerce.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {

    Optional<Payment> findByTxnRef(String txnRef);

    boolean existsByTxnRef(String txnRef);

    Optional<Payment> findByOrder(Order order);

    Optional<Payment> findByOrderId(String orderId);

    List<Payment> findAllByStatusAndCreatedAtBefore(String status, LocalDateTime time);

    // Tìm theo Order và status
    Optional<Payment> findByOrderAndStatus(Order order, String status);
}
