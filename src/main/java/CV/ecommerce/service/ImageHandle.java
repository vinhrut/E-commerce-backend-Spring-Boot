package CV.ecommerce.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import CV.ecommerce.entity.Product;
import CV.ecommerce.exception.AppException;

@Component
public class ImageHandle {
    @Autowired
    private MinioService minioService;

    public List<String> createImageURL(List<MultipartFile> images) {
        List<String> imageUrls = new ArrayList<>();
        if (images != null && !images.isEmpty() && images.stream().anyMatch(image -> !image.isEmpty())) {
            for (MultipartFile image : images) {
                if (!image.isEmpty()) {
                    String imageUrl = minioService.uploadFile(image);
                    imageUrls.add(imageUrl);
                }
            }
        } else {
            throw new AppException(1002, "Product images must not be empty");
        }
        return imageUrls;
    }

    public void updateImageURL(List<MultipartFile> images, Product product) {
        if (images != null && !images.isEmpty() && images.stream().anyMatch(image -> !image.isEmpty())) {
            List<String> imageUrls = product.getImages();
            for (String fileName : imageUrls) {
                minioService.deleteFile(fileName);
            }
            imageUrls.clear();
            for (MultipartFile image : images) {
                if (!image.isEmpty()) {
                    String imageUrl = minioService.uploadFile(image);
                    imageUrls.add(imageUrl);
                }
            }
            product.setImages(imageUrls);
        }
    }
}
