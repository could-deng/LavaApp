package lava.bluepay.com.lavaapp.view.activity;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Message;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import lava.bluepay.com.lavaapp.Config;
import lava.bluepay.com.lavaapp.R;
import lava.bluepay.com.lavaapp.base.RequestBean;
import lava.bluepay.com.lavaapp.base.WeakHandler;
import lava.bluepay.com.lavaapp.common.JsonHelper;
import lava.bluepay.com.lavaapp.common.Logger;
import lava.bluepay.com.lavaapp.common.UmengAnalysisHelper;
import lava.bluepay.com.lavaapp.model.MemExchange;
import lava.bluepay.com.lavaapp.model.api.ApiUtils;
import lava.bluepay.com.lavaapp.model.api.MD5Util;
import lava.bluepay.com.lavaapp.model.api.bean.BaseBean;
import lava.bluepay.com.lavaapp.model.api.bean.TokenData;
import lava.bluepay.com.lavaapp.model.process.RequestManager;
import lava.bluepay.com.lavaapp.view.dialog.material.DialogAction;
import lava.bluepay.com.lavaapp.view.dialog.material.MaterialDialog;
import lava.bluepay.com.lavaapp.view.widget.NewVPIndicator;


/**
 * Created by bluepay on 2017/10/9.
 */

public class BaseActivity extends AppCompatActivity {

    protected Context context;
    protected Toolbar toolbar;
    protected TextView tv_title;
    protected NewVPIndicator indicator;

    private String mPageName;


    private MaterialDialog permissionDialog;

    /**
     * 设置页面标题,用于友盟统计
     */
    protected void setPageName(String name) {
        mPageName = name;

    }

    public NewVPIndicator getIndicator() {
        return indicator;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        context = this;
        if (mPageName == null) return;
    }

    @Override
    protected void onResume() {
        super.onResume();
        UmengAnalysisHelper.getInstance().onActivityResume(context,mPageName);
    }

    @Override
    protected void onPause() {
        super.onPause();
        UmengAnalysisHelper.getInstance().onActivityPause(context,mPageName);
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
        if(bean == null){
            return;
        }
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
                        }
                        else if(lastBean.getRequestType() == ApiUtils.requestCheckSub){
                            ((MainActivity)context).sendCheckSubRequest(MemExchange.m_iIMSI);
                        }
                        else{
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





    /**
     * 退出游戏、或者退出支付页面，需要调用Client.exit();方法释放资源
     *
     * */
    public void showExitDialog() {
        new MaterialDialog.Builder(this)
                .title(R.string.tips)
                .content(R.string.if_sure_to_exit)
                .positiveText(R.string.sure)
                .positiveColor(getResources().getColor(R.color.colorPrimary))
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(MaterialDialog dialog, DialogAction which) {
                        BaseActivity.this.finish();
                        dialog.dismiss();
                        System.exit(0);
                    }
                })
                .negativeText(R.string.cancel)
                .negativeColor(getResources().getColor(R.color.colorPrimary))
                .onNegative(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(MaterialDialog dialog, DialogAction which) {
                        dialog.dismiss();
                    }
                }).show();
    }



    //region ============================== Android M 权限 ==============================

    /**
     * 申请单个权限的请求
     */
    private static final int REQUEST_CODE_ASK_PERMISSIONS = 185;
    /**
     * 申请多个权限的请求
     */
    private static final int REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS = 186;

    /**
     * 检查是否获得了指定权限
     *
     * @param permissionName 权限名称,如 Manifest.permission.ACCESS_FINE_LOCATION
     * @return true:已获取了权限,false:未获取权限
     */
    protected boolean permissionIsGranted(String permissionName) {
        if (TextUtils.isEmpty(permissionName))
            return false;
        return ContextCompat.checkSelfPermission(this, permissionName) == PackageManager.PERMISSION_GRANTED;
    }

//    /**
//     * 检查并处理权限申请
//     *
//     * @param permissionName permissionName 权限名称,如 Manifest.permission.ACCESS_FINE_LOCATION等
//     */
//    protected void getPermission(final String permissionName) {
//        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
//            return;
//        }
//        boolean hasPermission = permissionIsGranted(permissionName);
//        if (!hasPermission) {
//            if (ActivityCompat.shouldShowRequestPermissionRationale(this, permissionName)) {
//                String message = String.format(getString(R.string.request_permission_format), getPermissionConciseName(permissionName));
//                permissionDialog = new AlertDialog.Builder(this)
////                        .iconRes(R.drawable.ic_notification)
//                        .setTitle(R.string.tips)
//                        .setMessage(message)
//                        .setPositiveButton(getResources().getString(R.string.sure), new DialogInterface.OnClickListener(){
//                            @Override
//                            public void onClick(DialogInterface dialog, int which) {
//                                ActivityCompat.requestPermissions(BaseActivity.this, new String[]{permissionName},
//                                        REQUEST_CODE_ASK_PERMISSIONS);
//                                dialog.dismiss();
//                            }
//                        }).create();
////                        .onAny(new MaterialDialog.SingleButtonCallback() {
////                            @Override
////                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
////                                ActivityCompat.requestPermissions(BaseActivity.this, new String[]{permissionName},
////                                        REQUEST_CODE_ASK_PERMISSIONS);
////                                dialog.dismiss();
////                            }
////                        }).build();
//                if (permissionDialog != null) {
//                    permissionDialog.show();
//                }
//            } else {
//                ActivityCompat.requestPermissions(BaseActivity.this, new String[]{permissionName},
//                        REQUEST_CODE_ASK_PERMISSIONS);
//            }
//        }
//    }

    /**
     * 是否全部权限允许
     * @param permissionNames
     * @return
     */
    protected boolean ifAllPermissionAllo(String[] permissionNames){
        for (String permission : permissionNames) {
            if(!permissionIsGranted(permission)){
                return false;
            }
        }
        return true;
    }

    /**
     * 检查并处理单个或多个权限申请
     *
     * @param permissionNames 权限名称组成的字符串数组,如Manifest.permission.ACCESS_FINE_LOCATION等
     */
    protected void getPermissions(String[] permissionNames) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return;
        }
        List<String> permissionsNeeded = new ArrayList<>();
        final List<String> permissionsList = new ArrayList<>();

        if (permissionNames == null)
            return;

        for (String permissionName : permissionNames) {
            if (!addPermission(permissionsList, permissionName)) {
                permissionsNeeded.add(getPermissionConciseName(permissionName));
            }
        }

        if (permissionsList.size() > 0) {
            if (permissionsNeeded.size() > 0) {
                // Need Rationale
//                String message = permissionsNeeded.get(0);
//                for (int i = 1; i < permissionsNeeded.size(); i++) {
//                    message = message + "\n\t" + permissionsNeeded.get(i);
//                }
//
//                String msg = String.format(getString(R.string.request_permission_format), message);
//                permissionDialog = new MaterialDialog.Builder(this)
////                        .iconRes(R.drawable.ic_notification)
//                        .title(R.string.tips)
//                        .content(msg)
//                        .positiveText(R.string.sure)
//                        .positiveColor(getResources().getColor(R.color.colorPrimary))
//                        .cancelable(false)
//                        .onAny(new MaterialDialog.SingleButtonCallback() {
//                            @Override
//                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                ActivityCompat.requestPermissions(BaseActivity.this,
                                        permissionsList.toArray(new String[permissionsList.size()]),
                                        REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS);
//                                dialog.dismiss();
//                            }
//                        }).build();
//                if (permissionDialog != null) {
//                    permissionDialog.show();
//                }
                return;
            }
            ActivityCompat.requestPermissions(this,
                    permissionsList.toArray(new String[permissionsList.size()]),
                    REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS);
        }
    }

    /**
     * 判断指定权限是否已经被获取
     *
     * @param permissionsList 需要请求权限的权限集合
     * @param permission      指定的权限名称
     * @return true:已获取权限,false:未获取权限
     */
    private boolean addPermission(List<String> permissionsList, String permission) {
        if (!permissionIsGranted(permission)) {
            permissionsList.add(permission);
            // Check for Rationale Option
            if (!ActivityCompat.shouldShowRequestPermissionRationale(this, permission))
                return false;
        }
        return true;
    }

    /**
     * 根据android manifest权限名转换成相应的权限提示信息
     *
     * @param permission manifest权限名,如Manifest.permission.READ_EXTERNAL_STORAGE 等
     */
    private String getPermissionConciseName(String permission) {
        if (TextUtils.isEmpty(permission))
            return "";
        switch (permission) {
            case Manifest.permission.READ_EXTERNAL_STORAGE://读写外部存储器权限
            case Manifest.permission.WRITE_EXTERNAL_STORAGE:
                return getString(R.string.external_storage_permission_concise);
            case Manifest.permission.READ_PHONE_STATE://电话状态
                return getString(R.string.phone_permission_concise);
            case Manifest.permission.SEND_SMS:
                return getString(R.string.sms_permission_concise);

        }
        return "";
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
//        for (int i = 0; i < permissions.length && i < grantResults.length; i++) {
//            Logger.i(Logger.DEBUG_TAG, "onRequestPermissionsResult-->permission:" + permissions[i] + " status:" + grantResults[i]);
//        }
        switch (requestCode) {
            case REQUEST_CODE_ASK_PERMISSIONS://单个权限请求
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {//权限被允许
                    if (permissions[0] != null) {
                        handlePermissionAllowed(permissions[0]);
                    }
                } else {//权限被禁止
                    if (permissions[0] != null) {
                        String message = String.format(Locale.getDefault(), getString(R.string.permission_forbid_message_format),
                                getPermissionConciseName(permissions[0]));
//                        showAppMessage(message, AppMsg.STYLE_ALERT);
                        Toast.makeText(context,message,Toast.LENGTH_SHORT).show();
                        handlePermissionForbidden(permissions[0]);
                    }
                }
                break;
            case REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS://多个权限请求
                String msg = "";
                if (permissions != null && grantResults != null) {
                    for (int i = 0; i < permissions.length && i < grantResults.length; i++) {
                        if (permissions[i] != null) {
                            if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {//权限被允许
//                                handlePermissionAllowed(permissions[i]);
                            } else {//权限被禁止
                                msg += "\n\t\t" + getPermissionConciseName(permissions[i]);
//                                handlePermissionForbidden(permissions[i]);
                            }
                        }
                    }
                    if (msg != null && msg.length() > 0) {
                        String message = String.format(Locale.getDefault(), getString(R.string.permission_forbid_message_format), msg);
                        if (message != null)
                            permissionDialog = new MaterialDialog.Builder(this)
//                                    .iconRes(R.drawable.ic_notification)
                                    .title(R.string.tips)
                                    .content(message)
                                    .positiveText(getResources().getString(R.string.sure))
                                    .positiveColor(getResources().getColor(R.color.colorPrimary))
                                    .cancelable(false)
                                    .onAny(new MaterialDialog.SingleButtonCallback() {
                                        @Override
                                        public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                            dialog.dismiss();
                                            switch (which) {
                                                case POSITIVE:
                                                    openAppDetail();
                                                    break;
                                            }
                                        }
                                    }).build();
                        if (permissionDialog != null) {
                            permissionDialog.show();
                        }
                    }else{
                        handlePermissionAllowed("");
                    }
                }
                break;
//            default:
//                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    /**
     * 应用权限被禁止后续处理
     *
     * @param permissionName manifest权限名,如Manifest.permission.READ_EXTERNAL_STORAGE 等
     */
    protected void handlePermissionForbidden(String permissionName) {
    }

    /**
     * 应用权限被允许后续处理
     *
     * @param permissionName manifest权限名,如Manifest.permission.READ_EXTERNAL_STORAGE 等
     */
    protected void handlePermissionAllowed(String permissionName) {
    }

    /**
     * 打开系统设置中的乐享动详情界面
     */
    protected void openAppDetail() {
        Intent intent = new Intent();
        intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package", getPackageName(), null);
        intent.setData(uri);
        if (intent != null && intent.resolveActivity(getPackageManager()) != null) {
//            startActivity(intent);
            startActivityForResult(intent,MainActivity.RequestCodeIntentToPermissionSetting);
        }
    }

    //endregion ============================== Android M 权限 ==============================
}
