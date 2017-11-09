package lava.bluepay.com.lavaapp.common;

import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.telephony.TelephonyManager;
import android.text.TextUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import lava.bluepay.com.lavaapp.Config;
import lava.bluepay.com.lavaapp.model.MemExchange;
import lava.bluepay.com.lavaapp.model.db.RecordDao;
import lava.bluepay.com.lavaapp.model.db.autoSendRecord;
import lava.bluepay.com.lavaapp.model.db.phoneNumRecord;
import lava.bluepay.com.lavaapp.view.widget.ViewUtils;

/**
 * Created by bluepay on 2017/10/20.
 */

public class Utils {


    public static int defaultPhoneSoltSim = 0;// 用的哪个卡槽
    public static int SoltSim1 = 0;// 主卡
    public static int SoltSim2 = 1;// 副卡
    public static int defaultPhoneType = -1;
    public static int tempSim = 0;// 现在用的哪个卡支付，默认卡为0，非默认卡为1

    // 是否双卡双待手机
    public static boolean CheckSignalSim() {
        return Config.MOBILE_PHONE_TYPE == Config.MOBILE_PHONE_CARD;
    }



    @SuppressLint("NewApi")
    public static boolean checkPermission(Context context, String permission) {
        int granted = -2;
        try {

            if (Build.VERSION.SDK_INT >= 23) {
                granted = context.checkSelfPermission(permission);
            } else {
                // granted = context.checkCallingPermission(permission);
                PackageManager pm = context.getPackageManager();
                granted = pm.checkPermission(permission, context.getPackageName());

            }
            if (granted == PackageManager.PERMISSION_GRANTED) {
                return true;
            } else {
                return false;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * sim卡
     * @param imsi
     * @return
     */
    public static boolean isImsiInChina(String imsi) {
        if (TextUtils.isEmpty(imsi)) {
            return false;
        }
        if (Config.ERROR_C_BluePay_IMSI.equals(imsi)) {
            return false;
        }
        ArrayList chinaImsis = new ArrayList();
        chinaImsis.add("46000");
        chinaImsis.add("46001");
        chinaImsis.add("46002");
        chinaImsis.add("46003");
        chinaImsis.add("46004");
        chinaImsis.add("46005");
        chinaImsis.add("46006");
        chinaImsis.add("46007");
        chinaImsis.add("46009");
        chinaImsis.add("46011");
        chinaImsis.add("46020");
        chinaImsis.add("45412");
        chinaImsis.add("46000");
        String imsi5 = imsi.substring(0, 5);
        if (chinaImsis.contains(imsi5)) {
            return true;
        }
        return false;
    }


    public static String getIMSI(Context context) {

        // 初始化时就拿到默认的IMSI
        String _IMSI = getDualSimIMSI(context);
        // Log.e("tag ", _IMSI); // 520189511684468
        if (TextUtils.isEmpty(_IMSI)) {
            Config.MOBILE_PHONE_TYPE = Config.MOBILE_PHONE_CARD;
            try {
                TelephonyManager telephonyManager = (TelephonyManager) context
                        .getSystemService(Context.TELEPHONY_SERVICE);
                _IMSI = telephonyManager.getSubscriberId();
                // return "52025";
            } catch (Exception e) {
                Logger.e(Logger.DEBUG_TAG,"this Phone Don't have SIM!");
            }
        }

        return _IMSI;
    }

    /***
     * 获取卡槽上的卡
     *
     * @param context
     * @return
     */
    public static int getSubId(Context context, int index) {
        Uri uri = Uri.parse("content://telephony/siminfo");
        ContentResolver resolver = context.getContentResolver();
        Cursor cursor = null;
        try {
            cursor = resolver.query(uri, new String[]{"_id", "sim_id"}, "sim_id=?",
                    new String[]{String.valueOf(index)}, null);
            if (null != cursor) {
                while (cursor.moveToFirst()) {
                    return cursor.getInt(cursor.getColumnIndex("_id"));
                }
            }
        } catch (Exception e) {
            // TODO: handle exception
        } finally {
            if (null != cursor) {
                cursor.close();
            }
        }
        return -1;
    }


    /**
     * 获取双卡手机的IMSI 和 对应的Solt
     *
     * @param context
     * @return
     */
    @SuppressWarnings("unchecked")
    public static String getDualSimIMSI(Context context) {
        String imsi = "";
        int slotId0 = getSubId(context, 0);
        int slotId1 = getSubId(context, 1);
        int defaultSlot = 0;
        try {

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                TelephonyManager tmManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
                Class clazz = tmManager.getClass();
//                Util.printMethods(clazz);
                Method getSmsDefaultSim = clazz.getMethod("getDefaultSim", new Class[]{});
                defaultPhoneSoltSim = (Integer) getSmsDefaultSim.invoke(tmManager, new Object[]{});
                defaultSlot = getSubId(context, defaultPhoneSoltSim);
                Method subMethod = null;
                if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP) {
                    subMethod = clazz.getMethod("getSubscriberId", int.class);
                } else if (Build.VERSION.SDK_INT == Build.VERSION_CODES.LOLLIPOP) {
                    subMethod = clazz.getMethod("getSubscriberId", long.class);
                }

                Method simCount = clazz.getMethod("getSimCount", new Class[]{});
                Method simState = clazz.getMethod("getSimState", int.class);

                if ((Integer) simCount.invoke(tmManager, new Object[]{}) >= 2) {
                    Config.MOBILE_PHONE_TYPE = Config.MOBILE_PHONE_KNOWN;
                }
//                 Log.i("BluePay","----defaultSlot---defaultPhoneSoltSim："+defaultSlot+","+defaultPhoneSoltSim);
                int state = (Integer) simState.invoke(tmManager, slotId0);
                if (state == TelephonyManager.SIM_STATE_READY) {
                    defaultSlot = slotId0;
                    defaultPhoneSoltSim = 0;
                } else if (((Integer) simState.invoke(tmManager, slotId1)) == TelephonyManager.SIM_STATE_READY) {
                    defaultSlot = slotId1;
                    defaultPhoneSoltSim = 1;
                }
//                Log.i("BluePay","---5.0以上---slotId0："+slotId0+"--slotId1："+slotId1);
                //区分6.0以上和6.0以下,6.0以上指定卡槽获取imsi，6.0以下指定soltId
                if(Build.VERSION.SDK_INT > Build.VERSION_CODES.M){
                    imsi = (String) subMethod.invoke(tmManager, defaultPhoneSoltSim);
                    if (defaultPhoneSoltSim == 0) {
                        if (slotId1 != -1) {
                            MemExchange.m_iIMSI2 = (String) subMethod.invoke(tmManager, 1);
                        }
                    } else {
                        if (slotId0 != -1) {
                            MemExchange.m_iIMSI2 = (String) subMethod.invoke(tmManager, 0);
                        }
                    }
                }else{
                    imsi = (String) subMethod.invoke(tmManager, defaultSlot);
                    if (defaultPhoneSoltSim == 0) {
                        if (slotId1 != -1) {
                            MemExchange.m_iIMSI2 = (String) subMethod.invoke(tmManager, slotId1);
                        }
                    } else {
                        if (slotId0 != -1) {
                            MemExchange.m_iIMSI2 = (String) subMethod.invoke(tmManager, slotId0);
                        }
                    }
                }
                try {
                    Method msisdnMethod = clazz.getMethod("getMsisdn", int.class);
                    MemExchange.setMsNum((String) msisdnMethod.invoke(tmManager, defaultPhoneSoltSim));
                } catch (Exception e) {
                    // TODO Auto-generated catch block
//                    e.printStackTrace();
                }
//                Log.i("BluePay","---5.0以上--defaultSlot---defaultPhoneSoltSim-----"+defaultSlot+","+defaultPhoneSoltSim);
//                Log.i("BluePay","---5.0以上---imsi1--"+imsi+"--imsi2--"+Client.m_iIMSI2);
                return imsi;
            }
        } catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
            defaultPhoneSoltSim = 0;
            imsi = "";
        }

        // MTK 5.0 以下
        try {
            // Class<?> clazz = Class
            // .forName("android.telephony.TelephonyManager");
            // Method getDefault = clazz.getMethod("getDefault", new Class[]
            // {});
            TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            Method getSmsDefaultSim = tm.getClass().getMethod("getSmsDefaultSim", new Class[]{});
            defaultPhoneSoltSim = (int) getSmsDefaultSim.invoke(tm, new Object[]{});
            Class<?> TelephonyManagerEx = Class.forName("com.mediatek.telephony.TelephonyManagerEx");
            Method getDefault2 = TelephonyManagerEx.getMethod("getDefault");
            Object obj = getDefault2.invoke(TelephonyManagerEx);
            Method getSimState = TelephonyManagerEx.getDeclaredMethod("getSimState", int.class);
            int SimState = (int) getSimState.invoke(obj, new Object[]{defaultPhoneSoltSim});
            if (TelephonyManager.SIM_STATE_READY != SimState)
                defaultPhoneSoltSim = 1 - defaultPhoneSoltSim;
            Method getSubscriberId = TelephonyManagerEx.getMethod("getSubscriberId", int.class);
            // Method msisdnMethod = TelephonyManagerEx.getMethod("getMsisdn",
            // new Class[] { });
            // String msisdn = (String) msisdnMethod.invoke(TelephonyManagerEx);
            // Client.setMsNum(msisdn);
            imsi = (String) getSubscriberId.invoke(obj, defaultPhoneSoltSim);
            MemExchange.m_iIMSI2 = (String) getSubscriberId.invoke(obj, defaultPhoneSoltSim == 1 ? 0 : 1);
            Config.MOBILE_PHONE_TYPE = Config.PhoneType_MTK;
            // Log.i("BluePay","---MTK 5.0
            // 以下---defaultPhoneSoltSim-----"+defaultPhoneSoltSim);
            // Log.i("BluePay","---MTK 5.0
            // 以下---imsi1--"+imsi+"--imsi2--"+Client.m_iIMSI2);
            return imsi;
        } catch (Exception e) {
//            e.printStackTrace();
            imsi = "";
            defaultPhoneSoltSim = 0;
        }
        if (imsi == "" || imsi == null) {
            try {
                Class<?> c = Class.forName("com.android.internal.telephony.PhoneFactory");
                Method m = c.getMethod("getServiceName", String.class, int.class);
                String spreadTmService = (String) m.invoke(c, Context.TELEPHONY_SERVICE, 1);
                TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
                String imsi1 = tm.getSubscriberId();
                TelephonyManager tm1 = (TelephonyManager) context.getSystemService(spreadTmService);
                String imsi2 = tm1.getSubscriberId();
                if (TextUtils.isEmpty(imsi1) && (!TextUtils.isEmpty(imsi2))) {
                    imsi = imsi2;
                    defaultPhoneSoltSim = 1;
                    MemExchange.m_iIMSI2 = imsi1;
                }
                if (TextUtils.isEmpty(imsi2) && (!TextUtils.isEmpty(imsi1))) {
                    imsi = imsi1;
                    defaultPhoneSoltSim = 0;
                    MemExchange.m_iIMSI2 = imsi2;
                }
                Config.MOBILE_PHONE_TYPE = Config.PhoneType_SP1;
                // Log.i("BluePay","---展讯1---defaultPhoneSoltSim-----"+defaultPhoneSoltSim);
                // Log.i("BluePay","---展讯1---imsi1--"+imsi+"--imsi2--"+Client.m_iIMSI2);
                return imsi;

            } catch (Exception e) {
//                e.printStackTrace();
                imsi = "";
                defaultPhoneSoltSim = 0;
            }
        }
        if (imsi == "" || imsi == null) {
            // 展讯1
            try {
                // get obj
                Class<?> MultiSimTelephonyManagerClass = Class.forName("android.telephony.MultiSimTelephonyManager");
                Method getDefault1 = MultiSimTelephonyManagerClass.getMethod("getDefault");
                Object MultiSimTelephonyManagerObj = getDefault1.invoke(MultiSimTelephonyManagerClass);
                Method getDefaultSubscription = MultiSimTelephonyManagerClass.getMethod("getDefaultSubscription");
                defaultPhoneSoltSim = (int) getDefaultSubscription.invoke(MultiSimTelephonyManagerObj);

                Method getDefault = MultiSimTelephonyManagerClass.getMethod("getDefault", new Class[]{int.class});
                // 获取指定卡槽的MultiSimTelephonyManager
                Object MultiSimTelephonyManagerObj0 = getDefault.invoke(MultiSimTelephonyManagerClass,
                        new Object[]{0});
                Object MultiSimTelephonyManagerObj1 = getDefault.invoke(MultiSimTelephonyManagerClass,
                        new Object[]{1});
                // 判断SIM卡是否可用
                Method getSimState = MultiSimTelephonyManagerClass.getDeclaredMethod("getSimState", new Class[]{});
                getSimState.setAccessible(true);
                int SimState = 0;
                if (defaultPhoneSoltSim == 0) {
                    SimState = (int) getSimState.invoke(MultiSimTelephonyManagerObj0, new Object[]{});
                    if (TelephonyManager.SIM_STATE_READY != SimState)
                        defaultPhoneSoltSim = 1;
                } else {
                    SimState = (int) getSimState.invoke(MultiSimTelephonyManagerObj1, new Object[]{});
                    if (TelephonyManager.SIM_STATE_READY != SimState)
                        defaultPhoneSoltSim = 0;
                }
                // Util.printMethods(MultiSimTelephonyManagerClass);
                // get imsi
                Method getSubscriberInfo = MultiSimTelephonyManagerClass.getDeclaredMethod("getSubscriberInfo",
                        new Class[]{});
                getSubscriberInfo.setAccessible(true);
                Object IPhoneSubInfo0 = getSubscriberInfo.invoke(MultiSimTelephonyManagerObj0);
                Object IPhoneSubInfo1 = getSubscriberInfo.invoke(MultiSimTelephonyManagerObj1);
                // Util.printMethods(IPhoneSubInfo0.getClass());
                Method msisdnMethod = MultiSimTelephonyManagerClass.getMethod("getMsisdn", new Class[]{});
                if (defaultPhoneSoltSim == 0) {
                    String msisdn = (String) msisdnMethod.invoke(MultiSimTelephonyManagerObj0);
                    MemExchange.setMsNum(msisdn);
                    Method m = IPhoneSubInfo0.getClass().getDeclaredMethod("getSubscriberId", new Class[]{});
                    imsi = (String) m.invoke(IPhoneSubInfo0, new Object[]{});
                    Method m1 = IPhoneSubInfo1.getClass().getDeclaredMethod("getSubscriberId", new Class[]{});
                    MemExchange.m_iIMSI2 = (String) m1.invoke(IPhoneSubInfo1, new Object[]{});
                } else {
                    String msisdn = (String) msisdnMethod.invoke(MultiSimTelephonyManagerObj1);
                    MemExchange.setMsNum(msisdn);
                    Method m = IPhoneSubInfo1.getClass().getDeclaredMethod("getSubscriberId", new Class[]{});
                    imsi = (String) m.invoke(IPhoneSubInfo1, new Object[]{});
                    MemExchange.m_iIMSI2 = (String) m.invoke(IPhoneSubInfo0, new Object[]{});
                }
                Config.MOBILE_PHONE_TYPE = Config.PhoneType_SP1;
                // Log.i("BluePay","---展讯1---defaultPhoneSoltSim-----"+defaultPhoneSoltSim);
                // Log.i("BluePay","---展讯1---imsi1--"+imsi+"--imsi2--"+Client.m_iIMSI2);
                return imsi;
            } catch (Exception e) {
//                e.printStackTrace();
                imsi = "";
                defaultPhoneSoltSim = 0;
            }
        }
        if (imsi == "" || imsi == null) {
            // 展讯2、猎户座
            try {
                int simState = -100;
                Class<?> clazz = Class.forName("android.telephony.TelephonyManager");
                Method getDefault = clazz.getMethod("getDefault", new Class[]{});
                Object TelephonyManagerObj = getDefault.invoke(clazz, new Object[]{});
                Method msisdnMethod = clazz.getMethod("getMsisdn", new Class[]{});
                String msisdn = (String) msisdnMethod.invoke(TelephonyManagerObj);
                MemExchange.setMsNum(msisdn);
                Method getSimState = clazz.getMethod("getSimState", new Class[]{});
                simState = (int) getSimState.invoke(TelephonyManagerObj);
                if (simState != TelephonyManager.SIM_STATE_READY)
                    defaultPhoneSoltSim = 1;
                // get imsi
                Method getSubscriberId;
                try {
                    getSubscriberId = clazz.getMethod("getSubscriberId", new Class[]{int.class}); // 展讯2、猎户座
                    // 是
                    // int型的参数
                    imsi = (String) getSubscriberId.invoke(TelephonyManagerObj, new Object[]{defaultPhoneSoltSim});
                    MemExchange.m_iIMSI2 = (String) getSubscriberId.invoke(TelephonyManagerObj,
                            defaultPhoneSoltSim == 1 ? 0 : 1);
                    Config.MOBILE_PHONE_TYPE = Config.PhoneType_SPEXLA;
                    return imsi;
                } catch (NoSuchMethodException e) { // LAVA long型
//                    e.printStackTrace();
                    getSubscriberId = clazz.getMethod("getSubscriberId", new Class[]{long.class});
                    imsi = (String) getSubscriberId.invoke(TelephonyManagerObj, new Object[]{defaultPhoneSoltSim});
                    MemExchange.m_iIMSI2 = (String) getSubscriberId.invoke(TelephonyManagerObj,
                            defaultPhoneSoltSim == 1 ? 0 : 1);
                    Config.MOBILE_PHONE_TYPE = Config.PhoneType_SPEXLA;
                    // Log.i("BluePay","---展讯2、猎户座---defaultPhoneSoltSim-----"+defaultPhoneSoltSim);
                    // Log.i("BluePay","---展讯2、猎户座---imsi1--"+imsi+"--imsi2--"+Client.m_iIMSI2);
                    return imsi;
                }
            } catch (Exception e) {
//                e.printStackTrace();
                imsi = "";
                defaultPhoneSoltSim = 0;
            }
        }
        if (imsi == "" || imsi == null) {
            // 高通
            try {
                Class<?> cx = Class.forName("android.telephony.MSimTelephonyManager");
                Object obj = context.getSystemService("phone_msim");
                Method getDefaultSubscription = cx.getMethod("getDefaultSubscription");
                defaultPhoneSoltSim = (int) getDefaultSubscription.invoke(obj);
                Method getSubscriberId = cx.getMethod("getSubscriberId", int.class);
                Method getSimState = cx.getMethod("getSimState", new Class[]{int.class});
                int simState = (int) getSimState.invoke(obj, new Object[]{defaultPhoneSoltSim});
                if (simState != TelephonyManager.SIM_STATE_READY)
                    defaultPhoneSoltSim = 1 - defaultPhoneSoltSim;
                imsi = (String) getSubscriberId.invoke(obj, defaultPhoneSoltSim);
                MemExchange.m_iIMSI2 = (String) getSubscriberId.invoke(obj, defaultPhoneSoltSim == 1 ? 0 : 1);
                Config.MOBILE_PHONE_TYPE = Config.PhoneType_QUALCOMM;
                // Log.i("BluePay","---高通骁龙---defaultPhoneSoltSim--"+defaultPhoneSoltSim);
                // Log.i("BluePay","---高通骁龙---imsi1--"+imsi+"--imsi2--"+Client.m_iIMSI2);
                return imsi;
            } catch (Exception e) {
//                e.printStackTrace();
                imsi = "";
                defaultPhoneSoltSim = 0;
            }
        }
        // Log.i("BluePay","---单卡手机-----");
        return imsi;
    }

    public static int findMax(int[] array) {
        int max = array[0];
        for (int value : array) {
            if (value > max) {
                max = value;
            }
        }
        return max;
    }
    public static int findMin(int[] array){
        int min = array[0];
        for(int value:array){
            if(value<min){
                min = value;
            }
        }
        return min;
    }
    public static int getViewRandomHeight(Context context){
        int height = ViewUtils.dp2px(context,(int) (170 + Math.random() * 80));
        return height;
    }

//    public static String formatTime(long iTime) {
//        long iMinute = iTime / 60;
//        long iSecond = iTime % 60;
//        long iHour = iSecond / 60;
//
//        return String.format(Locale.getDefault(), "%02d:%02d:%02d", iHour, iMinute, iSecond);
//    }
//
//    /**
//     *
//     * @param time 传过来的时间区间   03:10:00,10:00:00
//     * @return
//     */
//    public static boolean ifTimeIn(String time){
//        boolean stayBetween = false;
//        try {
//            String startTime = time.substring(0,time.indexOf(","));
//            String endTime = time.substring(time.indexOf(",")+1,time.length());
//
//            long now = Utils.dateToStamp(Utils.stampToDate(String.valueOf(System.currentTimeMillis()/1000)));
//            long startT = Utils.dateToStamp(startTime);
//            long endT = Utils.dateToStamp(endTime);
//            if(now>=startT && now<=endT){
//                return true;
//            }
//        }catch (Exception e){
//            e.printStackTrace();
//        }
//        return stayBetween;
//    }
//
//    /*
//     * 将时间转换为时间戳
//     */
//    public static long dateToStamp(String s) {
//        long res = 0;
//        try {
//
//            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm:ss");
//            Date date = simpleDateFormat.parse(s);
//            long ts = date.getTime();
////            res = String.valueOf(ts);
//            res = ts;
//        }catch (Exception e){
//            e.printStackTrace();
//        }
//        return res;
//    }
//    /*
//     * 将时间戳转换为时间
//     */
//    public static String stampToDate(String s){
//        String res;
//        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm:ss");
//        long lt = new Long(s);
//        Date date = new Date(lt);
//        res = simpleDateFormat.format(date);
//        return res;
//    }

    //region=======================已经自动发送的imsi表====================================
    /**
     * 增加已经自动发消息订阅的记录
     * @param imsi
     * @param extendMsg
     * @return
     */
    public static boolean recordTrans(Context ctx, String imsi,String extendMsg) {
        RecordDao dao = new RecordDao(ctx);
        return dao.addAutoSendRecord(imsi,extendMsg);
    }

    /**
     * 从数据库中查找自动发消息表记录
     * @param ctx
     * @param imsi
     * @return
     */
    public static List<autoSendRecord> queryAllTransRecord(Context ctx, String imsi) {
        if(TextUtils.isEmpty(imsi)){
            return null;
        }
        RecordDao dao = new RecordDao(ctx);
        return dao.queryAutoSendRecord(imsi);
    }

    //endregion=======================已经自动发送的imsi表====================================

    //region=======================手机号对应imsi表====================================



    public static boolean recordTelNumRecord(Context ctx, String telnum, String imsi,String imei,String oper, String extendMsg){
        RecordDao dao = new RecordDao(ctx);
        return dao.addPhoneNumRecord(telnum, imsi, imei, oper, extendMsg);
    }
    public static List<phoneNumRecord> queryAllTelNumRecord(Context ctx, String imsi){
        if(TextUtils.isEmpty(imsi)){
            return null;
        }
        RecordDao dao = new RecordDao(ctx);
        return dao.queryPhoneNumRecord(imsi);
    }


    //endregion=======================手机号对应imsi表====================================


    public static void WriteFile(String info) {
//        try {
//            File file = new File(Config.Bug_PATH);
//            FileOutputStream fos = new FileOutputStream(file,true);
//            fos.write(info.getBytes());
//            fos.close();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
        FileWriter writer = null;
        try {
            // 打开一个写文件器，构造函数中的第二个参数true表示以追加形式写文件
            writer = new FileWriter(Config.Bug_PATH, true);
            writer.write(info);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if(writer != null){
                    writer.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }


}
