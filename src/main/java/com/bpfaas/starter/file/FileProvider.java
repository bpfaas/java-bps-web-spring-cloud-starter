package com.bpfaas.starter.file;

import java.io.File;
import java.io.InputStream;
import java.util.List;

/**
 * 文件操作类
 */
public interface FileProvider {

    /**
     * 获取文件
     * @param fileName 文件名称
     * @return
     */
    String getFileUrl(String fileName);

    /**
     * 获取指定bucket下文件列表
     * @param bucketName
     * @param fileName
     * @return
     */
    String getFileUrl(String bucketName, String fileName);

    /**
     * 下载文件
     * @param fileName  文件名称
     * @return
     */
    InputStream downLoadFile(String fileName);

    /**
     * 下载指定的bucket下文件
     * @param bucketName
     * @param fileName
     * @return
     */
    InputStream downLoadFile(String bucketName, String fileName);

    /**
     *
     * @param fileName  文件名称
     * @param destFile  保存路径
     */
    void downLoadFile(String fileName, File destFile);

    /**
     * 下载指定的bucket下文件
     * @param bucketName
     * @param fileName
     * @param destFile
     */
    void downLoadFile(String bucketName, String fileName, File destFile);

    /**
     * 下载文件
     * @param bucketName
     * @param fileName
     * @param destFileName
     */
    void downLoadFile(String bucketName, String fileName, String  destFileName);

    /**
     * 复制文件
     * @param sourceFileName  源文件
     * @param targetFileName  目标文件
     * @return
     */
    String copyFile(String sourceFileName, String targetFileName);

    /**
     * 删除文件
     * @param fileName 文件名称
     */
    void deleteFile(String fileName);

    /**
     * 删除指定bucket下文件
     * @param bucketName
     * @param fileName
     */
    void deleteFile(String bucketName, String fileName);

    /**
     * 查询当前服务器上bucket列表
     * @return
     */
    List<String> listBuckets();

    /**
     * 判断bucket是否存在
     * @param bucketName
     * @return
     */
    boolean bucketExists(String bucketName);

    /**
     * 创建bucket
     * @param bucketName
     */
    void makeBucket(String bucketName);

    /**
     * 上传文件
     * @param fileName     上传文件名称
     * @param inputStream  文件流
     * @param size         文件大小
     * @param contentType  文件类型
     * @return
     */
    String uploadFile(String fileName, InputStream inputStream, Long size, String contentType);

    /**
     * 上传文件
     * @param sourceFile
     * @return
     */
    String uploadFile(File sourceFile);

    /**
     * 上传文件到指定bucket
     * @param bucketName   bucket名称
     * @param fileName     上传文件名称
     * @param inputStream  文件流
     * @param size         文件大小
     * @param contentType  文件类型
     * @return
     */
    String uploadFile(String bucketName, String fileName, InputStream inputStream, Long size, String contentType);

    /**
     * 上传文件
     * @param bucketName
     * @param sourceFile
     * @return
     */
    String uploadFile(String bucketName, File sourceFile);

}
