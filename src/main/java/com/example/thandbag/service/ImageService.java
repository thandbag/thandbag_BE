package com.example.thandbag.service;

import com.amazonaws.AmazonClientException;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.transfer.TransferManager;
import com.amazonaws.services.s3.transfer.Upload;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

// AWS S3에 게시글 작성시 이미지 업로드 기능
@RequiredArgsConstructor
@Service
public class ImageService {

    private final AmazonS3Client amazonS3Client;

    // 버킷 이름 동적 할당
    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    // 버킷 주소 동적 할당
    @Value("${cloud.aws.s3.bucket.url}")
    private String defaultUrl;

    // S3 업로드
    public String uploadFile(MultipartFile file) {
        String origName = file.getOriginalFilename();
        String url;
        try {
            // 확장자를 찾기 위한 코드
            final String ext = origName.substring(origName.lastIndexOf('.'));
            // 파일이름 암호화
            final String saveFileName = getUuid() + ext;
            // 파일 객체 생성
            // System.getProperty => 시스템 환경에 관한 정보를 얻을 수 있다. (user.dir = 현재 작업 디렉토리를 의미함)
            File newFile = new File(System.getProperty("user.dir") + "/" + saveFileName);
//            File newFile = new File(file + saveFileName);
            // 파일 변환
            file.transferTo(newFile);
            // S3 파일 업로드
            uploadOnS3(saveFileName, newFile);
            // 주소 할당
            url = defaultUrl + "/" + saveFileName;
            System.out.println(url);
//          이게 안되는거 https://s3.ap-northeast-2.amazonaws.com/thandbag/4658cd87c5094e90ac2c0d8cbc17cd4a.png
//          이게 되는거  https://thandbag.s3.ap-northeast-2.amazonaws.com/4658cd87c5094e90ac2c0d8cbc17cd4a.png
            // 파일 삭제
            newFile.delete();
        } catch (StringIndexOutOfBoundsException | IOException e) {
            url = null;
        }
        return url;
    }

    // UUID 생성
    private static String getUuid() {
        return UUID.randomUUID().toString().replaceAll("-", "");
    }

    // S3 업로드
    private void uploadOnS3(final String findName, final File file) {
        // AWS S3 전송 객체 생성
        final TransferManager transferManager = new TransferManager(this.amazonS3Client);
        // 요청 객체 생성
        final PutObjectRequest request = new PutObjectRequest(bucket, findName, file);
        System.out.println("bucket : " + bucket);
        System.out.println("findName : " + findName);
        System.out.println("file : " + file);

        // 업로드 시도
        final Upload upload = transferManager.upload(request);

        try {
            upload.waitForCompletion();
        } catch (AmazonClientException amazonClientException) {
            System.out.println((amazonClientException.getMessage()));
        } catch (InterruptedException e) {
            System.out.println((e.getMessage()));
        }
    }

}

