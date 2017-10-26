package lava.bluepay.com.lavaapp;

import android.os.Environment;

import java.io.File;

/**
 * Created by bluepay on 2017/10/17.
 */

public class Config {

    public static final String bufFileEnd = "buf.jpg";
    public static final int PerPageSize = 10;//每页的数量
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


//    客户端设备id 1安卓pad 2安卓手机 3ios手机 4iospad
//     * versionid 设备的版本
    public static final int DeviceIdAndroidPhone = 2;
    public static final int AppVersionId =1;

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





    //region==========手机相关资源====================
    public static final String ERROR_C_BluePay_IMSI = "0000000000";



    public static int PhoneType;
    public static final int PhoneType_MTK = 0;	// MTK 5.0 以下
    public static final int PhoneType_SP1 = 1;		// 展讯1 5.0以下
    public static final int PhoneType_SPEXLA = 2; // 展讯2、猎户座、Lava 5.0以上
    public static final int PhoneType_QUALCOMM = 3; // 高通骁龙
    /**
     * 单卡手机
     */
    public static final byte MOBILE_PHONE_CARD = 5;
    /**
     * 未知手机
     */
    public static final byte MOBILE_PHONE_KNOWN = 6;

    public static byte MOBILE_PHONE_TYPE = MOBILE_PHONE_CARD;


    //endregion==========手机相关资源====================
}
