package com.running.utils;

import com.qiniu.android.utils.UrlSafeBase64;

import org.json.JSONObject;

import javax.crypto.Mac;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

/**
 * 获得七牛云的Token
 * Created by ldd on 2016/6/10.
 */
public class GetQiNiuYunToken {
    public static final String ENCODING = "UTF-8";
    public static final String MAC_NAME = "HmacSHA1";
    //AccessKey
    String accessKey = "Gs39wGlCk4vFruUGNn-8bmMIEymTEeS7lYAGhoGu";
    //SecretKey
    String secretKey = "poBJ1eYfzH8Au8s0HR1kPerbYi9Do3fSlan45n44";

    /**
     * @param name (要传入的空间名)
     * @return
     */
    public String getToken(String name) {
        JSONObject _json = new JSONObject();
        long _dataLine = System.currentTimeMillis() / 10 + 3600;
        try {
            //有效时间为1小时
            _json.put("deadline", _dataLine);
            _json.put("scope", name);
            String _encodedPutPolicy = UrlSafeBase64.encodeToString(_json.toString().getBytes());
            byte[] _sign = HMACSHA1Encrypt(_encodedPutPolicy,secretKey);
            String _encodeSign = UrlSafeBase64.encodeToString(_sign);
            String _uploadToken = accessKey+':'+_encodeSign+':'+_encodedPutPolicy;
            return _uploadToken;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * @param encryptText (被签名的字符串)
     * @param encryptKey (密钥)
     * @return
     * @throws Exception
     */
    public static byte[] HMACSHA1Encrypt(String encryptText, String encryptKey) throws Exception {
        byte[] data = encryptKey.getBytes(ENCODING);
        //根据给定的字节数组构造一个密钥，第二参数指定一个密钥算法的名称
        SecretKey secretKey = new SecretKeySpec(data, MAC_NAME);
        //生成一个指定Mac算法的Mac对象
        Mac mac = Mac.getInstance(MAC_NAME);
        //用给定密钥初始化Mac对象
        mac.init(secretKey);
        byte[] text = encryptText.getBytes(ENCODING);
        //完成Mac操作
        return mac.doFinal(text);
    }
}
