package com.cjz;

import net.sourceforge.tess4j.ITesseract;
import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;

import java.io.File;

public class tess4jTest {

    public static void main(String[] args) throws Exception {
        //创建Tesseract对象

        ITesseract tesseract = new Tesseract();

        //设置语言包
        tesseract.setDatapath("D:\\MyTest\\Tess4jTest");
        tesseract.setLanguage("chi_sim");

        //扫描图片，获取文字
        String result = tesseract.doOCR(new File("D:\\MyTest\\Tess4jTest\\test.png"));
        System.out.println("扫描结果 = " + result);


    }
}
