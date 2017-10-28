package lava.bluepay.com.lavaapp.common.pay;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.telephony.SmsManager;

import java.lang.reflect.Method;

import lava.bluepay.com.lavaapp.Config;
import lava.bluepay.com.lavaapp.common.Logger;
import lava.bluepay.com.lavaapp.common.Utils;
import lava.bluepay.com.lavaapp.model.MemExchange;

/**
 * Created by bluepay on 2017/10/26.
 */

public class SmsSender  {

    public static final String SENT_SMS_ACTION = "SENT_SMS_ACTION";
    public static final String SMS_DELIVERED_ACTION = "SMS_DELIVERED_ACTION";


    //短代V2接口直接走这个方法
    public static boolean finalsendSms(Context context,String toNumber, String smsContent)
            throws Exception {
//		if (!Util.checkSmsPermission(order.getActivity())) {
//			throw new BlueException(ErrorCode.code_405,
//					"please granted send sms permission then try again.");
//		}

        Intent sentIntent = new Intent(SENT_SMS_ACTION);
        PendingIntent mSendPI = PendingIntent.getBroadcast(context, 0,
                sentIntent, 0);
        Intent intent = new Intent(SMS_DELIVERED_ACTION);
        PendingIntent diliverIntent = PendingIntent.getBroadcast(context, 0,
                intent,0);
        Logger.i(Logger.DEBUG_TAG,"content:   "+smsContent);
        finalSend(context, toNumber, smsContent, mSendPI, diliverIntent);

        return true;
    }


    private static boolean finalSend(Context ctx, String dest,
                                     String content, PendingIntent sendPI,PendingIntent diliverIntent) throws Exception {

        try {
            Logger.i(Logger.DEBUG_TAG,"content:"+content+" length:"+content.length());
//            if(Utils.isImsiInChina(MemExchange.m_iIMSI)){
//                content = order.getCurrency() + " " + content;
//            }
            if (!Utils.CheckSignalSim()) {
                try {

                    sendMsgByDualSim(ctx, dest, content,
                            Config.MOBILE_PHONE_TYPE,sendPI,diliverIntent);
                } catch (Exception e2) {
                    e2.printStackTrace();
                    // 单卡处理
                    Logger.i(Logger.DEBUG_TAG,"sigle sim send,dest:"+dest + " content:"+content);
//					Log.i("BluePay", "sigle sim send,dest:"+dest + " content:"+content);
                    SmsManager smsManager = android.telephony.SmsManager
                            .getDefault();
                    smsManager.sendTextMessage(dest, null, content, sendPI,
                            diliverIntent);
                }
            } else {
                // 单卡处理

                Logger.i(Logger.DEBUG_TAG,"sigle sim send,dest:"+dest + " content:"+content);
                SmsManager smsManager = android.telephony.SmsManager
                        .getDefault();
                smsManager.sendTextMessage(dest, null, content, sendPI,
                        diliverIntent);
            }
            Logger.i(Logger.DEBUG_TAG,"send sms finish");
            String imsi = MemExchange.m_iIMSI;
            if (!Utils.CheckSignalSim()) {
                imsi = "double sim|imsi1:" + MemExchange.m_iIMSI1 + "|imsi2:"
                        + MemExchange.m_iIMSI2 + "|send_imsi:" + MemExchange.m_iIMSI;
            } else {
                imsi = "single sim|imsi1:" + MemExchange.m_iIMSI1 + "|imsi2:"
                        + MemExchange.m_iIMSI2 + "|send_imsi:" + MemExchange.m_iIMSI;
            }
        } catch (SecurityException se) {
            se.printStackTrace();
//            throw new BlueException(ErrorCode.code_405,
//                    "please granted send sms permission then try again.");
        } catch (Exception e) {
//            throw new BlueException(ErrorCode.code_405, e.getMessage());
            e.printStackTrace();
        }
        return true;
    }


    /**
     * 双卡手机指定卡槽发短信
     *
     * @throws Exception
     */
    public static void sendMsgByDualSim(Context context, String toNum,
                                        String content, int PhoneType, PendingIntent mSendPI, PendingIntent diliverPI) throws Exception {
        Class[] sendTextMessagePamas = { String.class, String.class,
                String.class, PendingIntent.class, PendingIntent.class };
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Class<?> smsClass = Class.forName("android.telephony.SmsManager");
            Method smsManagerDefault = null;
            if (Build.VERSION.SDK_INT == Build.VERSION_CODES.LOLLIPOP) {
                smsManagerDefault = smsClass.getMethod("getSmsManagerForSubscriber", long.class);
            } else {
                smsManagerDefault = smsClass.getMethod("getSmsManagerForSubscriptionId", int.class);
            }
//			Log.i("BluePay", "----sendMsgByDualSim--subId--"+Util.getSubId(context, Util.defaultPhoneSoltSim));
            SmsManager smsManagerObjet;
            //区分6.0以上和6.0以下,6.0以上指定卡槽获取SmsManager，6.0以下指定soltId
            if(Build.VERSION.SDK_INT > Build.VERSION_CODES.M){
                smsManagerObjet = (SmsManager) smsManagerDefault.invoke(smsClass, Utils.defaultPhoneSoltSim);
            }else{
                smsManagerObjet = (SmsManager) smsManagerDefault.invoke(smsClass, Utils.getSubId(context, Utils.defaultPhoneSoltSim));
            }
            Method sendTextMessage = smsClass.getDeclaredMethod("sendTextMessage", sendTextMessagePamas);
            sendTextMessage.invoke(smsManagerObjet, new Object[] {
                    toNum, null, content, mSendPI, diliverPI });
            return;
        }
        Class[] sendMultipartTextMessagePamas = { String.class, String.class,
                String.class, PendingIntent.class, PendingIntent.class,
                int.class };
        switch (PhoneType) {
            case Config.PhoneType_MTK:
                // MTK
                Class<?> smsManagerClass = Class
                        .forName("com.mediatek.telephony.SmsManagerEx");
                Method getDefault = smsManagerClass.getMethod("getDefault",
                        new Class[] {});
                getDefault.setAccessible(true);
                Object SmsManagerExObj = getDefault.invoke(smsManagerClass,
                        new Object[] {});
                Method sendMultipartTextMessage = smsManagerClass.getMethod(
                        "sendTextMessage", sendMultipartTextMessagePamas);
                sendMultipartTextMessage.invoke(SmsManagerExObj, toNum, null,
                        content, mSendPI, diliverPI, Utils.defaultPhoneSoltSim);
                break;
            case Config.PhoneType_SP1:
                // 展讯1
                SmsManager smsManager = SmsManager.getDefault();
                Method sendTextMessageBySolt = smsManager.getClass()
                        .getDeclaredMethod("sendTextMessage",
                                sendMultipartTextMessagePamas);
                sendTextMessageBySolt.invoke(smsManager,
                        new Object[] { toNum, null, content, mSendPI, diliverPI,
                                Utils.defaultPhoneSoltSim });
                break;
            case Config.PhoneType_SPEXLA:
                // 展讯2、猎户座、lava
                Class<?> SmsManagerSpread = Class
                        .forName("android.telephony.SmsManager");
                Method getSmsManagerForSubscriber = SmsManagerSpread
                        .getDeclaredMethod("getSmsManagerForSubscriber",
                                new Class[] { long.class });
                getSmsManagerForSubscriber.setAccessible(true);
                Object SmsManagerForSubscriberObj = getSmsManagerForSubscriber
                        .invoke(SmsManagerSpread,
                                new Object[] { Utils.defaultPhoneSoltSim });
                Method sendTextMessage = SmsManagerForSubscriberObj.getClass()
                        .getDeclaredMethod("sendTextMessage", sendTextMessagePamas);
                sendTextMessage.invoke(SmsManagerForSubscriberObj, new Object[] {
                        toNum, null, content, mSendPI, diliverPI });
                break;
            case Config.PhoneType_QUALCOMM:
                //高通骁龙
                Class<?> msc = Class.forName("android.telephony.MSimSmsManager");
                Method getDefault2 = msc.getMethod("getDefault");
                Object smsObj = getDefault2.invoke(msc);
                Method sendMessage = msc.getDeclaredMethod("sendTextMessage", sendMultipartTextMessagePamas);
                sendMessage.invoke(smsObj, new Object[] {
                        toNum, null, content, mSendPI, diliverPI, Utils.defaultPhoneSoltSim});
                break;
            default:
                break;
        }
    }


}
