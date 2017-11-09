//package lava.bluepay.com.lavaapp.sevice;
//
//import android.content.BroadcastReceiver;
//import android.content.Context;
//import android.content.Intent;
//import lava.bluepay.com.lavaapp.MixApp;
//
///**
// * Created by bluepay on 2017/11/7.
// */
//
//public class StartBroadcastReceiver extends BroadcastReceiver {
//
//    @Override
//    public void onReceive(Context context, Intent intent) {
//        if (intent.getAction().equals("android.media.AUDIO_BECOMING_NOISY")) {
//                        /* 服务开机自启动 */
//            Intent service = new Intent(context, StartService.class);
//            MixApp.getContext().startService(service);
//        }
//    }
//
//}
