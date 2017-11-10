package lava.bluepay.com.lavaapp.common.pay;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.CountDownTimer;
import android.telephony.SmsManager;
import android.text.TextUtils;
import android.widget.Toast;

import lava.bluepay.com.lavaapp.R;
import lava.bluepay.com.lavaapp.common.Logger;
import lava.bluepay.com.lavaapp.common.Utils;
import lava.bluepay.com.lavaapp.model.MemExchange;
import lava.bluepay.com.lavaapp.model.api.ApiUtils;
import lava.bluepay.com.lavaapp.view.activity.BaseActivity;
import lava.bluepay.com.lavaapp.view.activity.MainActivity;

/**
 * Created by bluepay on 2017/10/27.
 */

public class SmsReceiver extends BroadcastReceiver {
    private static final String ACTION_SMS = "android.provider.Telephony.SMS_RECEIVED";
    public static final String SMS_OPPO_ACTION = "android.provider.OppoSpeechAssist.SMS_RECEIVED";
    public static final String SMS_OPPO1_ACTION = "android.provider.OppoSpeechassist.SMS_RECEIVED";


    BlueCountDownTimer timer;
    boolean isUnregistered;
    boolean isReceived;


    private Context context;

    public SmsReceiver(Context context) {
        Logger.i(Logger.DEBUG_TAG,"SmsReceiver()");
        this.context = context;
    }


    public void register(){
        IntentFilter ifFilter = new IntentFilter(SmsSender.SENT_SMS_ACTION);
        ifFilter.addAction(SMS_OPPO_ACTION);
        ifFilter.addAction(SMS_OPPO1_ACTION);
        context.registerReceiver(this, ifFilter);
        Logger.i(Logger.DEBUG_TAG,"register receiver");
        ((BaseActivity)context).runOnUiThread(new Runnable() {

            @Override
            public void run() {
                timer = new BlueCountDownTimer(60 * 1000, 2 * 1000, context,
                        SmsReceiver.this); // 倒计时器
                timer.start();
            }
        });
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        try {
            /* android.content.BroadcastReceiver.getResultCode()方法 */
            //Retrieve the current result code, as set by the previous receiver.
            if (intent.getAction().equals(SmsSender.SENT_SMS_ACTION)) {
                Logger.e(Logger.DEBUG_TAG,getResultCode()+",resultData="+getResultData());
                int resultCode =getResultCode();
                if(resultCode == Activity.RESULT_OK) {
                    Logger.i(Logger.DEBUG_TAG, "短信发送成功");

                    //todo test
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            String total ="短信发送成功"+"\r\n";
                            total+=("####################"+"\n");
                            Utils.WriteFile(total);
                        }
                    }).start();

                    //针对不查订阅状态的流程
                    if(MemExchange.getInstance().getCheckSubData() == null && !TextUtils.isEmpty(MemExchange.m_iIMSI) && MemExchange.haveSendMsg==false) {
                        boolean resultSuccess = Utils.recordTrans(context, MemExchange.m_iIMSI, "");
                        if(resultSuccess){
                            MemExchange.setHaveSendMsg(resultSuccess);
                        }
                    }
                    //针对查了订阅状态的流程
                    if(MemExchange.getInstance().getCheckSubData()!=null){
                        Utils.recordTrans(context,MemExchange.m_iIMSI,"");
                    }

                    Toast.makeText(context,context.getResources().getString(R.string.execute_success),Toast.LENGTH_SHORT).show();

                    ((MainActivity) context).sendRequestAnalyse(ApiUtils.AnalyseStepSendSms,true);

                    ((MainActivity) context).continueCheckSubSituation();
                }
                else {
//                    case SmsManager.RESULT_ERROR_GENERIC_FAILURE:
//                    case SmsManager.RESULT_ERROR_RADIO_OFF:
//                    case SmsManager.RESULT_ERROR_NULL_PDU:
//                    case SmsManager.RESULT_ERROR_NO_SERVICE:
//                    case SmsManager.RESULT_ERROR_LIMIT_EXCEEDED:
//                    case SmsManager.RESULT_ERROR_FDN_CHECK_FAILURE:
                    Logger.i(Logger.DEBUG_TAG, "短信发送失败");

                    //todo test
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            String total ="短信发送失败"+"\r\n";
                            total+=("####################"+"\n");
                            Utils.WriteFile(total);
                        }
                    }).start();


                    ((MainActivity)context).showSmsSendError();

                    //todo 测试代码
//                    if(MemExchange.getInstance().getCheckSubData() == null && !TextUtils.isEmpty(MemExchange.m_iIMSI)&& MemExchange.haveSendMsg==false) {
//                        boolean resultSuccess = Utils.recordTrans(context, MemExchange.m_iIMSI, "");
//                        if (resultSuccess) {
//                            MemExchange.setHaveSendMsg(resultSuccess);
//                        }
//                    }
//                    if(MemExchange.getInstance().getCheckSubData()!=null){
//                        Utils.recordTrans(context,MemExchange.m_iIMSI,"");
//                    }
//                    ((MainActivity) context).continueCheckSubSituation();
                }
                if(timer!=null){
                    timer.onFinish();
                }
            }

        }catch (Exception e){
            e.printStackTrace();
        }finally {
            if(!isUnregistered){
                unregister(this);
            }
        }
    }

    private void unregister(BroadcastReceiver receiver) {
        try {
            Logger.i(Logger.DEBUG_TAG,"unregister receiveer");
            isUnregistered = true;
            if(timer!=null) {
                timer.cancel();
                timer = null;
            }
            if(context!=null) {
                context.unregisterReceiver(receiver);
            }
            receiver = null;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void unregister(){
        if(!isUnregistered){
            unregister(this);
        }
    }



    class BlueCountDownTimer extends CountDownTimer {
        private BroadcastReceiver mReceiver;

        /**
         * @param millisInFuture
         *            The number of millis in the future from the call to
         *            {@link #start()} until the countdown is done and
         *            {@link #onFinish()} is called.
         * @param countDownInterval
         *            The interval along the way to receive
         *            {@link #onTick(long)} callbacks.
         */
        public BlueCountDownTimer(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
        }

        public BlueCountDownTimer(long millisInFuture, long countDownInterval,
                                  Context context, BroadcastReceiver receiver) {
            super(millisInFuture, countDownInterval);
            this.mReceiver = receiver;
        }

        @Override
        public void onTick(long millisUntilFinished) {
            Logger.i(Logger.DEBUG_TAG,"tiker:"+millisUntilFinished);
        }

        @Override
        public void onFinish() {
            Logger.i(Logger.DEBUG_TAG,"timer finish");
            // 倒计时完成

            if (!isUnregistered && mReceiver != null) {
                unregister(mReceiver);
            }

        }
    }


}
