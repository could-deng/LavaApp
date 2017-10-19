package lava.bluepay.com.lavaapp.model.process;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import lava.bluepay.com.lavaapp.common.Logger;

/**
 * Created by bluepay on 2017/10/19.
 */

public class RequestManager {

    /**请求结果为成功*/
    public static final int MSG_REQUEST_FINISH = 1000;



    private static ThreadPoolExecutor requestExecutor;

    private static RequestManager instance;
    public static RequestManager getInstance(){
        if(instance == null){
            instance = new RequestManager();
        }
        return instance;
    }
    private ThreadPoolExecutor getRequestExecutor(){
        if(requestExecutor == null){
            requestExecutor = new ThreadPoolExecutor(2,3,10,
                    TimeUnit.SECONDS, new LinkedBlockingQueue<Runnable>());
        }
        return requestExecutor;
    }

    public void request(final String url, final Handler handler,
                        final int iRequestType){
        getRequestExecutor().execute(new Runnable() {
            @Override
            public void run() {
                String sResult = BaseProcessor.getInstance().getDataFromApi(url);
                if(TextUtils.isEmpty(sResult)){
                    Logger.e(Logger.DEBUG_TAG,"RequestManager,result null error");
                    return;
                }
                if(handler!=null){
                    Logger.i(Logger.DEBUG_TAG,sResult);
                    sendRequestResultMessage(sResult,handler,iRequestType);
                }
            }
        });
    }

    private void sendRequestResultMessage(String sResult,Handler handler,int iRequestType) {
        sendResultMessage(sResult,handler,iRequestType,MSG_REQUEST_FINISH);
    }

    private void sendResultMessage(String sResult, Handler handler, int iRequestType, int iMsgType){
        Message message = Message.obtain();
        message.what = iMsgType;
        message.arg1 = iRequestType;
        Bundle bundle = new Bundle();
        bundle.putString("resultString",sResult);
        message.setData(bundle);
        try {
            if(handler!=null) {
                handler.sendMessage(message);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
