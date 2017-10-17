package lava.bluepay.com.lavaapp.common;

import java.util.Locale;

/**
 * Created by bluepay on 2017/10/17.
 */

public class FormatUtils {

    public static String formatToVideoPlayTime(long iTime){
        long iMinute = iTime / 60;
        long iSecond = iTime % 60;
        long iHour = iMinute / 60;
        if(iHour >0){
            return  String.format(Locale.getDefault(),"%02d:%02d:%02d",iHour,iMinute,iSecond);
        }else{
             return String.format(Locale.getDefault(),"%02d:%02d",iMinute,iSecond);
        }
    }


}
