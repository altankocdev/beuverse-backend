package com.altankoc.beuverse_backend.core.s3;

import com.altankoc.beuverse_backend.core.config.AwsProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.IOException;
import java.util.UUID;

@Slf4j
@Service
public class S3Service {

    @Autowired(required = false)
    private S3Client s3Client;

    @Autowired
    private AwsProperties awsProperties;

    public String uploadFile(MultipartFile file, String folder) {
        if (s3Client == null) {
            throw new RuntimeException("S3 servisi yapılandırılmamış!");
        }

        String fileName = folder + "/" + UUID.randomUUID() + "_" + file.getOriginalFilename();

        try {
            PutObjectRequest request = PutObjectRequest.builder()
                    .bucket(awsProperties.s3().bucket())
                    .key(fileName)
                    .contentType(file.getContentType())
                    .build();

            s3Client.putObject(request, RequestBody.fromBytes(file.getBytes()));

            String url = "https://" + awsProperties.s3().bucket()
                    + ".s3." + awsProperties.region()
                    + ".amazonaws.com/" + fileName;

            log.info("Dosya yüklendi: {}", url);
            return url;

        } catch (IOException e) {
            log.error("Dosya yükleme hatası: {}", e.getMessage());
            throw new RuntimeException("Dosya yüklenemedi!");
        }
    }

    public void deleteFile(String fileUrl) {
        if (s3Client == null) {
            log.warn("S3 servisi yapılandırılmamış, dosya silinemedi.");
            return;
        }

        try {
            String key = fileUrl.substring(fileUrl.indexOf(".amazonaws.com/") + 15);

            DeleteObjectRequest request = DeleteObjectRequest.builder()
                    .bucket(awsProperties.s3().bucket())
                    .key(key)
                    .build();

            s3Client.deleteObject(request);
            log.info("Dosya silindi: {}", key);

        } catch (Exception e) {
            log.error("Dosya silme hatası: {}", e.getMessage());
        }
    }
}