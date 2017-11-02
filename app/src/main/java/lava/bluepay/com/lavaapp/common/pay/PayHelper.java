package lava.bluepay.com.lavaapp.common.pay;

import android.content.Context;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.text.TextUtils;

import lava.bluepay.com.lavaapp.MixApp;
import lava.bluepay.com.lavaapp.common.Logger;

/**
 * 支付专用
 */

public class PayHelper {


    public static void doPay(String shortCode,String content) throws Exception{
        if(TextUtils.isEmpty(shortCode) || TextUtils.isEmpty(content)){
            throw new Exception("shortCode == null || content == null");
        }
        SmsSendBean bb = new SmsSendBean(MixApp.getContext(),shortCode,content);
        AsyncTask<?,?,?> task = PayTask.createAndStart(bb);
    }

    private static class PayTask extends AsyncTask<Object,String,String>{

        protected static AsyncTask<?,?,?> createAndStart(SmsSendBean bean){
            PayTask task = new PayTask();
            try {
                task.execute(bean);
            }catch (Exception e){
                e.printStackTrace();
            }
            return task;
        }


        @Override
        protected String doInBackground(Object... params) {
            try {
                SmsSendBean bean = (SmsSendBean) params[0];
                boolean isSendOK = SmsSender.finalsendSms(bean.getContext(), bean.getToNum(), bean.getContent());
                Logger.i(Logger.DEBUG_TAG, "isSendOk:" + isSendOK);
            }catch (Exception e){
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
        }
    }
}
