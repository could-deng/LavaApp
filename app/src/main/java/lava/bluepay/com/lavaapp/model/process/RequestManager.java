package lava.bluepay.com.lavaapp.model.process;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import lava.bluepay.com.lavaapp.base.RequestBean;
import lava.bluepay.com.lavaapp.common.JsonHelper;
import lava.bluepay.com.lavaapp.common.Logger;
import lava.bluepay.com.lavaapp.common.Utils;
import lava.bluepay.com.lavaapp.model.MemExchange;
import lava.bluepay.com.lavaapp.model.api.ApiUtils;
import lava.bluepay.com.lavaapp.model.api.bean.BaseBean;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.RequestBody;

/**
 * Created by bluepay on 2017/10/19.
 */

public class RequestManager {

//    /** 网络不可用     */
//    public static final int MSG_NETNOWR_ERROR = -1;
    /** 请求结果为成功 */
    public static final int MSG_REQUEST_FINISH = 1000;
    /** 请求结果为失败 */
    public static final int MSG_REQUEST_ERROR = 1001;


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

    /**
     * post请求
     * @param url
     * @param handler
     * @param rb
     * @param bean
     */
    public void requestByPost(final String url, final Handler handler, final RequestBody rb,final RequestBean bean){
        getRequestExecutor().execute(new Runnable() {
            @Override
            public void run() {
                Logger.e(Logger.DEBUG_TAG,"请求:"+url);

                MemExchange.getInstance().saveLastestReqBean(bean);

                String sResult = BaseProcessor.getInstance().postRequestBodyToApi(url,rb);
                if(TextUtils.isEmpty(sResult)){
                    Logger.e(Logger.DEBUG_TAG,"exception");
//                    sendResultMessage("",handler,iRequestType,MSG_NETNOWR_ERROR);
                    return;
                }
                if(handler!=null){
                    Logger.i(Logger.DEBUG_TAG,""+sResult.toString());
                    sendRequestResultMessage(sResult,handler,bean.getRequestType());
                }else{
                    Logger.e(Logger.DEBUG_TAG,"handler miss error");
                }
            }
        });
    }

    /**
     * 初始化接口参数
     * @param dev
     * @param versionid
     * @return
     */
    public RequestBody getInitRequestBody(int dev,int versionid,String tokenString){
        FormBody.Builder builder = new FormBody.Builder();
        builder.add("appdev",String.valueOf(dev))
                .add("versionid",String.valueOf(versionid))
                .add("token",tokenString)
                .add("Content-Type","application/json; charset=utf-8");
        return builder.build();
    }

    /**
     * 查询订阅状态
     * @param telNum
     * @return
     */
    public RequestBody getCheckSubRequestBody(String telNum,String tokenString){
        FormBody.Builder builder = new FormBody.Builder();
        builder.add("msisdn",telNum)
                .add("token", tokenString)
                .add("Content-Type","application/json; charset=utf-8");
        return builder.build();
    }

    /**
     * 查询所有激活的分类
     * @return
     */
    public RequestBody getCategoryRequestBody(){
        FormBody.Builder builder = new FormBody.Builder();
        builder.add("tree","1")
                .add("token",MemExchange.getInstance().getTokenData().getToken())
                .add("Content-Type","application/json; charset=utf-8");
        return builder.build();
    }

    /**
     * 统计信息
     * @param jdata
     * @return
     */
    public RequestBody getAnalyseRequestBody(String jdata){
        FormBody.Builder builder = new FormBody.Builder();
        builder.add("jdata",jdata)
                .add("token",MemExchange.getInstance().getTokenData().getToken())
                .add("Content-Type","application/json; charset=utf-8");
        return builder.build();
    }


    /**
     * get请求
     * @param url
     * @param handler
     * @param iRequestType
     */
    public void request(final String url, final Handler handler,
                        final int iRequestType, final RequestBean reqBean){
        getRequestExecutor().execute(new Runnable() {
            @Override
            public void run() {
                Logger.e(Logger.DEBUG_TAG,"请求:"+url);
//                if(iRequestType!=ApiUtils.requestToken) {
                    if(reqBean!=null) {
                        MemExchange.getInstance().saveLastestReqBean(reqBean);
                    }
//                }

                String sResult = BaseProcessor.getInstance().getDataFromApi(url);
                if(TextUtils.isEmpty(sResult)){
                    Logger.e(Logger.DEBUG_TAG,"exception");
//                    Logger.e(Logger.DEBUG_TAG,"网络不可用");
//                    sendResultMessage("",handler,iRequestType,MSG_NETNOWR_ERROR);
                    return;
                }
                if(handler!=null){
                    Logger.i(Logger.DEBUG_TAG,""+sResult.toString());
                    sendRequestResultMessage(sResult,handler,iRequestType);
                }
            }
        });
    }


    private void sendRequestResultMessage(String sResult,Handler handler,int iRequestType) {
        if(isHttpResultValid(sResult)) {
            MemExchange.getInstance().removeLastRequestBean(iRequestType);

            sendResultMessage(sResult, handler, iRequestType, MSG_REQUEST_FINISH);
        }else{
            sendResultMessage(sResult, handler, iRequestType, MSG_REQUEST_ERROR);
        }
    }

    /**
     *
     * @param result
     * @return
     */
    private boolean isHttpResultValid(String result){
        BaseBean bean = JsonHelper.getObject(result, BaseBean.class);
        if(bean!=null && bean.getCode() == ApiUtils.reqResSuccess){
            return true;
        }
        return false;
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
