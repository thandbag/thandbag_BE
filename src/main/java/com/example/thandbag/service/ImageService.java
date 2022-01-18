package com.example.thandbag.service;

import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.google.common.net.MediaType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;

// AWS S3에 게시글 작성시 이미지 업로드 기능
@Slf4j
@RequiredArgsConstructor
@Service
public class ImageService {

    private final AmazonS3Client amazonS3Client;

    // 버킷 이름 동적 할당
    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    // S3 업로드
    public String uploadFile(MultipartFile file) throws IOException {
        String origName = file.getOriginalFilename();
        // 확장자를 찾기 위한 코드
        final String ext = origName.substring(origName.lastIndexOf('.'));
        // 파일이름 암호화
        final String saveFileName = UUID.randomUUID().toString().replaceAll("-", "") + ext;

        return uploadImageToS3(file, saveFileName);
    }


    private String uploadImageToS3(MultipartFile file, String fileName) {
        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentType(String.valueOf(MediaType.ANY_IMAGE_TYPE));
        metadata.setContentLength(file.getSize());
        metadata.setCacheControl("max-age=31536000");

        try {
            final InputStream uploadImageFileInputStream = file.getInputStream();
            amazonS3Client.putObject(new PutObjectRequest(bucket,
                    fileName,
                    uploadImageFileInputStream,
                    metadata));
            uploadImageFileInputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return amazonS3Client.getUrl(bucket, fileName).toString();
    }

}
