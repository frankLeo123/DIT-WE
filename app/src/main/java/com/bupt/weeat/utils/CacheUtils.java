package com.bupt.weeat.utils;

import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Environment;

import java.io.File;

import static android.os.Environment.MEDIA_MOUNTED;

//缓存类，感觉不用怎么看……
public class CacheUtils {
    private static final String EXTERNAL_STORAGE_PERMISSION = "android.permission.WRITE_EXTERNAL_STORAGE";

    private static boolean hasExternalStoragePermission(Context mContext) {
        int permission = mContext.checkCallingOrSelfPermission(EXTERNAL_STORAGE_PERMISSION);
        return permission == PackageManager.PERMISSION_GRANTED;
    }


    public static File getCacheDirectory(Context mContext, boolean isPreferExternal, String dirName) {
        File appCacheDir = null;
        if (isPreferExternal && MEDIA_MOUNTED
                .equals(Environment.getExternalStorageState()) && hasExternalStoragePermission(mContext)) {
            appCacheDir = getExternalCacheDir(mContext, dirName);
        }
        if (appCacheDir == null) {
            appCacheDir = mContext.getCacheDir();
        }
        return appCacheDir;
    }

    private static File getExternalCacheDir(Context mContext, String cacheType) {
        File dataDir = new File(new File(Environment.getExternalStorageDirectory(), "Android"), "data");
        //ExternalStorageDirectory()/Android/data
        File appCacheDir = new File(new File(dataDir, mContext.getPackageName()), "cache");
        return new File(appCacheDir, cacheType);
    }
}
