package CV.ecommerce.controller;

import CV.ecommerce.dto.request.product.ProductCreate;
import CV.ecommerce.dto.request.product.ProductFilterRequest;
import CV.ecommerce.dto.request.product.ProductUpdate;
import CV.ecommerce.dto.response.APIResponse;
import CV.ecommerce.dto.response.ProductResponse;
import CV.ecommerce.entity.Product;
import CV.ecommerce.mapper.ProductMapper;
import CV.ecommerce.service.ProductService;
import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;

import java.util.ArrayList;
import java.util.List;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;
    private final ProductMapper productMapper;

    @PreAuthorize("hasAnyAuthority('ADMIN', 'STAFF')")
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public APIResponse<ProductResponse> createProduct(
            @ModelAttribute ProductCreate request,
            @RequestParam(value = "images", required = false) List<MultipartFile> images) {

        Product product = productService.createProduct(request, images);
        ProductResponse response = productMapper.mapToProductResponse(product);
        return new APIResponse<>(1000, "Create product success!", response);
    }

    @GetMapping
    public APIResponse<List<ProductResponse>> getAllProduct() {
        List<Product> products = productService.getAllProduct();
        List<ProductResponse> responses = new ArrayList<>();
        if (products.isEmpty())
            return new APIResponse<>(1001, "Get all product fail, no product !", responses);
        for (Product product : products) {
            responses.add(productMapper.mapToProductResponse(product));
        }
        return new APIResponse<>(1000, "Get all product success !", responses);
    }

    @PreAuthorize("hasAnyAuthority('ADMIN', 'STAFF')")
    @GetMapping("/deleted")
    public APIResponse<List<ProductResponse>> getProductDeleted() {
        List<Product> products = productService.getProductDeleted();
        List<ProductResponse> responses = new ArrayList<>();
        if (products.isEmpty())
            return new APIResponse<>(1001, "Get all product fail, no product !", responses);
        for (Product product : products) {
            responses.add(productMapper.mapToProductResponse(product));
        }
        return new APIResponse<>(1000, "Get all product success !", responses);
    }

    @GetMapping("/{id}")
    public APIResponse<ProductResponse> getProductById(@PathVariable String id) {
        ProductResponse product = productMapper.mapToProductResponse(productService.getProductById(id));
        if (product == null) {
            return new APIResponse<>(1001, "Get product fail, no product with id: " + id, null);
        }
        return new APIResponse<>(1000, "Get product success !", product);
    }

    @PreAuthorize("hasAnyAuthority('ADMIN', 'STAFF')")
    @PutMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public APIResponse<ProductResponse> updateProduct(
            @PathVariable String id,
            @ModelAttribute ProductUpdate request,
            @RequestParam(value = "images", required = false) List<MultipartFile> images) {

        Product product = productService.updateProduct(id, request, images);
        ProductResponse response = productMapper.mapToProductResponse(product);
        return new APIResponse<>(1000, "Update product success!", response);
    }

    @PreAuthorize("hasAnyAuthority('ADMIN', 'STAFF')")
    @DeleteMapping("/{id}")
    public APIResponse<ProductResponse> deleteProduct(@PathVariable String id) {
        productService.deleteProduct(id);
        return new APIResponse<>(1000, "Delete product success!", null);
    }

    @PreAuthorize("hasAnyAuthority('ADMIN', 'STAFF')")
    @PostMapping("/filter")
    public APIResponse<Page<ProductResponse>> filterProducts(
            @RequestBody ProductFilterRequest filter,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String direction) {

        Sort sort = direction.equalsIgnoreCase("asc") ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<Product> productPage = productService.filterProducts(filter, pageable);
        Page<ProductResponse> responsePage = productPage.map(productMapper::mapToProductResponse);

        return new APIResponse<>(1000, "Filtered products", responsePage);
    }

}
