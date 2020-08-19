package com.tx.filedown.rest;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.FileOutputStream;

/**
 * @author whoami
 * 音频文件转码回调服务
 */
@RestController
@Slf4j
@RequestMapping(value = "/file")
public class FileController {

    //处理文件上传
    @PostMapping("/uploading")
    public @ResponseBody String uploading(@RequestParam(value="filePath") String filePath,@RequestParam("file") MultipartFile file,
                                          HttpServletRequest request) {
        System.out.println("filePath--->"+filePath+" 文件名字:"+file.getOriginalFilename()+" 文件大小为:"+file.getSize()/1000+"kb");
        try {
            if(new File(filePath+file.getOriginalFilename()).exists()){
                return "file exists,uploading success";
            }else{
                uploadFile(file.getBytes(), filePath, file.getOriginalFilename());
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("文件上传失败!");
            return "uploading failure";
        }
        System.out.println("文件上传成功!");
        return "uploading success";
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
