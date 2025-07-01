package CV.ecommerce.service;

import CV.ecommerce.dto.request.product.ProductCreate;
import CV.ecommerce.dto.request.product.ProductFilterRequest;
import CV.ecommerce.dto.request.product.ProductUpdate;
import CV.ecommerce.entity.Product;
import CV.ecommerce.entity.ProductSize;
import CV.ecommerce.exception.AppException;
import CV.ecommerce.mapper.ProductMapper;
import CV.ecommerce.repository.ProductRepository;
import CV.ecommerce.repository.ProductSizeRepository;
import CV.ecommerce.validation.ProductValidator;
import jakarta.persistence.criteria.Predicate;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProductService {

    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private ProductSizeRepository productSizeRepository;
    @Autowired
    private ProductValidator productValidator;
    @Autowired
    private SizeHandle sizeHandle;
    @Autowired
    private ProductMapper productMapper;
    @Autowired
    private ImageHandle imageHandle;

    public Product createProduct(ProductCreate request, List<MultipartFile> images) {
        // validate
        productValidator.validateCreate(request);

        Product product = productMapper.mapCreateToProduct(request);

        // Xu lu images
        List<String> imageUrls = new ArrayList<>();
        imageUrls = imageHandle.createImageURL(images);
        product.setImages(imageUrls);
        product = productRepository.save(product);

        // xu ly size
        List<ProductSize> sizes = new ArrayList<>();
        sizes = sizeHandle.createSize(request, product);
        productSizeRepository.saveAll(sizes);
        product.setSizes(sizes);

        return product;
    }

    public Product updateProduct(String id, ProductUpdate request, List<MultipartFile> images) {
        // validate
        productValidator.validateUpdate(request);

        Product product = getProductById(id);
        product = productMapper.mapUpdateToProduct(request, product);

        // xu ly images
        imageHandle.updateImageURL(images, product);
        // xu ly size
        sizeHandle.updateSizeForProduct(request, product);

        product.setUpdatedAt(LocalDateTime.now());
        return productRepository.save(product);
    }

    public void deleteProduct(String id) {
        Product product = getProductById(id);
        product.setDeleted(true);
        productRepository.save(product);
    }

    public List<Product> getAllProduct() {
        List<Product> products = productRepository.findAllByDeletedFalse();
        return products;
    }

    public List<Product> getProductDeleted() {
        List<Product> products = productRepository.findAllByDeletedTrue();
        return products;
    }

    public Product getProductById(String id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new AppException(404, "Product not found"));
        return product;
    }

    //////// PAGE/////////
    public Page<Product> filterProducts(ProductFilterRequest filter, Pageable pageable) {
        return productRepository.findAll((root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (filter.getName() != null && !filter.getName().isEmpty()) {
                predicates.add(cb.like(cb.lower(root.get("name")), "%" + filter.getName().toLowerCase() + "%"));
            }

            if (filter.getType() != null && !filter.getType().isEmpty()) {
                predicates.add(cb.equal(root.get("type"), filter.getType()));
            }

            if (filter.getMaterial() != null && !filter.getMaterial().isEmpty()) {
                predicates.add(cb.equal(root.get("material"), filter.getMaterial()));
            }

            if (filter.getMinPrice() != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("price"), filter.getMinPrice()));
            }

            if (filter.getMaxPrice() != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("price"), filter.getMaxPrice()));
            }

            predicates.add(cb.isFalse(root.get("deleted")));

            return cb.and(predicates.toArray(new Predicate[0]));
        }, pageable);
    }

}
