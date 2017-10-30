package lava.bluepay.com.lavaapp.view.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import lava.bluepay.com.lavaapp.Config;
import lava.bluepay.com.lavaapp.R;
import lava.bluepay.com.lavaapp.base.RequestBean;
import lava.bluepay.com.lavaapp.base.WeakHandler;
import lava.bluepay.com.lavaapp.common.JsonHelper;
import lava.bluepay.com.lavaapp.common.Logger;
import lava.bluepay.com.lavaapp.model.MemExchange;
import lava.bluepay.com.lavaapp.model.api.ApiUtils;
import lava.bluepay.com.lavaapp.model.api.MD5Util;
import lava.bluepay.com.lavaapp.model.api.bean.BaseBean;
import lava.bluepay.com.lavaapp.model.api.bean.TokenData;
import lava.bluepay.com.lavaapp.model.process.RequestManager;
import lava.bluepay.com.lavaapp.view.widget.NewVPIndicator;


/**
 * Created by bluepay on 2017/10/9.
 */

public class BaseActivity extends AppCompatActivity {

    protected Context context;
    protected Toolbar toolbar;
    protected TextView tv_title;
    protected NewVPIndicator indicator;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = this;
    }

    public void initToolbar(){
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        tv_title = (TextView) findViewById(R.id.tv_toolbar_title);
        indicator = (NewVPIndicator) findViewById(R.id.indicator);

        setSupportActionBar(toolbar);
        final ActionBar actionBar = getSupportActionBar();
        if(actionBar!=null){
            if(TextUtils.isEmpty(toolbar.getTitle().toString())){//默认的toolbar标题
                setUITitle(toolbar.getTitle().toString());
            }
            actionBar.setDisplayShowTitleEnabled(false);
        }
    }


    /**
     * toolbar中间设置标题
     * @param title
     */
    protected void setUITitle(String title){
        if(!TextUtils.isEmpty(title)){
            tv_title.setVisibility(View.VISIBLE);
            tv_title.setText(title);
            indicator.setVisibility(View.GONE);
        }
    }

    /**
     * toolbar中间设置viewPager的指示条
     * @param viewpager
     * @param titles
     */
    protected void setUiIndicator(ViewPager viewpager, String[] titles) {
        if(viewpager!=null && titles.length>0){
            tv_title.setVisibility(View.GONE);

            indicator.setmTitles(titles);
            indicator.setViewPager(viewpager);
            indicator.setVisibility(View.VISIBLE);
        }
    }

    private MyHandler myHandler;

    public MyHandler getMyHandler(){
        if(myHandler == null){
            myHandler = new MyHandler(this);
        }
        return myHandler;
    }

    protected static class MyHandler extends WeakHandler{

        public MyHandler(Activity instance) {
            super(instance);
        }

        @Override
        public void handleMessage(Message msg) {
            BaseActivity activity = (BaseActivity) getRefercence();
            if(activity == null){
                return;
            }
            switch (msg.what){
//                case RequestManager.MSG_NETNOWR_ERROR:
//                    Toast.makeText(activity.context,"网络不可用",Toast.LENGTH_SHORT).show();
//                    break;
                case RequestManager.MSG_REQUEST_FINISH:
                    activity.processRequest(msg);
                    break;
                case RequestManager.MSG_REQUEST_ERROR:
                    activity.processReqError(msg);
                    break;
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    /**
     * 请求失败
     * @param msg
     */
    protected  void processReqError(Message msg){
        Logger.e(Logger.DEBUG_TAG,"processReqError(),result = "+msg.getData().getString("resultString"));
        String mResult = msg.getData().getString("resultString");
        BaseBean bean = JsonHelper.getObject(mResult, BaseBean.class);
        switch (bean.getCode()){
            case ApiUtils.HTTP_NETWORK_FAIL:
            case ApiUtils.HTTP_REQUEST_EXCEPTION:
                //todo 网络请求失败
                //初始化过程中

                //轮循查询订阅状态过程中
                //网络异常，退出

                //使用过程中浏览过程中
                //null
                //重新获取token过程中

                MemExchange.getInstance().setRequestTokenTimes(0);

                break;
            case ApiUtils.reqResErrorAuthFail:
            case ApiUtils.reqResErrorAuthError:

                if(MemExchange.getInstance().getRequestTokenTimes()<Config.requestTokenMaxTimes) {
                    MemExchange.getInstance().addRequestTokenTimes();

                    //请求token
                    MemExchange.getInstance().setIsTokenInvalid(true);
                    String sRequest = ApiUtils.getToken(Config.APPID, MD5Util.getMD5String("appid=" + Config.APPID + Config.APPSALT));
                    RequestManager.getInstance().request(sRequest, getMyHandler(), ApiUtils.requestToken,null);
                }else{
                    //todo 清空缓存数据
                    Toast.makeText(context,R.string.request_out_of_date,Toast.LENGTH_SHORT).show();
                    finish();

                }


//                Toast.makeText(context,R.string.request_out_of_date,Toast.LENGTH_SHORT).show();

                break;
        }
    }

    /**
     * 请求成功
     * @param msg
     */
    protected void processRequest(Message msg){
        String result = getMessgeResult(msg);
        switch (msg.arg1) {
            case ApiUtils.requestToken:
                TokenData tokenData = JsonHelper.getObject(result, TokenData.class);
                if (tokenData == null) {
                    Logger.e(Logger.DEBUG_TAG, "TokenData null error");
                    return;
                }
                MemExchange.getInstance().setTokenData(tokenData.getData());

                Logger.e(Logger.DEBUG_TAG, "获取token成功");
                if(MemExchange.getInstance().getIsTokenInvalid()){
                    Logger.e(Logger.DEBUG_TAG,"上一次token失效，现在重新请求上一次请求");
                    MemExchange.getInstance().setRequestTokenTimes(0);
                    //todo 注意同步问题

                    //请求上一次任务
                    RequestBean lastBean = MemExchange.getInstance().getLastestReqBean();
                    if(lastBean != null && lastBean.getRequestType()>=ApiUtils.requestToken){
                        if(lastBean.getRequestType()>ApiUtils.requestAllCategory){
                            String url = ApiUtils.getQuerypage(lastBean.getNowPage(),Config.PerPageSize,lastBean.getCateId(),MemExchange.getInstance().getTokenData().getToken());
                            RequestManager.getInstance().request(url,getMyHandler(),lastBean.getRequestType(),null);
                        }else if(lastBean.getRequestType() == ApiUtils.requestCheckSub){
                            ((MainActivity)context).sendCheckSubRequest(MemExchange.m_iIMSI);
                        }else{
                            Logger.e(Logger.DEBUG_TAG,"last request type = "+lastBean.getRequestType());
                        }
                    }

                    MemExchange.getInstance().setIsTokenInvalid(false);
                }
                break;
        }
    }
    protected String getMessgeResult(Message message){
        Bundle bundle = message.getData();
        if(bundle == null){
            return "";
        }
        String sResult = bundle.getString("resultString");
        if(TextUtils.isEmpty(sResult)){
            return "";
        }
        return sResult;
    }





    public void showExitDialog() {
        // 弹框确认是否退出
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage(R.string.request_out_of_date);
        builder.setTitle("tips");
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int which) {
                ((BaseActivity)context).finish();
                dialog.dismiss();
                System.exit(0);
            }
        });

        builder.setCancelable(false);//不可取消
        builder.create().show();
    }
}
