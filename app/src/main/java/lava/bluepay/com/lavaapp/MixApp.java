package lava.bluepay.com.lavaapp;

import android.app.Application;
import android.content.Context;
import android.os.Build;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.imagepipeline.core.ImagePipelineConfig;
import lava.bluepay.com.lavaapp.common.FileUtils;
import lava.bluepay.com.lavaapp.common.UmengAnalysisHelper;

/**
 * Created by bluepay on 2017/10/14.
 */

public class MixApp extends Application {
    private static Context context;
//    private RefWatcher refWatcher;
//
//    public static RefWatcher getRefWatcher(Context context) {
//        MixApp application = (MixApp) context.getApplicationContext();
//        return application.refWatcher;
//    }

    @Override
    public void onCreate() {
        super.onCreate();
        context = this;



        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
            initFresco();//facebook fresco框架
        }else{
            Fresco.initialize(this);//facebook fresco框架
        }
        FileUtils.makeFolders(Config.PHOTO_PATH);
        FileUtils.makeFolders(Config.db_path);

        UmengAnalysisHelper.getInstance().onApplicationCreate(false);

//        BlockCanary.install(this, new AppBlockCanaryContext()).start();
//        refWatcher = LeakCanary.install(this);//LeakCanary
    }



    public static Context getContext(){
        return context;
    }

    /** 初始化facebook fresco*/
    private void initFresco() {
        /**
         * oom android 5.0
         * https://github.com/facebook/fresco/issues/1396
         * */
        //ActivityManager activityManager = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
        ImagePipelineConfig imagePipelineConfig = ImagePipelineConfig
                .newBuilder(getApplicationContext())
                .setDownsampleEnabled(true)
                //.setBitmapMemoryCacheParamsSupplier(new LollipopBitmapMemoryCacheParamsSupplier(activityManager))
                .build();
        Fresco.initialize(getApplicationContext(), imagePipelineConfig);
    }


}
