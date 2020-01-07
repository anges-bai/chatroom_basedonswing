package com.bittech.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * @Author: BaiMiao
 * @Date: 2020/1/7 10:25
 * @Description:封装所有公共操作，包括加载配置文件，json操作等
 */
public class CommUtil {
    private static final Gson GSON=new GsonBuilder().create();

    public static Properties loadProperties(String fileName){
        Properties properties=new Properties();
        InputStream in=CommUtil.class.getClassLoader()
                .getResourceAsStream(fileName);
        try {
            properties.load(in);
        }catch (IOException e){
            System.out.println("资源文件加载失败");
            e.printStackTrace();
            return null;
        }
        return properties;
    }
    //任意对象序列化为json字符串
    public static String object2Json(Object obj){
        
        return  GSON.toJson(obj);
    }
    //将json字符串反序列化为对象
    public static Object json2Object(String jsonStr,Class objClz){
        
        return GSON.fromJson(jsonStr,objClz);
    }

}
