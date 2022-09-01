package com.heima.article;

import com.heima.common.minio.MinIOFileStorageService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.FileInputStream;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = ArticleApplication.class)
public class MinioTest {
    @Autowired
    private MinIOFileStorageService storageService;

    /**
     * 上传文件
     */
    @Test
    public void testUploadFile() throws Exception {
        FileInputStream inputStream = new FileInputStream("D:\\A\\ProjectTestFile\\abc.html");
        String url = storageService.uploadHtmlFile("", "abc.html", inputStream);
        System.out.println(url);
    }

    /**
     * 下载文件
     */
    @Test
    public void testDownloadFile() throws Exception {
        String url = "http://192.168.66.133:9000/leadnews/2022/09/01/abc.html";
        byte[] bytes = storageService.downLoadFile(url);
        System.out.println(bytes);
    }

    /**
     * 删除文件
     */
    @Test
    public void testDeleteFile() throws Exception {
        String url = "http://192.168.66.133:9000/leadnews/2022/09/01/abc.html";
        storageService.delete(url);
    }
}

