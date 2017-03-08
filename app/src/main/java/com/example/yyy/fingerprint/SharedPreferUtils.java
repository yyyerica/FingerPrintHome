package com.example.yyy.fingerprint;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.util.Base64;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.StreamCorruptedException;

public class SharedPreferUtils {
    public static final String MY_PREFERENCE = "pf";  
  
    private static void paraCheck(SharedPreferences sp, String key) {  
        if (sp == null) {  
            throw new IllegalArgumentException();  
        }  
        if (TextUtils.isEmpty(key)) {
            throw new IllegalArgumentException();  
        }  
    }  
  
    public static boolean putBitmap(Context context, String key, Bitmap bitmap) {
        SharedPreferences sp = context.getSharedPreferences(MY_PREFERENCE,
                Context.MODE_PRIVATE);
  
        paraCheck(sp, key);  
        if (bitmap == null || bitmap.isRecycled()) {  
            return false;  
        } else {  
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
            String imageBase64 = new String(Base64.encode(baos.toByteArray(),
                    Base64.DEFAULT));  
            SharedPreferences.Editor e = sp.edit();
            e.putString(key, imageBase64);  
            return e.commit();  
        }  
    }  
  
    public static Bitmap getBitmap(Context context, String key,  
            Bitmap defaultValue) {  
        SharedPreferences sp = context.getSharedPreferences(MY_PREFERENCE,  
                Context.MODE_PRIVATE);  
  
        paraCheck(sp, key);  
        String imageBase64 = sp.getString(key, "");  
        if (TextUtils.isEmpty(imageBase64)) {  
            return defaultValue;  
        }  
  
        byte[] base64Bytes = Base64.decode(imageBase64.getBytes(),  
                Base64.DEFAULT);  
        ByteArrayInputStream bais = new ByteArrayInputStream(base64Bytes);
        Bitmap ret = BitmapFactory.decodeStream(bais);
        if (ret != null) {  
            return ret;  
        } else {  
            return defaultValue;  
        }  
    }  
  
    public static boolean putDrawable(Context context, String key, Drawable d) {
        SharedPreferences sp = context.getSharedPreferences(MY_PREFERENCE,  
                Context.MODE_PRIVATE);  
        paraCheck(sp, key);  
        ByteArrayOutputStream baos = new ByteArrayOutputStream();  
        ((BitmapDrawable) d).getBitmap()
                .compress(Bitmap.CompressFormat.JPEG, 50, baos);
        String imageBase64 = new String(Base64.encode(baos.toByteArray(),  
                Base64.DEFAULT));  
        SharedPreferences.Editor e = sp.edit();
        e.putString(key, imageBase64);  
        return e.commit();  
    }  
  
    public static Drawable getDrawable(Context context, String key,  
            Drawable defaultValue) {  
        SharedPreferences sp = context.getSharedPreferences(MY_PREFERENCE,  
                Context.MODE_PRIVATE);  
        paraCheck(sp, key);  
        String imageBase64 = sp.getString(key, "");  
        if (TextUtils.isEmpty(imageBase64)) {  
            return defaultValue;  
        }  
  
        byte[] base64Bytes = Base64.decode(imageBase64.getBytes(),  
                Base64.DEFAULT);  
        ByteArrayInputStream bais = new ByteArrayInputStream(base64Bytes);  
        Drawable ret = Drawable.createFromStream(bais, "");  
        if (ret != null) {  
            return ret;  
        } else {  
            return defaultValue;  
        }  
    }  
  
    public static boolean putObject(Context context, String key, Object obj) {  
        SharedPreferences sp = context.getSharedPreferences(MY_PREFERENCE,  
                Context.MODE_PRIVATE);  
        paraCheck(sp, key);  
        if (obj == null) {  
            SharedPreferences.Editor e = sp.edit();
            e.putString(key, "");  
            return e.commit();  
        } else {  
            ByteArrayOutputStream baos = new ByteArrayOutputStream();  
            ObjectOutputStream oos = null;
            try {  
                oos = new ObjectOutputStream(baos);  
                oos.writeObject(obj);  
            } catch (IOException e1) {
                e1.printStackTrace();  
                return false;  
            }  
            String objectBase64 = new String(Base64.encode(baos.toByteArray(),  
                    Base64.DEFAULT));  
            SharedPreferences.Editor e = sp.edit();
            e.putString(key, objectBase64);  
            return e.commit();  
        }  
    }  
  
    public static Object getObject(Context context, String key,  
            Object defaultValue) {  
        SharedPreferences sp = context.getSharedPreferences(MY_PREFERENCE,  
                Context.MODE_PRIVATE);  
        paraCheck(sp, key);  
        String objectBase64 = sp.getString(key, "");  
        if (TextUtils.isEmpty(objectBase64)) {  
            return defaultValue;  
        }  
        byte[] base64Bytes = Base64.decode(objectBase64.getBytes(),  
                Base64.DEFAULT);  
        ByteArrayInputStream bais = new ByteArrayInputStream(base64Bytes);  
        ObjectInputStream ois;  
        try {  
            ois = new ObjectInputStream(bais);
            return ois.readObject();  
        } catch (StreamCorruptedException e) {
            // e.printStackTrace();  
            return null;  
        } catch (IOException e) {  
            // e.printStackTrace();  
            return null;  
        } catch (ClassNotFoundException e) {  
            // e.printStackTrace();  
            return null;  
        }  
    }  
  
    public static boolean isObjectEqual(Context context, String key,  
            Object newValue) {  
        SharedPreferences sp = context.getSharedPreferences(MY_PREFERENCE,  
                Context.MODE_PRIVATE);  
        paraCheck(sp, key);  
        if (newValue == null) {  
            return false;  
        } else {  
            ByteArrayOutputStream baos = new ByteArrayOutputStream();  
            ObjectOutputStream oos = null;  
            try {  
                oos = new ObjectOutputStream(baos);  
                oos.writeObject(newValue);  
            } catch (IOException e1) {  
                e1.printStackTrace();  
                return false;  
            }  
            String newValueBase64 = new String(Base64.encode(  
                    baos.toByteArray(), Base64.DEFAULT));  
            String oldValueBase64 = sp.getString(key, "");  
            return newValueBase64.equals(oldValueBase64);  
        }  
    }  
}  