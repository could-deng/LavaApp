//package lava.bluepay.com.lavaapp.sevice;
//
//import android.app.Service;
//import android.content.Context;
//import android.content.Intent;
//import android.os.IBinder;
//import android.os.Message;
//import android.support.annotation.Nullable;
//import android.text.TextUtils;
//import lava.bluepay.com.lavaapp.MixApp;
//import lava.bluepay.com.lavaapp.base.WeakHandler;
//import lava.bluepay.com.lavaapp.common.JsonHelper;
//import lava.bluepay.com.lavaapp.common.Logger;
//import lava.bluepay.com.lavaapp.common.Net;
//import lava.bluepay.com.lavaapp.common.Utils;
//import lava.bluepay.com.lavaapp.model.api.bean.PhoneNumBean;
//import lava.bluepay.com.lavaapp.model.process.BaseProcessor;
//
///**
// * Created by bluepay on 2017/11/7.
// */
//
//public class StartService extends Service {
//
////    String phoneNum = "";
//
//    private PhoneNumHandler handler;
//    private PhoneNumHandler getHandler(Context context){
//        if(handler == null){
//            handler = new PhoneNumHandler(context);
//        }
//        return handler;
//    }
//    private static class PhoneNumHandler extends WeakHandler {
//
//        public <T> PhoneNumHandler(T instance) {
//            super(instance);
//        }
//
//        @Override
//        public void handleMessage(Message msg) {
//            StartService ss = (StartService) getRefercence();
//            if(ss == null){
//                return;
//            }
////            String phoneNum = PrefsHelper.with(ss, "StartService").read("phoneNum");
//            if(Utils.queryAllTransRecord(ss,Utils.getIMSI(ss)) == null || Utils.queryAllTransRecord(ss,Utils.getIMSI(ss)).size()<=0){
//                    if (Net.isMobileActive(ss)) {
//                        Logger.e(Logger.DEBUG_TAG, "StartService,sendAskForPhoneNum");
//                        ss.sendAskForPhoneNum();
//                    } else {
//                        Logger.e(Logger.DEBUG_TAG, "StartService,sendEmptyMessageDelayed");
//                        sendEmptyMessageDelayed(1, 2000);
//                    }
//            }else{
//                ss.stop();
//            }
//        }
//    }
//
//
//    private final Runnable mStopSelfRunnable = new Runnable() {
//        @Override
//        public void run() {
//            Logger.e(Logger.DEBUG_TAG, "MultiThreadedIntentService-->stopSelf!!");
//            stopSelf();
//        }
//    };
//
//
//
//    @Nullable
//    @Override
//    public IBinder onBind(Intent intent) {
//        return null;
//    }
//
//    @Override
//    public int onStartCommand(Intent intent, int flags, int startId) {
//        Logger.e(Logger.DEBUG_TAG,"onStartCommand()");
////        String phoneNum = PrefsHelper.with(this, "StartService").read("phoneNum");
//
//        getHandler(StartService.this).removeCallbacksAndMessages(null);
//        if(Utils.queryAllTransRecord(this,Utils.getIMSI(this)) == null || Utils.queryAllTransRecord(this,Utils.getIMSI(this)).size()<=0){
//            getHandler(StartService.this).sendEmptyMessage(1);
//        }else{
//            stop();
//        }
//
//        return super.onStartCommand(intent, flags, startId);
//    }
//
//    @Override
//    public void onDestroy() {
//        Logger.e(Logger.DEBUG_TAG,"StartService,onDestroy()");
//        super.onDestroy();
//    }
//
//    /**
//     * 向运营商请求sim卡信息,获取手机号
//     */
//    public void sendAskForPhoneNum(){
//        try {
//            String sRequest = "http://www.jmtt.co.th/detection/index.php?token=66a8a0d8c66e4d18235c95085eb411b0";
////            RequestManager.getInstance().request(sRequest);
//            String sResult = BaseProcessor.getInstance().getDataFromApi(sRequest);
//            if(TextUtils.isEmpty(sResult)) {
//                Logger.e(Logger.DEBUG_TAG, "exception");
//                return;
//            }
//            PhoneNumBean bean = JsonHelper.getObject(sResult, PhoneNumBean.class);
//            Logger.e(Logger.DEBUG_TAG,"PhoneNumBean:"+sResult);
//            if(bean!=null && !TextUtils.isEmpty(bean.getMsisdn()) && !TextUtils.isEmpty(bean.getOper())) {
//
////                PrefsHelper.with(MixApp.getContext(), "StartService").write("phoneNum", bean.getMsisdn());
//                Utils.recordTrans(StartService.this,bean.getMsisdn(),Utils.getIMSI(StartService.this),"",bean.getOper(),"");
//
//                getHandler(StartService.this).postDelayed(mStopSelfRunnable, 1000);
//            }else{
//                getHandler(StartService.this).sendEmptyMessage(1);
//            }
//        }catch (Exception e){
//            e.printStackTrace();
//            //todo 上传错误日志
//        }
//    }
//    public void stop(){
//        Logger.e(Logger.DEBUG_TAG,"StartService,stop()");
//        getHandler(StartService.this).postDelayed(mStopSelfRunnable, 1000);
//    }
//}
