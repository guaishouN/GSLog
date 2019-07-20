package com.guaishou.gslog;


import android.util.Log;

public class GSLog {
    private static String Tag;
    public static void setTag(String tag){
        if (null ==tag) return;
        Tag = tag;
    }

    public static void print(String msg){
        if (null ==msg) return;
        Log.d(Tag,msg);
    }
}