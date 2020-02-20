package com.tx.filedown.rest;

import com.tx.filedown.utils.HttpUtil;
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

    @PostMapping("/")
    public String uploading(@RequestParam("file") MultipartFile file, HttpServletRequest request, Model model) throws UnknownHostException {
        ///public MapRestResponse uploading(@RequestParam(value="filePath") String filePath, @RequestParam("file") MultipartFile file,HttpServletRequest request) {
        String filename=UUID.randomUUID().toString().replaceAll("-","")+file.getOriginalFilename().substring(file.getOriginalFilename().lastIndexOf("."),file.getOriginalFilename().length());
        try {
                uploadFile(file.getBytes(), filePath, filename);
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("文件上传失败!");
            return "redirect:/";
        }
        System.out.println("文件名 "+filename+" 文件上传 "+filePath+" 成功!");
        if(1==ocr){
                Map<String, Object> headers = new HashMap<String, Object>();
                headers.put("Content-Type", "application/x-www-form-urlencoded");

                Map<String, Object> postparams = new HashMap<String, Object>();

                postparams.put("method", "ocrService");// 固定参数
                postparams.put("url", "http://39.105.47.147:52118/pic/"+filename);// 图⽚片完整URL，URL⻓长度不不超过1024字节，和img参数只能同时存在⼀一个。PS：如果您需要通过url进⾏行行访问，需要您考虑SSRF攻击的防护。
                postparams.put("prob", "true");//是否需要置信度
                postparams.put("charInfo", "true");//是否需要单字输出
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

                String resultPost = HttpUtil.httpPost("http://47.92.239.98:80/ocrapidocker/ocrservice.json", headers, null,
                        postparams, 60000, false);

                System.out.println(resultPost);

                model.addAttribute("fileName",filename);
                model.addAttribute("path",filePath);
                model.addAttribute("imgpath","http://39.105.47.147:52118/pic/"+filename);
                model.addAttribute("result",resultPost);
        }else{
            model.addAttribute("fileName",filename);
            model.addAttribute("path",filePath);
            model.addAttribute("imgpath","http://39.105.47.147:52118/pic/"+filename);
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
