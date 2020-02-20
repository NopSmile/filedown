package com.tx.filedown.utils;


import com.alibaba.fastjson.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class ocrMethod {

    public static String toTurn(String imageurl) {
        Map<String, Object> headers = new HashMap<String, Object>();
        headers.put("Content-Type", "application/x-www-form-urlencoded");
        Map<String, Object> postparams = new HashMap<String, Object>();
        postparams.put("method", "ocrService");// 固定参数
        postparams.put("url", imageurl);// 图⽚片完整URL，URL⻓长度不不超过1024字节，和img参数只能同时存在⼀一个。PS：如果您需要通过url进⾏行行访问，需要您考虑SSRF攻击的防护。
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
        String resultPost = HttpUtil.httpPost("http://47.92.239.98:80/ocrapidocker/ocrservice.json", headers, null, postparams, 60000, false);
        System.out.println(resultPost);
        if( 200 == JSONObject.parseObject(resultPost).getInteger("code")){
            resultPost=JSONObject.parseObject(resultPost).getJSONObject("data").getString("content");
        }
        return resultPost;
    }

}
