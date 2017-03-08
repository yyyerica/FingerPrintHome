package com.example.yyy.fingerprint.LoginRegister;

import java.io.ByteArrayOutputStream;
import java.security.Key;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import Decoder.BASE64Decoder;
import Decoder.BASE64Encoder;

/**
 * Created by admin on 2017/2/13.
 */
public class MyEncript {
    //指定key的大小
    private static int KEYSIZE = 1024;

    private String publicKey = "";
    private String privateKey = "";

    private static int MAXENCRYPTSIZE = 117;
    private static int MAXDECRYPTSIZE = 128;

    private static byte[] iv = {0x01, 0x02, 0x03, 0x04, 0x05, 0x06, 0x07, 0x08};

    public MyEncript() throws Exception {

    }

    public void generateKeyPair() throws Exception {
        //生成密钥对
        KeyPairGenerator kpg = KeyPairGenerator.getInstance("RSA");
        kpg.initialize(KEYSIZE, new SecureRandom());
        KeyPair kp = kpg.generateKeyPair();
        //通过密钥对分别得到公钥和私钥
        Key publicKeys = kp.getPublic();
        Key privateKeys = kp.getPrivate();
        publicKey = (new BASE64Encoder()).encodeBuffer(publicKeys.getEncoded());
        privateKey = (new BASE64Encoder()).encodeBuffer(privateKeys.getEncoded());
    }

    public PublicKey getPublicKey(String key) throws Exception {
        byte[] keyBytes;
        keyBytes = (new BASE64Decoder()).decodeBuffer(key);
        X509EncodedKeySpec keySpec = new X509EncodedKeySpec(keyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        PublicKey publicKey = keyFactory.generatePublic(keySpec);
        return publicKey;
    }

    public PrivateKey getPrivateKey(String key) throws Exception {
        byte[] keyBytes;
        keyBytes = (new BASE64Decoder()).decodeBuffer(key);
        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(keyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        PrivateKey privateKey = keyFactory.generatePrivate(keySpec);
        return privateKey;
    }

    //RSA加密方法
    public String encryptRSA(String data, String keys, int model) throws Exception {
        Key key;
        if (model == 1) {
            key = getPublicKey(keys);
        }
        else {
            key = getPrivateKey(keys);
        }
        byte[] source = data.getBytes("utf-8");
        //得到Cipher对象来实现对源数据的RSA加密
        Cipher cipher = Cipher.getInstance("RSA/ECB/NoPadding");
        cipher.init(Cipher.ENCRYPT_MODE, key);

        int length = source.length;
        int offset = 0;
        byte[] cache;
        ByteArrayOutputStream outStream = new ByteArrayOutputStream();
        int i = 0;
        while (length - offset > 0) {
            if (length - offset > MAXENCRYPTSIZE) {
                cache = cipher.doFinal(source, offset, MAXENCRYPTSIZE);
            } else {
                cache = cipher.doFinal(source, offset, length - offset);
            }
            outStream.write(cache, 0, cache.length);
            i++;
            offset = i * MAXENCRYPTSIZE;
        }

        String cryptograph = new BASE64Encoder().encode(outStream.toByteArray());
        return cryptograph;
    }

    //RSA解密方法
    public String decryptRSA(String cryptograph, String keys, int model) throws Exception {
        Key key;
        if (model == 1) {
            key = getPublicKey(keys);
        }
        else {
            key = getPrivateKey(keys);
        }
        byte[] encryptData = new BASE64Decoder().decodeBuffer(cryptograph);
        //对已经加密的数据进行RSA解密
        Cipher cipher = Cipher.getInstance("RSA/ECB/NoPadding");
        cipher.init(Cipher.DECRYPT_MODE, key);

        int length = encryptData.length;
        int offset = 0;
        int i = 0;
        byte[] cache;
        ByteArrayOutputStream outStream = new ByteArrayOutputStream();
        while (length - offset > 0) {
            if (length - offset > MAXDECRYPTSIZE) {
                cache = cipher.doFinal(encryptData, offset, MAXDECRYPTSIZE);
            } else {
                cache = cipher.doFinal(encryptData, offset, length - offset);
            }
            outStream.write(cache, 0, cache.length);
            i++;
            offset = i * MAXDECRYPTSIZE;
        }

        String source = new String(outStream.toByteArray(), "utf-8");
        return source;
    }

    public String SHA1(String decript) {
        try {
            MessageDigest digest = MessageDigest
                    .getInstance("SHA-1");
            digest.update(decript.getBytes());
            byte messageDigest[] = digest.digest();
            // Create Hex String
            StringBuffer hexString = new StringBuffer();
            // 字节数组转换为 十六进制 数
            for (int i = 0; i < messageDigest.length; i++) {
                String shaHex = Integer.toHexString(messageDigest[i] & 0xFF);
                if (shaHex.length() < 2) {
                    hexString.append(0);
                }
                hexString.append(shaHex);
            }
            return hexString.toString();

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return "";
    }

    public String encryptDES(String encryptString, String encryptKey) throws Exception {
        IvParameterSpec zeroIv = new IvParameterSpec(iv);
        SecretKeySpec key = new SecretKeySpec(encryptKey.getBytes("utf-8"), "DES");
        Cipher cipher = Cipher.getInstance("DES/CBC/PKCS5Padding");
        cipher.init(Cipher.ENCRYPT_MODE, key, zeroIv);
        byte[] encryptedData = cipher.doFinal(encryptString.getBytes("utf-8"));

        return new BASE64Encoder().encodeBuffer(encryptedData);
    }

    public String decryptDES(String decryptString, String decryptKey) throws Exception {
        byte[] byteMi = new BASE64Decoder().decodeBuffer(decryptString);
        IvParameterSpec zeroIv = new IvParameterSpec(iv);
        SecretKeySpec key = new SecretKeySpec(decryptKey.getBytes("utf-8"), "DES");
        Cipher cipher = Cipher.getInstance("DES/CBC/PKCS5Padding");
        cipher.init(Cipher.DECRYPT_MODE, key, zeroIv);
        byte decryptedData[] = cipher.doFinal(byteMi);

        return new String(decryptedData, "utf-8");
    }

    public String getPublicKey() {
        return publicKey;
    }

    public String getPrivateKey() {
        return privateKey;
    }

    public void setPublicKey(String publicKey) {
        this.publicKey = publicKey;
    }

    public void setPrivateKey(String privateKey) {
        this.privateKey = privateKey;
    }
}
