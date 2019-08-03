package com.jttj.midtv;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.widget.Toast;

import java.util.HashMap;
import java.util.List;

/**
 * @content: Author: gjt66888
 * Description:
 * Time: 2019/6/7
 */
public class StrUtils {

    public static String TAG = "mid.jks";
    public static final String MidiTitleID = "MidiTitleID";
    public static final String MidiTitleName = "MidiTitleName";
    public static Toast mToast;

    // TODO: 2018/10/29 修改了内存泄漏的问题
    public static void showToast(String msg) {
        showToast(msg, Toast.LENGTH_SHORT);
    }

    public static void showToast(String msg, int duration) {
        if (mToast == null) {
            mToast = Toast.makeText(MyApp.getContext(), "", duration);
        }
        mToast.setText(msg);
        mToast.show();
    }

    public static String getPath(Context context, Uri uri) {

        if ("content".equalsIgnoreCase(uri.getScheme())) {
            String[] projection = {"_data"};
            Cursor cursor = null;

            try {
                cursor = context.getContentResolver().query(uri, projection, null, null, null);
                int column_index = cursor.getColumnIndexOrThrow("_data");
                if (cursor.moveToFirst()) {
                    return cursor.getString(column_index);
                }
            } catch (Exception e) {
                // Eat it
            }
        } else if ("file".equalsIgnoreCase(uri.getScheme())) {
            return uri.getPath();
        }

        return null;
    }

    /**
     * 集合的非空判断
     */
    public static boolean isListEmpty(List list) {
        if (list == null) {
            return true;
        }
        return list.size() == 0;
    }

    /**
     * 集合的非空判断
     */
    public static boolean isHashEmpty(HashMap<String, Integer> list) {
        if (list == null) {
            return true;
        }
        return list.size() == 0;
    }

    public static String bytesToHexString(byte[] src) {
        StringBuilder stringBuilder = new StringBuilder();
        if (src == null || src.length <= 0) {
            return null;
        }
        for (int i = 0; i < src.length; i++) {
            int v = src[i] & 0xFF;
            String hv = Integer.toHexString(v);
            if (hv.length() < 2) {
                stringBuilder.append(0);
            }
            stringBuilder.append(hv);
        }
        return stringBuilder.toString();
    }
}
