package lava.bluepay.com.lavaapp;

import android.os.Environment;

import java.io.File;

/**
 * Created by bluepay on 2017/10/17.
 */

public class Config {

    public static final String bufFileEnd = "buf.jpg";

    //todo 待删除
    /** 当手机没插卡时的默认手机号   */
    public static final String defaultTelNum = "13418638286";

    public static final String API_HOST = "http://lava.BluePay.asia/";


    public static final String API_HOST_TEST = "http://192.168.4.210:8168";


    //todo 暂时写死,后面需要改
    public static final String APPID = "wsgYatEx";
    //todo app的密钥,后面需要改
    public static final String APPSALT = "1xVBuoHwM7b01PuS3DWFyiGkRn0rXDfN";


    public static final String PRODUCT_DIC = "LavaApp";
    public static final String PATH_APP_STORRAGE = Environment
            .getExternalStorageDirectory().getAbsolutePath()
            + File.separator+PRODUCT_DIC+File.separator;
    public static final String PHOTO_PATH = PATH_APP_STORRAGE + File.separator + "Pic"+ File.separator;




    /**
     * 请求各类数据接口（cateid参数）的类别枚举
     * RecyclerViewAdapter构造器（categoryId参数）的类别枚举
     */

    //图片类
    public static final int CategoryPhoto = 1;
    public static final int CategoryPhotoPopular = 4;
    public static final int CategoryPhotoPortray = 5;
    public static final int CategoryPhotoScenery = 6;
    //视频类
    public static final int CategoryVideo = 2;
    public static final int CategoryVideoPopular = 7;
    public static final int CategoryVideoFunny = 8;
    public static final int CategoryVideoSport = 9;
    //卡通类
    public static final int CategoryCartoon = 3;
    public static final int CategoryCartoonPopular = 10;
    public static final int CategoryCartoonFunny = 11;
    public static final int CategoryCartoonhorror = 12;


}
