package com.example.jitpacklibrary;

import android.util.Log;

import java.util.Arrays;
import java.util.List;

/**
 * Created by Administrator on 2017/4/1.
 */

public class LogUtil {

    private static boolean DEBUG_V = true;
    private static boolean DEBUG_D = true;
    private static boolean DEBUG_I = true;
    private static boolean DEBUG_W = true;
    private static boolean DEBUG_E = true;
    private static String tag = "kotlin==========>";

    public static void v(String tag, String msg) {
        if (DEBUG_V) {
            Log.v(tag, msg);
        }
    }

    public static void d(String tag, String msg) {
        if (DEBUG_D) {
            Log.d(tag, msg);
        }
    }

    public static void i(String tag, String msg) {
        if (DEBUG_I) {
            Log.i(tag, msg);
        }
    }

    public static void w(String tag, String msg) {
        if (DEBUG_W) {
            Log.w(tag, msg);
        }
    }
    public static void e(String msg) {
        //信息太长,分段打印
        //因为String的length是字符数量不是字节数量所以为了防止中文字符过多，
        //  把4*1024的MAX字节打印长度改为2001字符数
        if (DEBUG_E) {
            int max_str_length = 2001 - tag.length();
            //大于4000时
            while (msg.length() > max_str_length) {
                Log.e(tag, msg.substring(0, max_str_length));
                msg = msg.substring(max_str_length);
            }
            //剩余部分
            Log.e(tag, msg);
        }
    }

    public static void e(Object... msg) {


        if (DEBUG_E) {
            List<Object> strings = Arrays.asList(msg);
            StringBuilder stringBuilder = new StringBuilder();
            for (int i = 0; i < strings.size(); i++) {
                if (i > 0) {
                    stringBuilder.append("====>");
                }
                stringBuilder.append(strings.get(i));
            }
            e(stringBuilder.toString());
        }

    }
}
