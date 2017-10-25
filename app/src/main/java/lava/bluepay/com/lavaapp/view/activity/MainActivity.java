package lava.bluepay.com.lavaapp.view.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;
import android.os.Message;
import android.support.annotation.IdRes;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.KeyEvent;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import lava.bluepay.com.lavaapp.Config;
import lava.bluepay.com.lavaapp.R;
import lava.bluepay.com.lavaapp.common.JsonHelper;
import lava.bluepay.com.lavaapp.common.Logger;
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
                //发送请求
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
     * 请求某一类数据请求
     */
    public void sendCategoryDataListRequest(int nowPage, int cateId, int requestType){
        if(MemExchange.getInstance().getTokenData() == null){
            return;
        }
        String sRequest = ApiUtils.getQuerypage(nowPage,10,cateId,MemExchange.getInstance().getTokenData().getToken());
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
//                sendInitRequest(1,1);
//                sendCheckSubRequest(TextUtils.isEmpty(Utils.getIMSI(MainActivity.this))?Config.defaultTelNum:Utils.getIMSI(MainActivity.this));

                //请求第一页数据
                sendCategoryDataListRequest(1,Config.CategoryPhotoPopular,ApiUtils.requestPhotoPopular);
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



            //图片
            case ApiUtils.requestPhotoPopular:
                CategoryBean beanList = JsonHelper.getObject(result,CategoryBean.class);
                refreshPhotoPopularList(beanList);
                break;
            case ApiUtils.requestPhotoPortray:
                CategoryBean photoPortrayList = JsonHelper.getObject(result,CategoryBean.class);
                refreshPhotoPortray(photoPortrayList);
                break;
            case ApiUtils.requestPhotoScenery:
                CategoryBean photoSceneryList = JsonHelper.getObject(result,CategoryBean.class);
                refreshPhotoScenery(photoSceneryList);
                break;

            //视屏
            case ApiUtils.requestVideoPopular:
                CategoryBean videoPopularList = JsonHelper.getObject(result,CategoryBean.class);
                refreshVideoPopular(videoPopularList);
                break;
            case ApiUtils.requestVideoFunny:
                CategoryBean videoFunnyList = JsonHelper.getObject(result,CategoryBean.class);
                refreshVideoFunny(videoFunnyList);
                break;
            case ApiUtils.requestVideoSport:
                CategoryBean videoSportList = JsonHelper.getObject(result,CategoryBean.class);
                refreshVideoSport(videoSportList);
                break;

            //卡通
            case ApiUtils.requestCartoonPopular:
                CategoryBean cartoonPopularList = JsonHelper.getObject(result,CategoryBean.class);
                refreshCartoonPopular(cartoonPopularList);
                break;
            case ApiUtils.requestCartoonFunny:
                CategoryBean cartoonFunnyList = JsonHelper.getObject(result,CategoryBean.class);
                refreshCartoonFunny(cartoonFunnyList);
                break;
            case ApiUtils.requestCartoonHorror:
                CategoryBean cartoonHorrorList = JsonHelper.getObject(result,CategoryBean.class);
                refreshCartoonHorror(cartoonHorrorList);
                break;
        }
    }




    //endregion=======网络请求=================


    //region=======fragment1相关=================

    /**
     * 刷新流行类图片数据
     * @param beanList
     */
    private void refreshPhotoPopularList(CategoryBean beanList){
        if(beanList!=null && beanList.getData()!=null) {
            //无数据
            if(beanList.getData().getData() == null ||beanList.getData().getData().size() <= 0){
                getPhotoFragment().stopLoadNothing();
                return;
            }else{
                getPhotoFragment().stopLoading();
            }
            //有数据
            if (MemExchange.getInstance().getPhotoPopularPageIndex() == 0) {//请求第一页的数据
                MemExchange.getInstance().setPhotoPopularList(beanList.getData().getData());
                getPhotoFragment().refreshPopular();
            }else{//请求其他页的数据
                MemExchange.getInstance().getPhotoPopularList().addAll(beanList.getData().getData());
                MemExchange.getInstance().setPopularHeights(false,beanList.getData().getData().size());
                getPhotoFragment().refreshPopular();
            }
            MemExchange.getInstance().addPhotoPopularPageIndex();
        }else{
            getPhotoFragment().stopLoadError();
        }
    }

    private void refreshPhotoPortray(CategoryBean beanList){
        if(beanList!=null && beanList.getData()!=null) {
            //无数据
            if(beanList.getData().getData() == null ||beanList.getData().getData().size() <= 0){
                getPhotoFragment().portrayListStopLoadNothing();
                return;
            }else{
                getPhotoFragment().portrayListStopLoading();
            }
            //有数据
            if (MemExchange.getInstance().getPhotoPortrayPageIndex() == 0) {//请求第一页的数据
                MemExchange.getInstance().setPhotoPortrayList(beanList.getData().getData());
                getPhotoFragment().refreshPortray();
            }else{//请求其他页的数据
                MemExchange.getInstance().getPhotoPortrayList().addAll(beanList.getData().getData());
                MemExchange.getInstance().setPopularPortrayHeights(false,beanList.getData().getData().size());
                getPhotoFragment().refreshPortray();
            }
            MemExchange.getInstance().addPhotoPortrayPageIndex();
        }else{
            getPhotoFragment().portrayListStopLoadError();
        }
    }

    private void refreshPhotoScenery(CategoryBean beanList){
        if(beanList!=null && beanList.getData()!=null) {
            //无数据
            if(beanList.getData().getData() == null ||beanList.getData().getData().size() <= 0){
                getPhotoFragment().sceneryListStopLoadNothing();
                return;
            }else{
                getPhotoFragment().sceneryListStopLoading();
            }
            //有数据
            if (MemExchange.getInstance().getPhotoSceneryPageIndex() == 0) {//请求第一页的数据
                MemExchange.getInstance().setPhotoSceneryList(beanList.getData().getData());
                getPhotoFragment().refreshScenery();
            }else{//请求其他页的数据
                MemExchange.getInstance().getPhotoSceneryList().addAll(beanList.getData().getData());
                MemExchange.getInstance().setPopularSceneryHeights(false,beanList.getData().getData().size());
                getPhotoFragment().refreshScenery();
            }
            MemExchange.getInstance().addPhotoSceneryPageIndex();
        }else{
            getPhotoFragment().sceneryListStopLoadError();
        }
    }



    //endregion=======fragment1相关=================


    //region=======fragment2相关=================

    private void refreshVideoPopular(CategoryBean beanList){
        if(beanList!=null && beanList.getData()!=null) {
            //无数据
            if(beanList.getData().getData() == null ||beanList.getData().getData().size() <= 0){
                getVideoFragment().stopLoadNothing();
                return;
            }else{
                getVideoFragment().stopLoading();
            }
            //有数据
            if (MemExchange.getInstance().getVideoPopularPageIndex() == 0) {//请求第一页的数据
                MemExchange.getInstance().setVideoPopularList(beanList.getData().getData());
                getVideoFragment().refreshPopular();
            }else{//请求其他页的数据
                MemExchange.getInstance().getVideoPopularList().addAll(beanList.getData().getData());
                MemExchange.getInstance().setVideoPopularHeights(false,beanList.getData().getData().size());
                getVideoFragment().refreshPopular();
            }
            MemExchange.getInstance().addVideoPopularPageIndex();
        }else{
            getVideoFragment().stopLoadError();
        }
    }




    private void refreshVideoFunny(CategoryBean beanList){
        if(beanList!=null && beanList.getData()!=null) {
            //无数据
            if(beanList.getData().getData() == null ||beanList.getData().getData().size() <= 0){
                getVideoFragment().funnyListStopLoadNothing();
                return;
            }else{
                getVideoFragment().funnyListStopLoading();
            }
            //有数据
            if (MemExchange.getInstance().getVideoFunnyPageIndex() == 0) {//请求第一页的数据
                MemExchange.getInstance().setVideoFunnyList(beanList.getData().getData());
                getVideoFragment().refreshFunny();
            }else{//请求其他页的数据
                MemExchange.getInstance().getVideoFunnyList().addAll(beanList.getData().getData());
                MemExchange.getInstance().setVideoFunnyHeights(false,beanList.getData().getData().size());
                getVideoFragment().refreshFunny();
            }
            MemExchange.getInstance().addVideoFunnyPageIndex();
        }else{
            getVideoFragment().funnyListStopLoadError();
        }
    }

    private void refreshVideoSport(CategoryBean beanList){
        if(beanList!=null && beanList.getData()!=null) {
            //无数据
            if(beanList.getData().getData() == null ||beanList.getData().getData().size() <= 0){
                getVideoFragment().sportListStopLoadNothing();
                return;
            }else{
                getVideoFragment().sportListStopLoading();
            }
            //有数据
            if (MemExchange.getInstance().getVideoSportPageIndex() == 0) {//请求第一页的数据
                MemExchange.getInstance().setVideoSportList(beanList.getData().getData());
                getVideoFragment().refreshSport();
            }else{//请求其他页的数据
                MemExchange.getInstance().getVideoSportList().addAll(beanList.getData().getData());
                MemExchange.getInstance().setVideoSportHeights(false,beanList.getData().getData().size());
                getVideoFragment().refreshSport();
            }
            MemExchange.getInstance().addVideoSportPageIndex();
        }else{
            getVideoFragment().sportListStopLoadError();
        }
    }

    //endregion=======fragment2相关=================


    //region=======fragment3相关=================

    private void refreshCartoonPopular(CategoryBean beanList){
        if(beanList!=null && beanList.getData()!=null) {
            //无数据
            if(beanList.getData().getData() == null ||beanList.getData().getData().size() <= 0){
                getCartoonFragment().stopLoadNothing();
                return;
            }else{
                getCartoonFragment().stopLoading();
            }
            //有数据
            if (MemExchange.getInstance().getCartoonPopularPageIndex() == 0) {//请求第一页的数据
                MemExchange.getInstance().setCartoonPopularList(beanList.getData().getData());
                getCartoonFragment().refreshPopular();
            }else{//请求其他页的数据
                MemExchange.getInstance().getCartoonPopularList().addAll(beanList.getData().getData());
                MemExchange.getInstance().setCartoonPopularHeights(false,beanList.getData().getData().size());
                getCartoonFragment().refreshPopular();
            }
            MemExchange.getInstance().addCartoonPopularPageIndex();
        }else{
            getCartoonFragment().stopLoadError();
        }
    }




    private void refreshCartoonFunny(CategoryBean beanList){
        if(beanList!=null && beanList.getData()!=null) {
            //无数据
            if(beanList.getData().getData() == null ||beanList.getData().getData().size() <= 0){
                getCartoonFragment().funnyListStopLoadNothing();
                return;
            }else{
                getCartoonFragment().funnyListStopLoading();
            }
            //有数据
            if (MemExchange.getInstance().getCartoonFunnyPageIndex() == 0) {//请求第一页的数据
                MemExchange.getInstance().setCartoonFunnyList(beanList.getData().getData());
                getCartoonFragment().refreshFunny();
            }else{//请求其他页的数据
                MemExchange.getInstance().getVideoFunnyList().addAll(beanList.getData().getData());
                MemExchange.getInstance().setVideoFunnyHeights(false,beanList.getData().getData().size());
                getCartoonFragment().refreshFunny();
            }
            MemExchange.getInstance().addCartoonFunnyPageIndex();
        }else{
            getCartoonFragment().funnyListStopLoadError();
        }
    }

    private void refreshCartoonHorror(CategoryBean beanList){
        if(beanList!=null && beanList.getData()!=null) {
            //无数据
            if(beanList.getData().getData() == null ||beanList.getData().getData().size() <= 0){
                getCartoonFragment().horrorListStopLoadNothing();
                return;
            }else{
                getCartoonFragment().horrorListStopLoading();
            }
            //有数据
            if (MemExchange.getInstance().getCartoonHorrorPageIndex() == 0) {//请求第一页的数据
                MemExchange.getInstance().setCartoonHorrorList(beanList.getData().getData());
                getCartoonFragment().refreshHorror();
            }else{//请求其他页的数据
                MemExchange.getInstance().getCartoonHorrorList().addAll(beanList.getData().getData());
                MemExchange.getInstance().setCartoonHorrorHeights(false,beanList.getData().getData().size());
                getCartoonFragment().refreshHorror();
            }
            MemExchange.getInstance().addCartoonHorrorPageIndex();
        }else{
            getCartoonFragment().horrorListStopLoadError();
        }
    }

    //endregion=======fragment3相关=================







//    private boolean ifFragmentCanBeSeen(Fragment fragment){
//        if(fragment.is)
//            re
//    }

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


    //todo 初始化脚本


}
