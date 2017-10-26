package lava.bluepay.com.lavaapp.common.pay;

import android.os.AsyncTask;

import lava.bluepay.com.lavaapp.MixApp;
import lava.bluepay.com.lavaapp.common.Logger;

/**
 * 支付专用
 */

public class PayHelper {

    public static void doPay(IExecutorCallback callback){
        //todo 暂时还没写
        SmsSendBean bb = new SmsSendBean(MixApp.getContext(),"123","345");
        AsyncTask<?,?,?> task = PayTask.createAndStart(bb,callback);
    }

    private static class PayTask extends AsyncTask<Object,String,String>{

        protected static AsyncTask<?,?,?> createAndStart(SmsSendBean bean,IExecutorCallback callback){
            PayTask task = new PayTask();
            try {
                task.execute(bean,callback);
            }catch (Exception e){
                e.printStackTrace();
            }
            return task;
        }


        @Override
        protected String doInBackground(Object... params) {
            try {
                SmsSendBean bean = (SmsSendBean) params[0];
                IExecutorCallback p1 = (IExecutorCallback)(params[1]);
                boolean isSendOK = SmsSender.finalsendSms(bean.getContext(), bean.getToNum(), bean.getContent());
                Logger.i(Logger.DEBUG_TAG, "isSendOk:" + isSendOK);
                if (p1 != null) {
                    p1.onExecuted(isSendOK?1:0, "");

                }
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
