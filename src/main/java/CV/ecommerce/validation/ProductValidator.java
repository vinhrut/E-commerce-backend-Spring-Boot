package CV.ecommerce.validation;

import CV.ecommerce.dto.request.product.ProductCreate;
import CV.ecommerce.dto.request.product.ProductUpdate;
import CV.ecommerce.exception.AppException;
import CV.ecommerce.exception.ErrorCode;
import org.springframework.stereotype.Component;

@Component
public class ProductValidator {

    public void validateCreate(ProductCreate request) {
        if (request.getName() == null || request.getName().trim().isEmpty()) {
            throw new AppException(901, "Product name must not be empty");
        }

        if (request.getPrice() == null || request.getPrice() <= 0 || request.getPrice().isNaN()) {
            throw new AppException(902, "Product price must be greater than 0");
        }

        if (request.getDescription() == null || request.getDescription().trim().isEmpty()) {
            throw new AppException(ErrorCode.NO_DESCRIPTION.getCode(), "Product description must not be empty");
        }

        if (request.getType() == null || request.getType().trim().isEmpty()) {
            throw new AppException(903, "Product type must not be empty");
        }

        if (request.getMaterial() == null || request.getMaterial().trim().isEmpty()) {
            throw new AppException(904, "Product material must not be empty");
        }

        if (request.getSizes() == null || request.getSizes().trim().isEmpty()) {
            throw new AppException(905, "Product sizes must not be empty");
        }
    }

    public void validateUpdate(ProductUpdate request) {

        if (request.getName() == null || request.getName().trim().isEmpty()) {
            throw new AppException(910, "Product name must not be empty");
        }

        if (request.getPrice() == null || request.getPrice() <= 0 || request.getPrice().isNaN()) {
            throw new AppException(911, "Product price must be greater than 0");
        }

        if (request.getDescription() == null || request.getDescription().trim().isEmpty()) {
            throw new AppException(912, "Product description must not be empty");
        }

        if (request.getType() == null || request.getType().trim().isEmpty()) {
            throw new AppException(913, "Product type must not be empty");
        }

        if (request.getMaterial() == null || request.getMaterial().trim().isEmpty()) {
            throw new AppException(914, "Product material must not be empty");
        }

        if (request.getSizes() == null || request.getSizes().trim().isEmpty()) {
            throw new AppException(915, "Product sizes must not be empty");
        }
    }
}
