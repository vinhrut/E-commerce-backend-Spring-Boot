package CV.ecommerce.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import CV.ecommerce.entity.CartItem;

@Repository
public interface CartItemRepository extends JpaRepository<CartItem, Long>, JpaSpecificationExecutor<CartItem> {
    CartItem findByCartIdAndProductIdAndSize(Long cartId, String productId, String size);

    List<CartItem> findByCartId(Long cartId);

    void deleteByCartId(Long cartId);

    List<CartItem> findByCartUserId(String userId);

    Page<CartItem> findByCartUserId(String userId, Pageable pageable);

    Page<CartItem> findByCartId(Long cartId, Pageable pageable);
}
