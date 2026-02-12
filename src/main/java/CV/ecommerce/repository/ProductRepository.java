package CV.ecommerce.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import CV.ecommerce.entity.Product;

@Repository
public interface ProductRepository extends JpaRepository<Product, String>, JpaSpecificationExecutor<Product> {
    // find All product By Deleted = false
    List<Product> findAllByDeletedFalse();

    // find All product By Deleted = true
    List<Product> findAllByDeletedTrue();

    long countByDeletedFalse();

    Page<Product> findAllByDeletedFalse(org.springframework.data.domain.Pageable pageable);

}
