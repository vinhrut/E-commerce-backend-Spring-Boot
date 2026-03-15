package CV.ecommerce.repository;

import CV.ecommerce.entity.User;

import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, String>, JpaSpecificationExecutor<User> {
    Optional<User> findByEmail(String email);

    boolean existsByEmail(String email);

    List<User> findByLockUser(boolean lockUser);

    Optional<User> findByPhone(String phone);

    boolean existsByRole(CV.ecommerce.enums.Role role);

    Page<User> findByLockUser(boolean lockUser, org.springframework.data.domain.Pageable pageable);

}
