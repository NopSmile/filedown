package com.tx.filedown.rest;

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
import java.net.Inet4Address;
import java.net.UnknownHostException;


import com.tencentcloudapi.common.Credential;
import com.tencentcloudapi.common.profile.ClientProfile;
import com.tencentcloudapi.common.profile.HttpProfile;
import com.tencentcloudapi.common.exception.TencentCloudSDKException;

import com.tencentcloudapi.ocr.v20181119.OcrClient;

import com.tencentcloudapi.ocr.v20181119.models.GeneralBasicOCRRequest;
import com.tencentcloudapi.ocr.v20181119.models.GeneralBasicOCRResponse;


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
        try {
                uploadFile(file.getBytes(), filePath, file.getOriginalFilename());
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("文件上传失败!");
            return "redirect:/";
        }
        System.out.println("文件名 "+file.getOriginalFilename()+" 文件上传 "+filePath+" 成功!");
        if(1==ocr){
            try{

                Credential cred = new Credential("AKIDNbdS3QOWFnYEh258sSt9HCuPhleJt6B3", "rV3SecNvGOI0iHdmhPt9Tkw4871rZ9cx");

                HttpProfile httpProfile = new HttpProfile();
                httpProfile.setEndpoint("ocr.tencentcloudapi.com");

                ClientProfile clientProfile = new ClientProfile();
                clientProfile.setHttpProfile(httpProfile);

                OcrClient client = new OcrClient(cred, "ap-beijing", clientProfile);

                String params = "{\"ImageUrl\":\"http://39.105.47.147:52111/ocrdata/"+file.getOriginalFilename()+"\"}";
                GeneralBasicOCRRequest req = GeneralBasicOCRRequest.fromJsonString(params, GeneralBasicOCRRequest.class);

                GeneralBasicOCRResponse resp = client.GeneralBasicOCR(req);

                System.out.println(GeneralBasicOCRRequest.toJsonString(resp));

                model.addAttribute("fileName",file.getOriginalFilename());
                model.addAttribute("path",filePath);
                model.addAttribute("imgpath","http://"+ Inet4Address.getLocalHost().getAddress()+":52118/pic/"+file.getOriginalFilename());
                model.addAttribute("result",GeneralBasicOCRRequest.toJsonString(resp));
            } catch (TencentCloudSDKException e) {
                System.out.println(e.toString());
            }
        }else{
            model.addAttribute("fileName",file.getOriginalFilename());
            model.addAttribute("path",filePath);
            model.addAttribute("imgpath","http://"+ Inet4Address.getLocalHost().getAddress()+":52118/pic/"+file.getOriginalFilename());
            model.addAttribute("result","没有开启转换ocr 请开启后在测试");
        }
        System.out.println(filePath+file.getOriginalFilename());
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
