package CV.ecommerce.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import CV.ecommerce.dto.request.product.ProductCreate;
import CV.ecommerce.dto.request.product.ProductUpdate;
import CV.ecommerce.entity.Product;
import CV.ecommerce.entity.ProductSize;
import CV.ecommerce.exception.AppException;

@Component
public class SizeHandle {

    public void updateSizeForProduct(ProductUpdate request, Product product) {
        if (request.getSizes() != null && !request.getSizes().isBlank()) {
            try {
                ObjectMapper mapper = new ObjectMapper();
                List<ProductCreate.ProductSizeRequest> sizeRequests = mapper.readValue(
                        request.getSizes(),
                        new TypeReference<List<ProductCreate.ProductSizeRequest>>() {
                        });
                Map<String, ProductSize> existingSizes = product.getSizes().stream()
                        .collect(Collectors.toMap(ProductSize::getSizeName, size -> size));
                for (ProductCreate.ProductSizeRequest sr : sizeRequests) {
                    if (sr.getSizeName() != null && !sr.getSizeName().trim().isEmpty()) {
                        if (existingSizes.containsKey(sr.getSizeName())) {
                            if (sr.getQuantity() >= 0) {
                                existingSizes.get(sr.getSizeName()).setQuantity(sr.getQuantity());
                            } else {
                                throw new AppException(1003, "Size quantity must be greater than 0");
                            }
                        } else {
                            if (sr.getQuantity() >= 0) {
                                ProductSize newSize = new ProductSize();
                                newSize.setProduct(product);
                                newSize.setSizeName(sr.getSizeName().trim());
                                newSize.setQuantity(sr.getQuantity());
                                product.getSizes().add(newSize);
                            } else {
                                throw new AppException(1003, "Size quantity must be greater than 0");
                            }
                        }
                    }
                }
            } catch (Exception e) {
                throw new AppException(1002, "error update size");
            }
        }
    }

    public List<ProductSize> createSize(ProductCreate request, Product product) {
        List<ProductSize> sizes = new ArrayList<>();
        if (request.getSizes() != null && !request.getSizes().isBlank()) {
            try {
                ObjectMapper mapper = new ObjectMapper();
                List<ProductCreate.ProductSizeRequest> sizeRequests = mapper.readValue(
                        request.getSizes(),
                        new TypeReference<List<ProductCreate.ProductSizeRequest>>() {
                        });

                for (ProductCreate.ProductSizeRequest sr : sizeRequests) {
                    if (sr.getSizeName() != null && !sr.getSizeName().isBlank()) {
                        if (sr.getQuantity() > 0) {
                            ProductSize size = new ProductSize();
                            size.setProduct(product);
                            size.setSizeName(sr.getSizeName().trim());
                            size.setQuantity(sr.getQuantity());
                            sizes.add(size);
                        } else {
                            throw new AppException(1003, "Size quantity must be greater than 0");
                        }
                    }
                }
            } catch (Exception e) {
                throw new AppException(0002, "error create size");
            }
        }
        return sizes;
    }

}
