package lava.bluepay.com.lavaapp.view.activity;

import android.app.AlertDialog;
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

import java.util.ArrayList;
import java.util.List;

import lava.bluepay.com.lavaapp.Config;
import lava.bluepay.com.lavaapp.R;
import lava.bluepay.com.lavaapp.common.JsonHelper;
import lava.bluepay.com.lavaapp.common.Logger;
import lava.bluepay.com.lavaapp.common.Utils;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //1. Android 6.0申请权限
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT_WATCH) {
//            getPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,
//                    Manifest.permission.BODY_SENSORS,
//                    Manifest.permission.ACCESS_FINE_LOCATION});
        }

        initToolbar();
        initViews();
        sendGetTokenRequest();

//
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
//                sendPhotoPopularListRequest();
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
        rg_select_fragment = (RadioGroup) findViewById(R.id.rg_select_fragment);
        rb_fragment_photo = (RadioButton) findViewById(R.id.tv_fragment_photo);
        rb_fragment_video = (RadioButton) findViewById(R.id.tv_fragment_video);
        rb_fragment_cartoon = (RadioButton) findViewById(R.id.tv_fragment_cartoon);
        rg_select_fragment.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {
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

            if(ApiUtils.isNetWorkAvailable()) {
//                sendPhotoPopularListRequest();
            }else{
                Logger.e(Logger.DEBUG_TAG,"MainActivity,switchToFragment,photoFragment,net work not work");
                //todo 提示网络不可用
            }
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
        String sRequest = ApiUtils.getToken(Config.APPID, MD5Util.getMD5String("appid="+Config.APPID+Config.APPSALT));
        RequestManager.getInstance().request(sRequest,getMyHandler(),ApiUtils.requestToken);
    }

    /**
     * 请求初始化数据
     * 客户端设备id 1安卓pad 2安卓手机 3ios手机 4iospad
     * versionid 设备的版本
     */
    public void sendInitRequest(int dev, int versionid){
        if(MemExchange.getInstance().getTokenData() == null){
            return;
        }
        String sRequest = ApiUtils.getInit();
        RequestManager.getInstance().requestByPost(sRequest,getMyHandler(),RequestManager.getInstance().getInitRequestBody(dev,versionid),ApiUtils.requestInit);
    }

    /**
     * 请求查询用户订阅状态
     * @param telNum
     */
    public void sendCheckSubRequest(String telNum){
        if(MemExchange.getInstance().getTokenData() == null){
            return;
        }
        String sRequest = ApiUtils.getCheckSub();
        RequestManager.getInstance().requestByPost(sRequest,getMyHandler(),RequestManager.getInstance().getCheckSubRequestBody(telNum),ApiUtils.requestCheckSub);
    }

    /**
     * 请求图片流行类数据
     */
    public void sendPhotoPopularListRequest(int nowPage,int cateId){
        if(MemExchange.getInstance().getTokenData() == null){
            return;
        }
        String sRequest = ApiUtils.getQuerypage(nowPage,10,cateId,MemExchange.getInstance().getTokenData().getToken());
        RequestManager.getInstance().request(sRequest,getMyHandler(),ApiUtils.requestPhotoPopular);
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
//                sendInitRequest(1,1);
//                sendCheckSubRequest(TextUtils.isEmpty(Utils.getIMSI(MainActivity.this))?Config.defaultTelNum:Utils.getIMSI(MainActivity.this));

                sendPhotoPopularListRequest(MemExchange.getInstance().getPhotoPopularPageIndex(),0);
                break;
            case ApiUtils.requestInit:
                InitData initData = JsonHelper.getObject(result,InitData.class);
                MemExchange.getInstance().setInitData(initData.getData());
                Logger.e(Logger.DEBUG_TAG,"初始化成功");
                break;

            case ApiUtils.requestCheckSub:
                CheckSubBean subBean = JsonHelper.getObject(result, CheckSubBean.class);
                MemExchange.getInstance().setCheckSubData(subBean.getData());
                Logger.e(Logger.DEBUG_TAG,"查询订阅状态成功");
                break;
            case ApiUtils.requestAllCategory:
                CategoryListBean categoryListBean = JsonHelper.getObject(result, CategoryListBean.class);

                Logger.e(Logger.DEBUG_TAG,"获取所有激活分类数据成功");
                break;
            case ApiUtils.requestPhotoPopular:
                //通过gson转换为bean类
                //todo 刷新数据
                CategoryBean beanList = JsonHelper.getObject(result,CategoryBean.class);
                MemExchange.getInstance().setPhotoPopularList(beanList);
                getPhotoFragment().refreshPopular();
                break;
        }
    }




    //endregion=======网络请求=================





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

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void finish() {
        super.finish();
        MemExchange.getInstance().clear();
    }


    //初始化脚本


}
