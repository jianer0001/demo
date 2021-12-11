package com.example.demo;

import com.github.tobato.fastdfs.domain.fdfs.StorePath;
import com.github.tobato.fastdfs.domain.proto.storage.DownloadByteArray;
import com.github.tobato.fastdfs.service.FastFileStorageClient;
import org.apache.commons.io.FilenameUtils;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.*;

@SpringBootTest
class SpringbootApplicationTest {

    @Autowired
    private FastFileStorageClient storageClient;

    /**
     * 文件上传
     */
    @Test
    public void uploadTest() {
        InputStream is = null;
        try {
            // 获取文件源
            File source = new File("/Users/jian/Desktop/11.png");
            // 获取文件流
            is = new FileInputStream(source);
            // 进行文件上传
            StorePath storePath = storageClient.uploadFile(is, source.length(), FilenameUtils.getExtension(source.getName()), null);
            // 获取文件的组
            String group = storePath.getGroup();
            // 获取文件的路径
            String path = storePath.getPath();
            // 获得文件上传后访问地址
            String fullPath = storePath.getFullPath();
            // 打印访问地址
            System.out.println("fullPath = " + fullPath);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } finally {
            try {
                if (is != null) {
                    // 关闭流资源
                    is.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 文件下载
     */
    @Test
    public void downloadTest() {
        // 文件访问地址
        String fullPath = "group1/M00/00/00/wKgAZmG0RDuAIr5AAAKkW3gj2VM689.png";
        // 分离文件分组
        String group = fullPath.substring(0, fullPath.indexOf("/"));
        // 分离文件路径
        String path = fullPath.substring(fullPath.indexOf("/") + 1);
        // 进行文件下载
        byte[] buffer = storageClient.downloadFile(group, path, new DownloadByteArray());
        // 创建输出文件源
        File target = new File("/Users/jian/Desktop/", "target" + fullPath.substring(fullPath.indexOf(".")));
        OutputStream os = null;
        try {
            // 获取文件输出字节流
            os = new FileOutputStream(target);
            // 将字节数组内容写入文件源
            os.write(buffer);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                // 关闭流资源
                if (os != null) {
                    os.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 文件删除
     */
    @Test
    public void deleteTest()
    {
        // 文件访问地址
        String fullPath = "group1/M00/00/00/wKgAZmG0RDuAIr5AAAKkW3gj2VM689.png";
        // 分离文件分组
        String group = fullPath.substring(0, fullPath.indexOf("/"));
        // 分离文件路径
        String path = fullPath.substring(fullPath.indexOf("/") + 1);
        // 进行文件删除
        storageClient.deleteFile(group, path);
    }
}
