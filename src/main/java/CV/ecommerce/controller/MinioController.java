package CV.ecommerce.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import CV.ecommerce.service.MinioService;

@RestController
@RequestMapping("/api/image")
public class MinioController {

    private final MinioService minioService;

    public MinioController(MinioService minioService) {
        this.minioService = minioService;
    }

    @PostMapping("/upload")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'STAFF')")
    public String uploadFile(@RequestParam("file") MultipartFile file) {
        return minioService.uploadFile(file);
    }

    @GetMapping("/{fileName}")
    public ResponseEntity<String> getFileUrl(@PathVariable String fileName) {
        String fileUrl = minioService.getFileUrl(fileName);
        return ResponseEntity.ok(fileUrl);
    }
}
