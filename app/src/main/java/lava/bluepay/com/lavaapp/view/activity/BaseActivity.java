package lava.bluepay.com.lavaapp.view.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
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

import lava.bluepay.com.lavaapp.R;
import lava.bluepay.com.lavaapp.base.WeakHandler;
import lava.bluepay.com.lavaapp.common.JsonHelper;
import lava.bluepay.com.lavaapp.common.Logger;
import lava.bluepay.com.lavaapp.model.api.bean.BaseBean;
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
                case RequestManager.MSG_NETNOWR_ERROR:
                    Toast.makeText(activity.context,"网络不可用",Toast.LENGTH_SHORT).show();
                    break;
                case RequestManager.MSG_REQUEST_FINISH:
                    activity.processRequest(msg);
                    break;
                case RequestManager.MSG_REQUEST_ERROR:
                    activity.processReqError(msg);
                    break;
            }
        }
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
            case 1001:
                Toast.makeText(context,R.string.request_out_of_date,Toast.LENGTH_SHORT).show();
                finish();
                break;
        }
    }

    /**
     * 请求成功
     * @param msg
     */
    protected void processRequest(Message msg){

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
