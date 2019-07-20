package com.example.tinkertestapplication.utils;

import android.content.Context;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import java.io.File;
import java.lang.reflect.Array;
import java.util.HashSet;

import dalvik.system.DexClassLoader;
import dalvik.system.PathClassLoader;

public class FixDexUtils {
    private final static String TAG = "tinkerTest";
    //存放需要修复的dex集合，可能不止一个
    private static HashSet<File> loadedDex = new HashSet<>();
    static {
        loadedDex.clear();
    }

    /**
     * 加载热修复的dex文件
     * @param context 上下文
     */
    public static void loadFixedDex(Context context){
        if(context== null)return;
        //Dex文件目录（私有目录中，存在之前已经复制过来的修复包）
        File fileDir = context.getDir(Constants.DEX_DIR,Context.MODE_PRIVATE);
        File[] listFiles = fileDir.listFiles();
        //遍历私有目录中所有文件
        for (File file:listFiles){
            //找到修复包，加入到集合
            if(file.getName().endsWith(Constants.DEX_SUFFIX)
                    && !"classes.dex".equals(file.getName())){
                loadedDex.add(file);
                Log.d(TAG,"class file name = "+file.getAbsolutePath());
            }
        }
        Log.d(TAG,"start create DexClassLoader!!!!");
        //模拟类加载器
        createDexClassLoader(context,fileDir);
    }

    /**
     * 创建加载补丁的DexClassLoader
     * @param context context
     * @param fileDir Dex文件目录
     */
    private static void createDexClassLoader(Context context, File fileDir) {
        //创建临时的解压目录（先解压到该目录，在加载java
        String optimizedDir = fileDir.getAbsolutePath()+
                File.separator + "opt_dex";
        //不存在就创建
        File fopt = new File(optimizedDir);
        if (!fopt.exists()){
            fopt.mkdirs();
        }

        for (File dex:loadedDex){
            Log.d(TAG,"try hot fix =++"+ dex.getName());
            DexClassLoader classLoader = new DexClassLoader(dex.getAbsolutePath()
            ,optimizedDir,null,context.getClassLoader());
            hotFix(classLoader,context);
        }
    }

    private static void hotFix(DexClassLoader classLoader, Context context) {
        //获取系统PathClassLoader类加载器
        Log.d(TAG,"try hot fix =++");
        PathClassLoader pathClassLoader = (PathClassLoader) context.getClassLoader();
        try{
             //获取自有的dexElement数组对象
            Object myDexElements = ReflectUtils.getDexElements(ReflectUtils.getPathList(classLoader));
            Log.d(TAG,"try hot fix myDexElements.size = "+ Array.getLength(myDexElements));
            //获取系统的dexElement数组对象
            Object systemDexElements = ReflectUtils.getDexElements(ReflectUtils.getPathList( pathClassLoader));
            Log.d(TAG,"try hot fix systemDexElements.size = "+ Array.getLength(systemDexElements));
            //合并新的dexElement数组对象
            Object dexElements  = ArrayUtils.combineArray(myDexElements,systemDexElements);
            Log.d(TAG,"try hot fix dexElements.size = "+ Array.getLength(dexElements));
            //通过反射再去获取系统的dexPathList对象
            Object systemPathList = ReflectUtils.getPathList(pathClassLoader);
            //重新赋值给系统的pathList属性
            //修改了pathList中的dexElement数组对象
            ReflectUtils.setField(systemPathList,
                    systemPathList.getClass(),
                    dexElements);
            Log.d(TAG,"end hot fix");
        }catch(Exception e){
            Log.d(TAG,"error hot fix!!");
            e.printStackTrace();
        }

    }


    public static void startFix(Context conext){
        //修复包放到sd卡根目录中
        File sourceFile = new File(Environment.getExternalStorageDirectory()
                            ,Constants.DEX_NAME);
        String logStr = "source file name = "+sourceFile.getAbsolutePath()+" \n is source file exist="+sourceFile.exists();
        Log.d(TAG,logStr);
        Toast.makeText(conext,logStr,Toast.LENGTH_LONG).show();
        File targetFile = new File(conext.getDir(Constants.DEX_DIR,Context.MODE_PRIVATE)
                            .getAbsolutePath()+File.separator+Constants.DEX_NAME);
        //如果存在之前修复过的dex
        if(targetFile.exists()){
            targetFile.delete();
        }
        //将下载的修复包复制到目标文件夹
        try{
            FileUitls.copyFile(sourceFile,targetFile);
            Log.d(TAG,"target file name = "+targetFile.getAbsolutePath()+" \n is target file exist="+targetFile.exists());
            FixDexUtils.loadFixedDex(conext);
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
