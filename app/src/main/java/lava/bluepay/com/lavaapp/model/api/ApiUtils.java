package lava.bluepay.com.lavaapp.model.api;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import lava.bluepay.com.lavaapp.Config;
import lava.bluepay.com.lavaapp.MixApp;

/**
 * Created by bluepay on 2017/10/19.
 */

public class ApiUtils {

    //region=========不同数据请求的唯一标示=====================
    public static final int requestToken = 500;
    public static final int requestInit = 501;
    public static final int requestCheckSub = 502;
    public static final int requestAllCategory = 10000;

    public static final int requestPhotoPopular = 10001;//图片-流行
    public static final int requestPhotoPortray = 10002;
    public static final int requestPhotoScenery = 10003;

    public static final int requestVideoPopular = 20001;//视频-流行
    public static final int requestVideoFunny = 20002;
    public static final int requestVideoSport = 20003;

    public static final int requestCartoonPopular = 30001;//卡通-流行
    public static final int requestCartoonFunny = 30002;
    public static final int requestCartoonHorror = 30003;

    //endregion=========不同数据请求的标示=====================




    /**
     * 自定义数据请求异常状态码,表示网络不可用
     */
    public static final int HTTP_NETWORK_FAIL = 600;
    /**
     * 自定义数据请求异常状态码,表示http请求异常
     */
    public static final int HTTP_REQUEST_EXCEPTION = 700;
    /**
     * 自定义数据请求异常状态码,表示添加或更新据请求状态表失败
     */
    public static final int ADD_OR_UPDATE_REQ_STATUS_FAIL = 800;
    /**
     * 自定义数据请求异常状态码,表示请求的参数无效
     */
    public static final int HTTP_REQUEST_INPUT_INVALID = 900;


    private static ApiUtils instance;

    public ApiUtils() {
    }
    public static ApiUtils getInstance(){
        if(instance == null){
            instance = new ApiUtils();
        }
        return instance;
    }

    /**
     * 网络是否可用
     * @return
     */
    public static boolean isNetWorkAvailable(){
        ConnectivityManager cm = (ConnectivityManager) MixApp.getContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        if(cm !=null){
            NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
            return activeNetwork !=null && activeNetwork.isAvailable();
        }else{
            return false;
        }

    }







    /**
     * 拼接String
     * @param params
     * @return
     */
    public static String jointStrings(String... params){
        StringBuilder sb = new StringBuilder();
        for(String str:params){
            sb.append(str);
        }
        return sb.toString();
    }




    /**
     * 获取
     * (get)
     * @param appid
     * @param encrypt
     * @return
     */
    public static String getToken(String appid,String encrypt){
        return jointStrings(Config.API_HOST_TEST,
                "/v1/gettoken.api?appid=",appid,
                "&encrypt=",encrypt);
    }

    /**
     * 获取初始化url
     * (post)
     * @return
     */
    public static String getInit(){
        return Config.API_HOST_TEST+"/v1/getinit.api";
    }

    /**
     * 查询用户订阅状态
     * @return
     */
    public static String getCheckSub(){
        return Config.API_HOST_TEST+"/v1/checksub.api";
    }

    /**
     * 获取请求全部类别url
     * (post)
     * @return
     */
    public static String getCategoryList(){
        return Config.API_HOST_TEST+"/v1/category.api";
    }
    /**
     * 获取请求分页数据url
     * (get)
     * @return
     */
    public static String getQuerypage(int page,int pagesize,int cateid,String token){
        return  jointStrings(Config.API_HOST_TEST,
                "/v1/querypage.api?page=",String.valueOf(page),
                "&pagesize=",String.valueOf(pagesize),
                        "&cateid=",String.valueOf(cateid),
                "&token=",token,
                "&Content-Type=","application/json; charset=utf-8");
    }

}
