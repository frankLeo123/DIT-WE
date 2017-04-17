package com.bupt.weeat.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

//加载pic，处理pics
public class BitmapUtils {
    private static Bitmap bitmap;


    public static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;
        if (height > reqHeight || width > reqWidth) {
            int heightRatio = (height / reqHeight);
            int widthRatio = (width / reqWidth);
           inSampleSize = (heightRatio > widthRatio) ? widthRatio : heightRatio;
        }
        return inSampleSize;
    }

    public static Bitmap compressBitmapFromFile(String srcPath, int reqWidth, int reqHeight) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(srcPath, options);
        int inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);
        options.inSampleSize = inSampleSize;
        options.inJustDecodeBounds = false;
        bitmap = BitmapFactory.decodeFile(srcPath, options);
        return bitmap;
    }

    public static String saveToCache(Context mContext, Bitmap bitmap) {
        String timeStamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINESE).format(new Date());
        String pathName = CacheUtils.getCacheDirectory(mContext, true, "image") + timeStamp + ".jpg";
        File file = new File(pathName);
        try {
            FileOutputStream out = new FileOutputStream(file);
            if (bitmap.compress(Bitmap.CompressFormat.JPEG, 80, out)) {
                out.flush();
                out.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return file.getAbsolutePath();
    }
}
