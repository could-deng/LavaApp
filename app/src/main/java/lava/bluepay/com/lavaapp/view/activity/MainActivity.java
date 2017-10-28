package lava.bluepay.com.lavaapp.view.activity;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
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

import lava.bluepay.com.lavaapp.Config;
import lava.bluepay.com.lavaapp.R;
import lava.bluepay.com.lavaapp.base.WeakHandler;
import lava.bluepay.com.lavaapp.common.JsonHelper;
import lava.bluepay.com.lavaapp.common.Logger;
import lava.bluepay.com.lavaapp.common.Utils;
import lava.bluepay.com.lavaapp.common.pay.PayHelper;
import lava.bluepay.com.lavaapp.common.pay.SmsReceiver;
import lava.bluepay.com.lavaapp.model.MemExchange;
import lava.bluepay.com.lavaapp.model.api.ApiUtils;
import lava.bluepay.com.lavaapp.model.api.MD5Util;
import lava.bluepay.com.lavaapp.model.api.bean.CategoryBean;
import lava.bluepay.com.lavaapp.model.api.bean.CategoryListBean;
import lava.bluepay.com.lavaapp.model.api.bean.CheckSubBean;
import lava.bluepay.com.lavaapp.model.api.bean.InitData;
import lava.bluepay.com.lavaapp.model.api.bean.TokenData;
import lava.bluepay.com.lavaapp.model.process.RequestManager;
import lava.bluepay.com.lavaapp.view.fragment.CartoonFragment;
import lava.bluepay.com.lavaapp.view.fragment.PhotoFragment;
import lava.bluepay.com.lavaapp.view.fragment.VideoFragment;

public class MainActivity extends BaseActivity {


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

    SmsReceiver mReceiver;


    private CheckHandler myHandler;
    /**轮循查询订阅状态 */
    public static final int MSG_RECHECK_SUB_SITUATION = 100;

    private int nowCheckTime;
    private boolean isInCheck = false;

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
    private static class CheckHandler extends WeakHandler{

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
     */
    public void continueCheckSubSituation(){
        isInCheck = true;
        getCheckHandler().sendEmptyMessageDelayed(MainActivity.MSG_RECHECK_SUB_SITUATION,Config.reCheckSubTimeSeparator);

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
    //endregion==============订阅业务===================


    //region==============初始化业务===================

    private ProgressDialog mProgressDialog;

    //初始化的三个阶段
    private static final int NOWInitState0 = 0;
    private static final int NOWInitState1 = 1;
    private static final int NOWInitState2 = 2;
    private static final int NOWInitState3 = 3;

    private int nowInitState = 0;//当前阶段（//todo activity回收的情况）
    private boolean isInInitState = false;
    public ProgressDialog getmProgressDialog(){
        return mProgressDialog;
    }

    /**
     * 初始化app（请求token、请求初始化字段、请求订阅状态）
     * 分三个阶段
     */
    private void initApp(int nowState){
        if(!mProgressDialog.isShowing()){
            mProgressDialog.show();
        }
        switch (nowState){
            case MainActivity.NOWInitState0://去请求token
                nowInitState = MainActivity.NOWInitState0;

                sendGetTokenRequest();
                break;
            case MainActivity.NOWInitState1://去请求初始化信息
                sendInitRequest(Config.DeviceIdAndroidPhone,Config.AppVersionId);

                break;
            case MainActivity.NOWInitState2://去请求订阅信息

                String telNum = Utils.getIMSI(context);
                MemExchange.m_iIMSI1 = telNum;
                MemExchange.m_iIMSI = MemExchange.m_iIMSI1;

                Logger.e(Logger.DEBUG_TAG,"imsi1="+MemExchange.m_iIMSI1+",imsi2="+MemExchange.m_iIMSI2+",imsi="+MemExchange.m_iIMSI
                +",imei="+MemExchange.m_iIMEI+",pho="+MemExchange.m_sPhoneNumber);

                if(!TextUtils.isEmpty(telNum)){
                    sendCheckSubRequest(telNum);
                }else{
                    MemExchange.getInstance().setHaveNoSim();
                    nowInitState = MainActivity.NOWInitState3;
                    initApp(nowInitState);
                }
                break;
            case MainActivity.NOWInitState3:
                if(mProgressDialog!=null && mProgressDialog.isShowing()){
                    mProgressDialog.dismiss();
                }
                isInInitState =false;
                //请求第一页数据
                sendCategoryDataListRequest(1,Config.CategoryPhotoPopular,ApiUtils.requestPhotoPopular);
                break;
        }

    }

    //region==============初始化业务===================

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //1. Android 6.0申请权限
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT_WATCH) {
//            getPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE});
        }

        initToolbar();
        initViews();
        isInInitState = true;
        initApp(MainActivity.NOWInitState0);

//        switchToFragment(FRAGMENT_PHOTO);
//        loadDataForPhotoFragment();
    }

    @Override
    protected void onResumeFragments() {
        Logger.i(Logger.DEBUG_TAG,"MainActivity,onResumeFragments()");
        super.onResumeFragments();
        if(currentNavIndex == FRAGMENT_PHOTO || currentNavIndex == -1){
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



    /**
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

        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setTitle("");
        mProgressDialog.setMessage(getResources().getString(R.string.app_initial));
        mProgressDialog.setCanceledOnTouchOutside(false);
        mProgressDialog.setCancelable(false);
        mProgressDialog.setIndeterminate(true);


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
                rb_fragment_photo.setBackgroundColor(getResources().getColor(R.color.activity_main_nav_radio_bg_check));
                rb_fragment_video.setBackgroundColor(getResources().getColor(R.color.activity_main_nav_radio_bg));
                rb_fragment_cartoon.setBackgroundColor(getResources().getColor(R.color.activity_main_nav_radio_bg));
                break;
            case FRAGMENT_VIDEO:
                rb_fragment_photo.setBackgroundColor(getResources().getColor(R.color.activity_main_nav_radio_bg));
                rb_fragment_video.setBackgroundColor(getResources().getColor(R.color.activity_main_nav_radio_bg_check));
                rb_fragment_cartoon.setBackgroundColor(getResources().getColor(R.color.activity_main_nav_radio_bg));
                break;
            case FRAGMENT_CARTOON:
                rb_fragment_photo.setBackgroundColor(getResources().getColor(R.color.activity_main_nav_radio_bg));
                rb_fragment_video.setBackgroundColor(getResources().getColor(R.color.activity_main_nav_radio_bg));
                rb_fragment_cartoon.setBackgroundColor(getResources().getColor(R.color.activity_main_nav_radio_bg_check));
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
            RequestManager.getInstance().request(sRequest, getMyHandler(), ApiUtils.requestToken);
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
            RequestManager.getInstance().requestByPost(sRequest, getMyHandler(), RequestManager.getInstance().getInitRequestBody(dev, versionid,bean.getToken()), ApiUtils.requestInit);
        }catch(Exception e){
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
            TokenData.DataBean bean = MemExchange.getInstance().getTokenData();
            if (bean == null || TextUtils.isEmpty(bean.getToken())) {
                Logger.e(Logger.DEBUG_TAG,"请求无效");
                return;
            }
            String sRequest = ApiUtils.getCheckSub();
            RequestManager.getInstance().requestByPost(sRequest, getMyHandler(), RequestManager.getInstance().getCheckSubRequestBody(telNum,bean.getToken()), ApiUtils.requestCheckSub);
        }catch (Exception e){
            e.printStackTrace();
            //todo 上传错误日志
        }
    }

    /**
     * 请求某一类数据请求
     */
    public void sendCategoryDataListRequest(int nowPage, int cateId, int requestType){
        if(MemExchange.getInstance().getTokenData() == null){
            Logger.e(Logger.DEBUG_TAG,"请求无效");
            return;
        }
        String sRequest = ApiUtils.getQuerypage(nowPage,Config.PerPageSize,cateId,MemExchange.getInstance().getTokenData().getToken());
        RequestManager.getInstance().request(sRequest,getMyHandler(),requestType);
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

    }

    @Override
    protected void processRequest(Message msg) {
        super.processRequest(msg);
        String result = getMessgeResult(msg);
        switch (msg.arg1){
            case ApiUtils.requestToken:
                TokenData tokenData = JsonHelper.getObject(result, TokenData.class);
                if(tokenData == null){
                    Logger.e(Logger.DEBUG_TAG,"TokenData null error");
                    return;
                }
                MemExchange.getInstance().setTokenData(tokenData.getData());
                Logger.e(Logger.DEBUG_TAG,"获取token成功");
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
                            MemExchange.getInstance().setCanSee();
                            //todo 刷新界面
                            SubSuccess();
                        }
                    }else{
                        isInCheck = false;
                        nowCheckTime = 0;
                        getCheckHandler().removeCallbacksAndMessages(null);
                        //todo 刷新界面
                        SubSuccess();
                    }
                }
                break;
            case ApiUtils.requestAllCategory:
                CategoryListBean categoryListBean = JsonHelper.getObject(result, CategoryListBean.class);

                Logger.e(Logger.DEBUG_TAG,"获取所有激活分类数据成功");
                break;



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
     * 退出游戏、或者退出支付页面，需要调用Client.exit();方法释放资源
     *
     * */
    public void showExitDialog() {
        // 弹框确认是否退出
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setMessage("Are you Exit LavaApp?");
        builder.setTitle("tips");
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int which) {
                MainActivity.this.finish();
                dialog.dismiss();
                System.exit(0);
            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.create().show();
    }

    /**
     * 订阅dialog
     */
    public void showSubscripDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setMessage("Are you sure to subscribe?");
        builder.setTitle("tips");
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int which) {

                if (Utils.checkPermission(context, Manifest.permission.READ_SMS)){
                    try {
                        mReceiver = new SmsReceiver(context);
                        mReceiver.register();
                        PayHelper.doPay();
                    }catch (Exception e){
                        if(mReceiver!=null){
                            mReceiver.unregister();
                        }
                        e.printStackTrace();
                    }

                }else{
                    Toast.makeText(context,"没有短信权限",Toast.LENGTH_SHORT).show();
                }
            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.create().show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        getCheckHandler().removeCallbacksAndMessages(null);
        getMyHandler().removeCallbacksAndMessages(null);//推出时清空全部消息
    }

    @Override
    public void finish() {
        super.finish();
        MemExchange.getInstance().clear();
    }


}
