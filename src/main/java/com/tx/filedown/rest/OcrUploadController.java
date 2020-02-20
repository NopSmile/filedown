package com.tx.filedown.rest;

import com.tx.filedown.utils.HttpUtil;
import com.tx.filedown.utils.Readword;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.FileOutputStream;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Controller
public class OcrUploadController {
    @Value("${file.upload.path}")
    private String filePath;

    @Value("${ocr}")
    private int ocr;

    @GetMapping("/")
    public String uploladPage(){
        return "index";
    }

    //47.92.107.98 研发环境
    private static final String imageurl="http://47.92.107.98:52118/pic/";

    @PostMapping("/")
    public String uploading(@RequestParam("file") MultipartFile file, HttpServletRequest request, Model model) throws UnknownHostException {

        String filename=UUID.randomUUID().toString().replaceAll("-","")+file.getOriginalFilename().substring(file.getOriginalFilename().lastIndexOf("."),file.getOriginalFilename().length());
        String afterType=file.getOriginalFilename().substring(file.getOriginalFilename().lastIndexOf(".")+1,file.getOriginalFilename().length());
        try {
                uploadFile(file.getBytes(), filePath, filename);
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("文件上传失败!");
            return "redirect:/";
        }
        System.out.println("文件名 "+filename+" 文件上传 "+filePath+" 成功!");
        if(1==ocr){
            switch (afterType) {
                case "doc":
                    System.out.println("doc");
                    long startdoc = System.currentTimeMillis();
                    String resultworddoc=Readword.returnword(filePath+filename);
                    long enddoc = System.currentTimeMillis() - startdoc;
                    System.out.println(resultworddoc);
                    model.addAttribute("time","文件格式为："+afterType+"的  word转换耗时: "+enddoc+" ms");
                    model.addAttribute("path",filePath);
                    model.addAttribute("imgpath",imageurl+filename);
                    model.addAttribute("result",resultworddoc);
                    break;
                case "docx":
                    System.out.println("docx");
                    long startdocx = System.currentTimeMillis();
                    String resultworddocx=Readword.returnword(filePath+filename);
                    long enddocx = System.currentTimeMillis() - startdocx;
                    System.out.println(resultworddocx);
                    model.addAttribute("time","文件格式为："+afterType+"的  word转换耗时: "+enddocx+" ms");
                    model.addAttribute("path",filePath);
                    model.addAttribute("imgpath",imageurl+filename);
                    model.addAttribute("result",resultworddocx);
                    break;
                case "pdf":
                    System.out.println("pdf");
                    //先切图片在转ocr 在拼接
                    break;
                default:
                    System.out.println("图片");
                    Map<String, Object> headers = new HashMap<String, Object>();
                    headers.put("Content-Type", "application/x-www-form-urlencoded");
                    Map<String, Object> postparams = new HashMap<String, Object>();
                    postparams.put("method", "ocrService");// 固定参数
                    postparams.put("url", imageurl+filename);// 图⽚片完整URL，URL⻓长度不不超过1024字节，和img参数只能同时存在⼀一个。PS：如果您需要通过url进⾏行行访问，需要您考虑SSRF攻击的防护。
                    //postparams.put("prob", "true");//是否需要置信度
                    //postparams.put("charInfo", "true");//是否需要单字输出
                    postparams.put("rotate", "true");//是否需要⾃自动旋转功能
                    /**
                     * page: 是否需要分⻚页功能
                     * paragraph: 是否需要分段功能
                     * row: 是否需要分⾏行行功能
                     * removeBoundary: 是否需要去除边界(对于包含多⻚页的图⽚片，去除边界的⻚页内容)
                     * noStamp: 是否去印章
                     * lowerP: 是否返回低识别率的字，如⼿手写体
                     * layout:版⾯面格式相关信息，⽬目前包含标题提取
                     * figure:是否需要图案（指纹和印章）坐标输出
                     * type:要识别的图⽚片类型，括号内表示需要传⼊入的参数值，包括⽂文档识
                     */
                    long startw = System.currentTimeMillis();

                    String resultPost = HttpUtil.httpPost("http://47.92.239.98:80/ocrapidocker/ocrservice.json", headers, null,
                            postparams, 60000, false);
                    long endw = System.currentTimeMillis() - startw;

                    System.out.println(resultPost);

                    model.addAttribute("time","文件格式为："+afterType+"的 图片转换耗时: "+endw+" ms");
                    model.addAttribute("path",filePath);
                    model.addAttribute("imgpath",imageurl+filename);
                    model.addAttribute("result",resultPost);
                    break;
            }
        }else{
            model.addAttribute("time","0 ms");
            model.addAttribute("path",filePath);
            model.addAttribute("imgpath",imageurl+filename);
            model.addAttribute("result","没有开启转换ocr 请开启后在测试");
        }
        System.out.println(filePath+filename+InetAddress.getLocalHost());
        return "index";
    }



    public void  uploadFile(byte[] file, String filePath, String fileName) throws Exception {
        File targetFile = new File(filePath);
        if(!targetFile.exists()){
            targetFile.mkdirs();
        }
        FileOutputStream out = new FileOutputStream(filePath+fileName);
        out.write(file);
        out.flush();
        out.close();
    }
}
