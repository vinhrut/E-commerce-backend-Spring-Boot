package CV.ecommerce.repository;

import CV.ecommerce.entity.ShippingInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ShippingInfoRepository extends JpaRepository<ShippingInfo, Long> {
    List<ShippingInfo> findByUserId(String userId);

    List<ShippingInfo> findByUser_IdAndDeletedFalse(String userId);

    Optional<ShippingInfo> findByUserIdAndIsDefaultTrueAndDeletedFalse(String userId);

}
