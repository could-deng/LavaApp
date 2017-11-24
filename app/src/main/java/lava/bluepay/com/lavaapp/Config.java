package lava.bluepay.com.lavaapp;

import android.os.Environment;

import java.io.File;
import java.util.HashMap;

/**
 * Created by bluepay on 2017/10/17.
 */

public class Config {
    public static final String sourceFileEnd = "r";
    public static final String bufFileEnd = "b";
    public static final int reCheckSubTimeSeparator = 2000;//发送短信成功后的查询订阅时间间隔
    public static final int maxCheckSubTimes = 4;//查询最大次数
    public static final int PerPageSize = 10;//每页的数量
    public static final int ViewPagerMaxSize = 10;//viewpager最多显示的张数
    public static final int requestTokenMaxTimes = 3;//请求失败时重复请求token的最大次数

    public static final String API_HOST_TEST = "http://203.151.201.170:21801";//http://192.168.4.210:8168//http://58.250.250.116:8168////http://120.76.73.92:8168
//    public static final String API_HOST_TEST = "http://120.76.73.92:8168";

    //todo 暂时写死,后面需要改
    public static final String APPID = "wsgYatEx";
    //todo app的密钥,后面需要改
    public static final String APPSALT = "1xVBuoHwM7b01PuS3DWFyiGkRn0rXDfN";


    public static final String PRODUCT_DIC = "LavaApp";
    public static final String PATH_APP_STORRAGE = Environment
            .getExternalStorageDirectory().getAbsolutePath()
            + File.separator+PRODUCT_DIC+File.separator;
    public static final String PHOTO_PATH = PATH_APP_STORRAGE + File.separator + "Pic"+ File.separator;

//    public static final String APATCH_PATH = PATH_APP_STORRAGE+ File.separator+"fix.apatch"; // 补丁文件名

//    public static final String Bug_PATH = PATH_APP_STORRAGE+ File.separator+"textBug.txt";


    public static final String db_path = "/data/data/lava.bluepay.com.lavaapp/databases";
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


    public static HashMap<String, String[]> mncs = new HashMap<>();

    public static void initMNC() {
        mncs.clear();
//        mncs.put("52005", new String[] { "TH", "dtac" });
//        mncs.put("52018", new String[] { "TH", "dtac" });
        mncs.put("52001", new String[] { "TH", "ais" });
        mncs.put("52003", new String[] { "TH", "ais" });
        mncs.put("52023", new String[] { "TH", "ais" });
//        mncs.put("52004", new String[] { "TH", "true" });
//        mncs.put("52025", new String[] { "TH", "true" });
//        mncs.put("52099", new String[] { "TH", "true" });
//        mncs.put("52000", new String[] { "TH", "true" });
//        mncs.put("52002", new String[] { "TH", "true" });
//        mncs.put("46000", new String[] { "CN", "chinamobile" });
//        mncs.put("46001", new String[] { "CN", "chinamobile" });
//        mncs.put("46002", new String[] { "CN", "chinamobile" });
//        mncs.put("46003", new String[] { "CN", "chinatelecom" });
//        mncs.put("46004", new String[] { "CN", "chinatelecom" });
//        mncs.put("46005", new String[] { "CN", "chinatelecom" });
//        mncs.put("46006", new String[] { "CN", "chinaunicom" });
//        mncs.put("46007", new String[] { "CN", "chinamobile" });
//        mncs.put("46009", new String[] { "CN", "chinaunicom" });
//        mncs.put("46011", new String[] { "CN", "chinatelecom" });
//        mncs.put("46020", new String[] { "CN", "chinamobile" });
//        mncs.put("45412", new String[] { "CN", "chinamobile" });
//        mncs.put("51001", new String[] { "ID", "indosat" });
//        mncs.put("51003", new String[] { "ID", "indosat" });
//        mncs.put("51021", new String[] { "ID", "indosat" });
//        mncs.put("51007", new String[] { "ID", "telkomsel" });
//        mncs.put("51010", new String[] { "ID", "telkomsel" });
//        mncs.put("51020", new String[] { "ID", "telkomsel" });
//        mncs.put("51020", new String[] { "ID", "telkomsel" });
//        mncs.put("51008", new String[] { "ID", "xl" });
//        mncs.put("51011", new String[] { "ID", "xl" });
//        mncs.put("51009", new String[] { "ID", "smartfren" });
//        mncs.put("51028", new String[] { "ID", "smartfren" });
//        mncs.put("51089", new String[] { "ID", "hutchison" });
//        mncs.put("45404", new String[] { "ID", "hutchison" });
//        mncs.put("45201", new String[] { "VN", "mobifone" });
//        mncs.put("45202", new String[] { "VN", "vinaphone" });
//        mncs.put("45204", new String[] { "VN", "viettel" });
//        mncs.put("45206", new String[] { "VN", "viettel" });
//        mncs.put("45208", new String[] { "VN", "viettel" });
//        mncs.put("45205", new String[] { "VN", "vietnammobile" });
//        mncs.put("45207", new String[] { "VN", "gmobile" });
//        mncs.put("41006", new String[] { "PAK", "telenor" });
    }
    //endregion==========手机相关资源====================
}
