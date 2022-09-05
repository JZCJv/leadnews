package com.cjz.wemedia;


import com.heima.common.aliyun.GreenImageScan;
import com.heima.common.aliyun.GreenTextScan;
import com.heima.common.minio.MinIOFileStorageService;
import com.heima.wemedia.WemediaApplication;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = WemediaApplication.class)
public class AliyunTest {


    @Autowired
    private GreenImageScan greenImageScan;

    @Autowired
    private GreenTextScan greenTextScan;

    @Autowired
    private MinIOFileStorageService minIOFileStorageService;


    /**
     * 文字检测
     * @throws Exception
     */
    @Test
        public void testTextScan() throws Exception {
        List<String> list = new ArrayList<>();
        list.add("小明");
        list.add("广州");
        list.add("方便");
        list.add(null);
        Map result = greenTextScan.greeTextScan(list);

        String suggestion = (String) result.get("suggestion");
        System.out.println("suggestion = " + suggestion);


    }

    /**
     * 图片检测
     */
    @Test
    public void testImageScan() throws Exception {

        String url ="http://192.168.66.133:9000/leadnews/2022/09/03/9c96eabf-253b-45d8-b159-93aacb2ec551.jpg";

        ArrayList<byte[]> list = new ArrayList<>();
        byte[] file = minIOFileStorageService.downLoadFile(url);
        list.add(file);

        Map result = greenImageScan.imageScan(list);
        String suggestion = (String) result.get("suggestion");
        System.out.println("结果为 = " + suggestion);

    }
}
