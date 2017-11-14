//package lava.bluepay.com.lavaapp.common;
//
//import android.content.Context;
//import android.text.TextUtils;
//import com.umeng.analytics.MobclickAgent;
//
///**
// * Created by bluepay on 2017/10/31.
// */
//
//public class UmengAnalysisHelper {
//    private static UmengAnalysisHelper mInstance;
//    public UmengAnalysisHelper() {
//
//    }
//
//    public static UmengAnalysisHelper getInstance() {
//        if (mInstance == null) {
//            mInstance = new UmengAnalysisHelper();
//        }
//
//        return mInstance;
//    }
//    /**
//     * 用于session的统计,在App onCreate方法中调用,禁止默认的Activity页面统计方式
//     *
//     * @param debug 友盟统计实时调试,为true时,可以在友盟统计后台实时查看测试设备的统计日志
//     */
//    public void onApplicationCreate(boolean debug) {
//        MobclickAgent.openActivityDurationTrack(false);
//        MobclickAgent.setDebugMode(debug);
//        MobclickAgent.setSessionContinueMillis(60000);//60秒
//    }
//
//    /**
//     * 用于session的统计,在每个Activity的onResume方法中调用
//     *
//     * @param context  程序上下文
//     * @param pageName 页面名称,如"LoginActivity"
//     */
//    public void onActivityResume(Context context, String pageName) {
//        if (!TextUtils.isEmpty(pageName)) {
//            MobclickAgent.onPageStart(pageName); //统计页面跳转(仅有Activity的应用中SDK自动调用,不需要单独写)
//        } else {
//            MobclickAgent.onPageStart("UnknownActivity");
//        }
//        MobclickAgent.onResume(context);        //统计时长
//    }
//
//    /**
//     * 用于session的统计,在每个Activity的onPause方法中调用
//     *
//     * @param context  程序上下文
//     * @param pageName 页面名称,如"LoginActivity"
//     */
//    public void onActivityPause(Context context, String pageName) {
//        if (!TextUtils.isEmpty(pageName)) {
//            MobclickAgent.onPageEnd(pageName); //统计页面跳转(仅有Activity的应用中SDK自动调用,不需要单独写)保证 onPageEnd 在onPause 之前调用,因为 onPause 中会保存信息
//        } else {
//            MobclickAgent.onPageEnd("UnknownActivity");
//        }
//        MobclickAgent.onPause(context);
//    }
//
//
//}
