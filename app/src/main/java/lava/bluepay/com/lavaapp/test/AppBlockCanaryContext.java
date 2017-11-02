//package lava.bluepay.com.lavaapp.test;
//
//import android.content.pm.PackageInfo;
//import android.content.pm.PackageManager;
//import com.github.moduth.blockcanary.BlockCanaryContext;
//
//import lava.bluepay.com.lavaapp.BuildConfig;
//import lava.bluepay.com.lavaapp.MixApp;
//import lava.bluepay.com.lavaapp.common.Logger;
//
//public class AppBlockCanaryContext extends BlockCanaryContext {
//
//    private static final String TAG = "BlockCanary";
//
//    /**
//     * 标示符,可以唯一标示该安装版本号,如版本+渠道名+编译平台
//     *
//     * @return apk唯一标示符
//     */
//    @Override
//    public String getQualifier() {
//        String qualifier = "";
//        try {
//            PackageInfo info = MixApp.getContext().getPackageManager()
//                    .getPackageInfo(MixApp.getContext().getPackageName(), 0);
//            qualifier += info.versionCode + "_" + info.versionName + "_YYB";
//        } catch (PackageManager.NameNotFoundException e) {
//            Logger.e(Logger.DEBUG_TAG, "getQualifier exception", e);
//        }
//        return qualifier;
//    }
//
//    @Override
//    public String getUid() {
//        return "87224330";
//    }
//
//    @Override
//    public String getNetworkType() {
//        return "4G";
//    }
//
//    @Override
//    public int getConfigDuration() {
//        return 9999;
//    }
//
//    @Override
//    public int getConfigBlockThreshold() {
//        return 500;
//    }
//
//    @Override
//    public boolean isNeedDisplay() {
//        return BuildConfig.DEBUG;
//    }
//}
