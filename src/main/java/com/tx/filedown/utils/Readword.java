package com.tx.filedown.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.poi.POIXMLDocument;
import org.apache.poi.hwpf.HWPFDocument;
import org.apache.poi.hwpf.extractor.WordExtractor;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.xwpf.extractor.XWPFWordExtractor;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFTable;
import org.springframework.util.FileCopyUtils;

public class Readword {


    /**
     * 字符串转unicode
     *
     * @param str
     * @return
     */
    public static String stringToUnicode(String str) {
        StringBuffer sb = new StringBuffer();
        char[] c = str.toCharArray();
        for (int i = 0; i < c.length; i++) {
            sb.append("\\u" + Integer.toHexString(c[i]));
        }
        return sb.toString();
    }

    public static String returnword(String filePath) {
        // TODO Auto-generated method stub
        //String filePath="D:\\aaa.docx";
        StringBuffer wordMapStr=new StringBuffer();

        Map wordMap = new LinkedHashMap();//创建一个map对象存放word中的内容
        try {
            if(filePath.endsWith(".doc")){ ///判断文件格式
                InputStream fis = new FileInputStream(new File(filePath));
                WordExtractor wordExtractor = new WordExtractor(fis);//使用HWPF组件中WordExtractor类从Word文档中提取文本或段落
                int i=1;
                for(String words : wordExtractor.getParagraphText()){//获取段落内容
                    //System.out.println(words);//.replaceAll("", "")
                    wordMap.put("DOC文档，第（"+i+"）段内容",words);
                    wordMapStr.append(words.replaceAll("", "")+"\n");
                    i++;
                }
                fis.close();
            }
            if(filePath.endsWith(".docx")){
                File uFile = new File("tempFile.docx");//创建一个临时文件
                if(!uFile.exists()){
                    uFile.createNewFile();
                }
                FileCopyUtils.copy(new File(filePath), uFile);//复制文件内容
                OPCPackage opcPackage = POIXMLDocument.openPackage("tempFile.docx");//包含所有POI OOXML文档类的通用功能，打开一个文件包。
                XWPFDocument document = new XWPFDocument(opcPackage);//使用XWPF组件XWPFDocument类获取文档内容
                List<XWPFParagraph> paras = document.getParagraphs();
                int i=1;
                for(XWPFParagraph paragraph : paras){
                    String words = paragraph.getText();
                    System.out.println(words);
                    wordMap.put("DOCX文档，第（"+i+"）段内容",words);
                    wordMapStr.append(words);
                    i++;
                }
                List<XWPFTable> it = document.getTables();

                it.forEach(item->{
                    wordMapStr.append(item.getText());
                });
                uFile.delete();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("-->"+wordMapStr.toString());
        // System.out.println(wordMap);
        return wordMapStr.toString();
    }
}
