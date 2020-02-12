package com.tx.filedown.rest;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.FileOutputStream;
import java.util.Base64;
import java.util.Map;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Random;
import java.util.TreeMap;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import javax.xml.bind.DatatypeConverter;
/**
 * @author whoami
 * 音频文件转码回调服务
 */
@RestController
@Slf4j
@RequestMapping(value = "/word")
public class WordController {


    /**
     * base64加密
     */
    public static String strEncode(String str) {
        byte[] bstr = new byte[0]; //改成默认字符集
        try {
            bstr = str.getBytes("utf-8");
        } catch (UnsupportedEncodingException e) {
        }
        //byte[] bstr = str.getBytes();
        return Base64.getEncoder().encodeToString(bstr);
    }
    @PostMapping("/base64")
    public String baseGet(@RequestBody Map<String, String> canshu) throws Exception {
        return strEncode(canshu.get("list"));
    }

    @PostMapping("/add")
    public String queryMachineCheckByPage(@RequestBody Map<String, String> canshu) throws Exception {

        TreeMap<String, Object> params = new TreeMap<String, Object>();
        params.put("Nonce", new Random().nextInt(java.lang.Integer.MAX_VALUE));

        params.put("Timestamp", System.currentTimeMillis() / 1000);
        params.put("Region", "ap-beijing");
        params.put("SecretId", "AKIDNbdS3QOWFnYEh258sSt9HCuPhleJt6B3");
        params.put("Action", "CreateAsrVocab");
        params.put("Version", "2019-06-14");
        params.put("Name", "cus01");
        //方式1:批量
        /**
         * 转业干部|10
         * 转业|10
         * 专业干部|1
         */
        params.put("WordWeightStr", canshu.get("list"));

        String str2sign = getStringToSign("GET", "asr.tencentcloudapi.com", params);
        String signature = sign(str2sign, "rV3SecNvGOI0iHdmhPt9Tkw4871rZ9cx", "HmacSHA1");
        params.put("Signature", signature); // 公共参数

        return getUrl(params);
    }

    @PostMapping("/update")
    public String UpdateAsrVocab(@RequestBody Map<String, String> canshu) throws Exception {

        TreeMap<String, Object> params = new TreeMap<String, Object>();
        params.put("Nonce", new Random().nextInt(java.lang.Integer.MAX_VALUE));

        params.put("Timestamp", System.currentTimeMillis() / 1000);
        params.put("Region", "ap-beijing");
        params.put("SecretId", "AKIDNbdS3QOWFnYEh258sSt9HCuPhleJt6B3");
        params.put("Action", "UpdateAsrVocab");
        params.put("Version", "2019-06-14");
        params.put("Name", "cus01");
        params.put("VocabId", canshu.get("vocabid"));
        //方式1:批量
        /**
         * 转业干部|10
         * 转业|10
         * 专业干部|1
         */
        params.put("WordWeightStr", canshu.get("list"));

        String str2sign = getStringToSign("GET", "asr.tencentcloudapi.com", params);
        String signature = sign(str2sign, "rV3SecNvGOI0iHdmhPt9Tkw4871rZ9cx", "HmacSHA1");
        params.put("Signature", signature); // 公共参数

        return getUrl(params);
    }

    @RequestMapping("/get/{funCode}")
    public String GetAsrVocab(@PathVariable("funCode") String funCode) throws Exception {

        TreeMap<String, Object> params = new TreeMap<String, Object>();
        params.put("Nonce", new Random().nextInt(java.lang.Integer.MAX_VALUE));

        params.put("Timestamp", System.currentTimeMillis() / 1000);
        //params.put("Region", "ap-beijing");
        params.put("SecretId", "AKIDNbdS3QOWFnYEh258sSt9HCuPhleJt6B3");
        params.put("Action", "GetAsrVocab");
        params.put("Version", "2019-06-14");
        params.put("VocabId", funCode);

        String str2sign = getStringToSign("GET", "asr.tencentcloudapi.com", params);
        String signature = sign(str2sign, "rV3SecNvGOI0iHdmhPt9Tkw4871rZ9cx", "HmacSHA1");

        params.put("Signature", signature); // 公共参数
        System.out.println(getUrl(params));

        return getUrl(params);
    }

    @RequestMapping("/del/{funCode}")
    public String DelAsrVocab(@PathVariable("funCode") String funCode) throws Exception {

        TreeMap<String, Object> params = new TreeMap<String, Object>();
        params.put("Nonce", new Random().nextInt(java.lang.Integer.MAX_VALUE));

        params.put("Timestamp", System.currentTimeMillis() / 1000);
        //params.put("Region", "ap-beijing");
        params.put("SecretId", "AKIDNbdS3QOWFnYEh258sSt9HCuPhleJt6B3");
        params.put("Action", "DeleteAsrVocab");
        params.put("Version", "2019-06-14");
        params.put("VocabId", funCode);

        String str2sign = getStringToSign("GET", "asr.tencentcloudapi.com", params);
        String signature = sign(str2sign, "rV3SecNvGOI0iHdmhPt9Tkw4871rZ9cx", "HmacSHA1");

        params.put("Signature", signature); // 公共参数
        System.out.println(getUrl(params));

        return getUrl(params);
    }
    private final static String CHARSET = "UTF-8";

    public static String sign(String s, String key, String method) throws Exception {
        Mac mac = Mac.getInstance(method);
        SecretKeySpec secretKeySpec = new SecretKeySpec(key.getBytes("UTF-8"), mac.getAlgorithm());
        mac.init(secretKeySpec);
        byte[] hash = mac.doFinal(s.getBytes("UTF-8"));
        return DatatypeConverter.printBase64Binary(hash);
    }

    public static String getStringToSign(String method, String endpoint, TreeMap<String, Object> params) {
        StringBuilder s2s = new StringBuilder();
        s2s.append(method).append(endpoint).append("/?");

        for (String k : params.keySet()) {
            s2s.append(k).append("=").append(params.get(k).toString()).append("&");
        }
        return s2s.toString().substring(0, s2s.length() - 1);
    }
    public static String getUrl(TreeMap<String, Object> params) throws UnsupportedEncodingException {
        StringBuilder url = new StringBuilder("https://asr.tencentcloudapi.com/?");
        // 实际请求的url中对参数顺序没有要求
        for (String k : params.keySet()) {
            // 需要对请求串进行urlencode，由于key都是英文字母，故此处仅对其value进行urlencode
            url.append(k).append("=").append(URLEncoder.encode(params.get(k).toString(), CHARSET)).append("&");
        }
        return url.toString().substring(0, url.length() - 1);
    }
}
