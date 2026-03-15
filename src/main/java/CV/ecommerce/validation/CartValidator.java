package CV.ecommerce.validation;

import CV.ecommerce.dto.request.cart.CartItemCreate;
import CV.ecommerce.entity.Product;
import CV.ecommerce.entity.ProductSize;
import CV.ecommerce.entity.User;
import CV.ecommerce.exception.AppException;
import CV.ecommerce.repository.ProductRepository;
import CV.ecommerce.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Component
@RequiredArgsConstructor
public class CartValidator {

    private final UserRepository userRepository;
    private final ProductRepository productRepository;

    public void validateCreateCartItem(CartItemCreate request) {
        // 1. Validate userId
        if (!StringUtils.hasText(request.getUserId())) {
            throw new AppException(1001, "userId can not be empty");
        }

        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new AppException(1001, "User not found"));

        if (user.isLockUser()) {
            throw new AppException(1001, "Account is locked, please contact admin");
        }

        // 2. Validate productId
        if (!StringUtils.hasText(request.getProductId())) {
            throw new AppException(1001, "productId can not be empty");
        }

        Product product = productRepository.findById(request.getProductId())
                .orElseThrow(() -> new AppException(1001, "Product not found"));

        if (product.isDeleted()) {
            throw new AppException(1001, "Product is deleted");
        }

        // 3. Validate quantity
        if (request.getQuantity() == null || request.getQuantity() <= 0) {
            throw new AppException(1001, "Quantity must be greater than 0");
        }

        // 4. Validate size và tồn kho
        String size = request.getSize();
        if (!StringUtils.hasText(size)) {
            throw new AppException(1001, "Size can not be empty");
        }

        ProductSize productSize = product.getSizes().stream()
                .filter(s -> s.getSizeName().equalsIgnoreCase(size))
                .findFirst()
                .orElseThrow(() -> new AppException(1001, "Size not found for product"));

        if (productSize.getQuantity() < request.getQuantity()) {
            throw new AppException(1001, "Quantity is out of stock");
        }
    }
}
