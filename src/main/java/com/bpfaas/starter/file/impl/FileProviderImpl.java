package com.bpfaas.starter.file.impl;

import com.bpfaas.common.exception.BpRuntimeException;
import com.bpfaas.starter.file.FileProvider;
import com.bpfaas.starter.file.WebMinioProperties;
import io.minio.*;
import io.minio.messages.Bucket;
import org.apache.commons.io.IOUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * 文件操作类
 */
public class FileProviderImpl implements FileProvider {

    private MinioClient minioClient;

    private String bucketName;

    private String bucketUrl;

    private String endPoint;

    public FileProviderImpl(WebMinioProperties properties) {
        this.minioClient = new MinioClient.Builder().
                endpoint(properties.getEndPoint()).credentials(properties.getAccessKey(), properties.getSecurityKey()).build();
        this.bucketName = properties.getBucketName();
        this.endPoint = properties.getEndPoint();
        if (properties.getEndPoint().endsWith("/")) {
            this.bucketUrl = properties.getEndPoint() + properties.getBucketName();
        }else {
            this.bucketUrl = properties.getEndPoint() + "/" + properties.getBucketName();
        }
    }

    @Override
    public String getFileUrl(String fileName) {
        return getFileUrl(this.bucketName, fileName);
    }

    @Override
    public String getFileUrl(String bucketName, String fileName) {
        try {
            return minioClient.getObjectUrl(bucketName, fileName);
        } catch (Exception e) {
            throw new BpRuntimeException("获取文件地址出错");
        }
    }

    @Override
    public InputStream downLoadFile(String fileName) {
        return downLoadFile(this.bucketName, fileName);
    }

    @Override
    public InputStream downLoadFile(String bucketName, String fileName) {
        try {
            GetObjectArgs args = GetObjectArgs.builder().bucket(bucketName).object(fileName).build();
            return minioClient.getObject(args);
        }catch (Exception e) {
            throw new BpRuntimeException("下载文件出错");
        }
    }

    @Override
    public void downLoadFile(String fileName, File destFile) {
        downLoadFile(this.bucketName, fileName, destFile);
    }

    @Override
    public void downLoadFile(String bucketName, String fileName, File destFile) {
        InputStream is = downLoadFile(bucketName, fileName);
        try {
            IOUtils.copy(is, new FileOutputStream(destFile));
        } catch (Exception e) {
            throw new BpRuntimeException("下载文件出错");
        }
    }

    @Override
    public void downLoadFile(String bucketName, String fileName, String destFileName) {
        downLoadFile(bucketName, fileName, new File(destFileName));
    }

    @Override
    public String copyFile(String sourceFileName, String targetFileName) {
        CopySource copySource = CopySource.builder().bucket(this.bucketName).object(sourceFileName).build();
        CopyObjectArgs args = CopyObjectArgs.builder().bucket(this.bucketName).source(copySource).object(targetFileName).build();
        try {
            minioClient.copyObject(args);
            return this.bucketUrl + "/" + targetFileName;
        } catch (Exception e) {
            throw new BpRuntimeException("下载文件出错");
        }
    }

    @Override
    public void deleteFile(String fileName) {
        deleteFile(this.bucketName, fileName);
    }

    @Override
    public void deleteFile(String bucketName, String fileName) {
        try {
            RemoveObjectArgs args = RemoveObjectArgs.builder().bucket(bucketName).object(fileName).build();
            minioClient.removeObject(args);
        } catch (Exception e) {
            throw new BpRuntimeException("删除文件出错");
        }
    }

    @Override
    public List<String> listBuckets() {
        List<String> bucketList = new ArrayList<>();
        try {
           List<Bucket> list = minioClient.listBuckets();
           list.forEach(bucket -> {
               bucketList.add(bucket.name());
           });
        } catch (Exception e) {
            throw new BpRuntimeException("获取buckets出错");
        }
        return bucketList;
    }

    @Override
    public boolean bucketExists(String bucketName) {
        try {
            BucketExistsArgs args = BucketExistsArgs.builder().bucket(bucketName).build();
            return minioClient.bucketExists(args);
        } catch (Exception e) {
            throw new BpRuntimeException("判断bucket是否存在出错");
        }
    }

    @Override
    public void makeBucket(String bucketName) {
        try {
            MakeBucketArgs args = MakeBucketArgs.builder().bucket(bucketName).build();
            minioClient.makeBucket(args);
        } catch (Exception e) {
            throw new BpRuntimeException("创建bucket出错");
        }
    }

    @Override
    public String uploadFile(String fileName, InputStream inputStream, Long size, String contentType) {
        return uploadFile(this.bucketName, fileName, inputStream, size, contentType);
    }

    @Override
    public String uploadFile(File sourceFile) {
        return uploadFile(this.bucketName, sourceFile);
    }

    @Override
    public String uploadFile(String bucketName, String fileName, InputStream inputStream, Long size, String contentType) {
        try {
            PutObjectArgs args = PutObjectArgs.builder().bucket(bucketName).object(fileName).stream(inputStream, size, -1).contentType(contentType).build();
            minioClient.putObject(args);
            String bucketUrl = null;
            if (endPoint.endsWith("/")) {
                bucketUrl = endPoint + bucketName;
            }else {
                bucketUrl = endPoint + "/" + bucketName;
            }
            return bucketUrl + "/" + fileName;
        } catch (Exception e) {
            e.printStackTrace();
            throw new BpRuntimeException("上传文件出错");
        }
    }

    @Override
    public String uploadFile(String bucketName, File sourceFile) {
        if (!sourceFile.exists()) {
            throw new BpRuntimeException("上传文件不存在");
        }
        try {
            InputStream is = new FileInputStream(sourceFile);
            return uploadFile(bucketName, sourceFile.getName(), is, Integer.valueOf(is.available()).longValue(), getContentType(sourceFile.getName()));
        } catch (Exception e) {
            throw new BpRuntimeException("上传文件出错");
        }
    }

    private String getContentType(String key) {
        key = key.toLowerCase();
        if (key.endsWith(".m3u8")){
            return "application/x-mpegURL";
        }

        if(key.endsWith(".ts")){
            return "video/MP2T";
        }

        if (key.endsWith(".bmp")) {
            return "image/bmp";
        }

        if (key.endsWith(".gif")) {
            return "image/gif";
        }

        if (key.endsWith(".jpeg") || key.endsWith(".jpg") || key.endsWith(".png")) {
            return "image/jpeg";
        }

        if (key.endsWith(".html")) {
            return "text/html";
        }

        if (key.endsWith(".txt")) {
            return "text/plain";
        }

        if (key.endsWith(".vsd")) {
            return "application/vnd.visio";
        }

        if (key.endsWith(".pptx") || key.endsWith(".ppt")) {
            return "application/vnd.ms-powerpoint";
        }

        if (key.endsWith(".docx") || key.endsWith(".doc")) {
            return "application/msword";
        }

        if (key.endsWith(".xml")) {
            return "text/xml";
        }

        if (key.endsWith(".mp4")) {
            return "video/mpeg4";
        }

        if (key.endsWith(".mp3")) {
            return "audio/mp3";
        }

        if (key.endsWith("flv")) {
            return "video/x-flv";
        }

        if (key.endsWith("jar")) {
            return "application/java-archive";
        }

        if (key.endsWith("zip")) {
            return "application/zip";
        }
        return "application/octet-stream";
    }

}
