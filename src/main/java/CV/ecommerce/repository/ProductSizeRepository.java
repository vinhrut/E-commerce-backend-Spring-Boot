package CV.ecommerce.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import CV.ecommerce.entity.ProductSize;

@Repository
public interface ProductSizeRepository extends JpaRepository<ProductSize, Long> {

    // deleteAllByProductId
    void deleteAllByProductId(String productId);

    Optional<ProductSize> findByProductIdAndSizeName(String productId, String sizeName);

}
