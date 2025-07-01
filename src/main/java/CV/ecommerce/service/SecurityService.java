package CV.ecommerce.service;

import CV.ecommerce.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component("securityService")
@RequiredArgsConstructor
public class SecurityService {

    private final OrderRepository orderRepository;

    public boolean isOrderOwner(String orderId) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String currentUserId = auth.getName();

        return orderRepository.findById(orderId)
                .map(order -> order.getUser().getId().equals(currentUserId))
                .orElse(false);
    }

}
