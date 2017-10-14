package lava.bluepay.com.lavaapp.view.widget;

import android.content.Context;
import android.content.res.Resources;
import android.util.DisplayMetrics;

/**
 * Created by bluepay on 2017/10/11.
 */

public class ViewUtils {

    public static float dp2px(Resources resouces, int dp){
        final float scale = resouces.getDisplayMetrics().density;//屏幕分辨率密度
        return dp * scale + 0.5f;
    }

    public static int dp2px(Context context, int dp) {
        float density = context.getResources().getDisplayMetrics().density;
        return Math.round((float) dp * density);
    }


    /**
     * 获取屏幕的宽度
     * @return 屏幕的像素宽度
     * */
    public static int getScreenWidth(Context context){
        if(context == null)
            return 720;
        DisplayMetrics dm = context.getResources().getDisplayMetrics();
        if(dm == null)
            return 720;
        return dm.widthPixels;
    }

    /**
     * 获取屏幕的密度
     * @return 屏幕的像素密度
     * */
    public static float getScreenDensity(Context context){
        if(context == null)
            return 2;
        DisplayMetrics dm = context.getResources().getDisplayMetrics();
        if(dm == null)
            return 720;
        return dm.density;
    }

    /**
     * 获取屏幕的高度
     * @return 屏幕的像素高度
     * */
    public static int getScreenHeight(Context context){
        if(context == null)
            return 1280;
        DisplayMetrics dm = context.getResources().getDisplayMetrics();
        if(dm == null)
            return 1280;
        return dm.heightPixels;
    }

}
