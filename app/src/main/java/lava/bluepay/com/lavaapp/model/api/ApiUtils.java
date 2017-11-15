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
    public static final int requestPhoneNum = 503;
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

    public static final int requestSendAnalyse = 40000;

    public static final int AnalyseStepStartApp = 1;
    public static final int AnalyseStepShowDialog = 2;
    public static final int AnalyseStepDialogSure = 3;
    public static final int AnalyseStepDialogCancel = 4;
    public static final int AnalyseStepSendSms = 5;



    //endregion=========不同数据请求的标示=====================

    //region=========http请求返回错误码=====================

    public static final int reqResSuccess = 200;
    public static final int reqResError = 400;
    public static final int reqResErrorException = 500;
    public static final int reqResErrorAuthFail = 1001;
    public static final int reqResErrorAuthError = 1002;
    public static final int reqResErrorParametersError = 1004;
    public static final int reqResErrorInBlack = 1005;
    public static final int reqResErrorHaveSub = 1006;
    public static final int reqResErrorHaveNotSub = 1007;
    public static final int reqResErrorHaveQue = 1008;
    public static final int reqResErrorIsNew = 1009;
    public static final int reqResErrorDevNotExist = 1010;
    public static final int reqResErrorCategeryNotExist = 1011;

//    200
//        完成请求
//    400
//        参数错误
//    500
//        系统出现异常,请联系开发人员
//    1001
//        鉴权失败
//    1002
//        鉴权失效
//    1004
//        必要参数错误
//    1005
//        用户在黑名单中
//    1006
//        用户已订阅
//    1007
//        用户未订阅
//    1008
//        用户已退订
//    1009
//        用户属于新用户，可以订阅
//    1010
//        设备号不存在
//    1011
//        分类ID不存在


    //endregion=========http请求返回错误码=====================


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

    /**
     * 行为统计url（post）
     * @return
     */
    public static String getAnalyseRequest(){
        return Config.API_HOST_TEST+"/v1/analyse.api";
    }

}
