package com.xson.common.utils;

/**
 * Created by xson on 2016/12/8.
 */


import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Environment;
import android.util.Log;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * <pre>
 *     author: Blankj
 *     blog  : http://blankj.com
 *     time  : 2016/8/11
 *     desc  : 文件相关工具类
 * </pre>
 */
public class FileUtils {
    private static int FILE_SIZE = 4*1024;
    private static String TAG = "FileUtil";
    /**
     * 将字符串写入文件
     *
     * @param filePath 文件路径
     * @param content  写入内容
     * @param append   是否追加在文件末
     * @return {@code true}: 写入成功<br>{@code false}: 写入失败
     */
    public static boolean writeFileFromString(String filePath, String content, boolean append) {
        return writeFileFromString(new File(filePath), content, append);
    }

    /**
     * 将字符串写入文件
     *
     * @param file    文件
     * @param content 写入内容
     * @param append  是否追加在文件末
     * @return {@code true}: 写入成功<br>{@code false}: 写入失败
     */
    public static boolean writeFileFromString(File file, String content, boolean append) {
        if (file == null || content == null) return false;

        // 如果存在，是文件则返回true，是目录则返回false
        try {
            if (!file.exists()) {
                file.createNewFile();
            }
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
//        if (!createOrExistsFile(file)) return false;
        BufferedWriter bw = null;
        try {
            bw = new BufferedWriter(new FileWriter(file, append));
            bw.write(content);
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        } finally {
            if (bw != null) {
                try {
                    bw.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
//            CloseUtils.closeIO(bw);
        }
    }

    public static boolean hasSdcard(){
        String status = Environment.getExternalStorageState();
        if(status.equals(Environment.MEDIA_MOUNTED)){
            return true;
        }
        return false;
    }

    public static boolean createPath(String path){
        File f = new File(path);
        if(!f.exists()){
            Boolean o = f.mkdirs();
            Log.i(TAG, "create dir:"+path+":"+o.toString());
            return o;
        }
        return true;
    }

    public static boolean exists(String file){
        return new File(file).exists();
    }

    public static File saveFile(String file, InputStream inputStream){
        File f = null;
        OutputStream outSm = null;

        try{
            f = new File(file);
            String path = f.getParent();
            if(!createPath(path)){
                Log.e(TAG, "can't create dir:"+path);
                return null;
            }

            if(!f.exists()){
                f.createNewFile();
            }

            outSm = new FileOutputStream(f);
            byte[] buffer = new byte[FILE_SIZE];
            while((inputStream.read(buffer)) != -1){
                outSm.write(buffer);
            }
            outSm.flush();
        }catch (IOException ex) {
            ex.printStackTrace();
            return null;

        }finally{
            try{
                if(outSm != null) outSm.close();
            }catch (IOException ex) {
                ex.printStackTrace();
            }
        }
        Log.v(TAG,"[FileUtil]save file:"+file+":"+Boolean.toString(f.exists()));

        return f;
    }

    public static Drawable getImageDrawable(String file){
        if(!exists(file)) return null;
        try{
            InputStream inp = new FileInputStream(new File(file));
            return BitmapDrawable.createFromStream(inp, "img");
        }catch (Exception ex){
            ex.printStackTrace();
        }
        return null;
    }
}
