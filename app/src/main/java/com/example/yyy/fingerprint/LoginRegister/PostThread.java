package com.example.yyy.fingerprint.LoginRegister;

import android.util.Log;

import org.json.JSONObject;

import java.net.URLEncoder;
import java.util.Random;


/**
 * Created by Administrator on 2015/4/28.
 */
public class PostThread {

    public static String decrypt(String encrypt) {
        MyEncript myEncript = null;
        try {
            myEncript = new MyEncript();
        } catch (Exception e) {
            e.printStackTrace();
        }
        myEncript.setPublicKey(Keys.CLIENT_PUBLIC_KEY);
        myEncript.setPrivateKey(Keys.CLIENT_PRIVATE_KEY);
        try {
            //encrypt = encrypt.replaceAll("\r|\n", "").replaceAll(" ", "").trim();
            JSONObject object = new JSONObject(encrypt);
            String text = object.getString("text");
            String password = object.getString("password");
            password = myEncript.decryptRSA(password, myEncript.getPrivateKey(), 2);
            password = password.replaceAll("\r|\n", "").replaceAll(" ", "").trim();
            text = myEncript.decryptDES(text, password);
            text = text.replaceAll("\r|\n", "").replaceAll(" ", "").trim();
            JSONObject jsonObject = new JSONObject(text);
            String character = jsonObject.getString("character");
            String source = jsonObject.getString("source");
            character = myEncript.decryptRSA(character, Keys.SERVER_PUBLIC_KEY, 1);
            character = character.replaceAll("\r|\n", "").replaceAll(" ", "").trim();
            String X = myEncript.SHA1(URLEncoder.encode(source,"utf-8"));
            Log.d("character", character);
            Log.d("X", X);
            Log.d("source", source);
            if(character.equals(X)) return source;
            else return "";
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    public static String encrypt(String source) {
        String result = "";
        try {
            source = source.replaceAll("\r", "").replaceAll("\n", "").replaceAll(" ", "").trim();
            MyEncript client = new MyEncript();
            client.setPublicKey(Keys.CLIENT_PUBLIC_KEY);
            client.setPrivateKey(Keys.CLIENT_PRIVATE_KEY);
            String X = client.SHA1(URLEncoder.encode(source,"utf-8"));
            String character = client.encryptRSA(X, client.getPrivateKey(), 2);
            Log.d("character", character);
            Log.d("X", X);
            String C = "{\"source\":\"" + source
                    + "\",\"character\":\"" + character + "\"}";
            String Q = generateString(8);
            String D = client.encryptDES(C, Q);
            String P = client.encryptRSA(Q, Keys.SERVER_PUBLIC_KEY, 1);
            Log.d("P", P);
            result = "&text=" + URLEncoder.encode(D, "utf-8") + "&password=" + URLEncoder.encode(P, "utf-8");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    public static String generateString(int length) {
        final String allChar = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
        StringBuffer sb = new StringBuffer();
        Random random = new Random();
        for (int i = 0; i < length; i++) {
            sb.append(allChar.charAt(random.nextInt(allChar.length())));
        }
        return sb.toString();
    }
}
