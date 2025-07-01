package CV.ecommerce.service;

import io.minio.GetPresignedObjectUrlArgs;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.RemoveObjectArgs;
import io.minio.http.Method;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import CV.ecommerce.exception.AppException;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class MinioService {

    private final MinioClient minioClient;
    private static final String BUCKET_NAME = "ecommerce";

    public String uploadFile(MultipartFile file) {
        try {
            String fileName = System.currentTimeMillis() + "_" + file.getOriginalFilename();
            InputStream inputStream = file.getInputStream();
            minioClient.putObject(
                    PutObjectArgs.builder()
                            .bucket(BUCKET_NAME)
                            .object(fileName)
                            .stream(inputStream, file.getSize(), -1)
                            .contentType(file.getContentType())
                            .build());
            return fileName;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public String getFileUrl(String fileName) {
        try {
            return minioClient.getPresignedObjectUrl(
                    GetPresignedObjectUrlArgs.builder()
                            .bucket(BUCKET_NAME)
                            .object(fileName)
                            .method(Method.GET)
                            .expiry(1, TimeUnit.DAYS)
                            .build());
        } catch (Exception e) {
            throw new AppException(004, e.getMessage());
        }
    }

    public void deleteFile(String fileName) {
        try {
            minioClient.removeObject(
                    RemoveObjectArgs.builder()
                            .bucket(BUCKET_NAME)
                            .object(fileName)
                            .build());
            System.out.println("File deleted successfully: " + fileName);
        } catch (Exception e) {
            e.printStackTrace();
            throw new AppException(004, e.getMessage());
        }
    }

    public String uploadFile(String fileName, byte[] content, String contentType) {
        try {
            InputStream inputStream = new ByteArrayInputStream(content);

            minioClient.putObject(
                    PutObjectArgs.builder()
                            .bucket(BUCKET_NAME)
                            .object(fileName)
                            .stream(inputStream, content.length, -1)
                            .contentType(contentType)
                            .build());

            return fileName;
        } catch (Exception e) {
            e.printStackTrace();
            throw new AppException(500, "Upload PDF fail: " + e.getMessage());
        }
    }

}
