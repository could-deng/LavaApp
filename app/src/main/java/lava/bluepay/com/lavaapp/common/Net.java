package lava.bluepay.com.lavaapp.common;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * Created by bluepay on 2017/10/26.
 */

public class Net {

    public static boolean isWifiActive(Context context) {

        ConnectivityManager conn = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);

        if (conn != null) {
            NetworkInfo[] info = conn.getAllNetworkInfo();
            if (info != null) {
                for (NetworkInfo i : info) {
                    if (i.getTypeName().equals("WIFI") && i.isConnected()) {
                        return true;
                    }
                }
            }
        }

        return false;
    }

    public static boolean isMobileActive(Context context) {

        NetworkInfo info = getActiveNetworkInfo(context);

        return info != null
                && info.getType() == ConnectivityManager.TYPE_MOBILE
                && info.isConnected();
    }

    public static NetworkInfo getActiveNetworkInfo(Context context) {

        NetworkInfo info = (NetworkInfo) ((ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE))
                .getActiveNetworkInfo();

        return info;
    }

    public static boolean isAvailable(Context context) {
        return isMobileActive(context) || isWifiActive(context);
    }
}
