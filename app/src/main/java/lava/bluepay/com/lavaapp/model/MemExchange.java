package lava.bluepay.com.lavaapp.model;

import android.text.TextUtils;

import java.util.ArrayList;
import java.util.List;

import lava.bluepay.com.lavaapp.MixApp;
import lava.bluepay.com.lavaapp.base.RequestBean;
import lava.bluepay.com.lavaapp.common.Logger;
import lava.bluepay.com.lavaapp.common.Utils;
import lava.bluepay.com.lavaapp.model.api.bean.CategoryBean;
import lava.bluepay.com.lavaapp.model.api.bean.CheckSubBean;
import lava.bluepay.com.lavaapp.model.api.bean.InitData;
import lava.bluepay.com.lavaapp.model.api.bean.TokenData;

/**
 * Created by bluepay on 2017/10/19.
 */

public class MemExchange {

    private boolean isTokenInvalid;//token是否失效
    public void setIsTokenInvalid(boolean invalid){
        isTokenInvalid = invalid;
    }
    public boolean getIsTokenInvalid(){
        return isTokenInvalid;
    }
    private int requestTokenTimes;
    public int getRequestTokenTimes(){
        return requestTokenTimes;
    }
    public void addRequestTokenTimes(){
        requestTokenTimes++;
    }
    public void setRequestTokenTimes(int times){
        requestTokenTimes = times;
    }
    private RequestBean requestBean;
    public void saveLastestReqBean(RequestBean bean){
        requestBean = bean;
    }
    public RequestBean getLastestReqBean(){
        return requestBean;
    }
    public void returnTokenToNormal(){
        isTokenInvalid = false;
        requestTokenTimes = 0;
        requestBean = null;
    }
    public ArrayList<String> bugText = new ArrayList<>();

    //region================手机相关====================================

    public static String m_iIMSI = null;
    public static String m_iIMSI1 = null;
    public static String m_iIMSI2 = null;
    public static String m_iIMEI = null;

    public static String m_sPhoneNumber = "";
    public static String m_phone_type ="";//手机号所属运营商
    public static boolean haveSendMsg = false;
    public static void setHaveSendMsg(boolean ifSendMsg){
        haveSendMsg = ifSendMsg;
    }

    public static void setMsNum(String num) {
        m_sPhoneNumber = num;
    }


    private boolean canSee = false;//情况1.除了AIS以外其他运营商可直接观看。情况2.发送了短信但是轮循查询失败的也可直接观看。情况3。轮循查询订阅状态时sim卡丢失了
    public void setCanSee(){
        canSee = true;
    }
    public boolean getCanSee(){
        return canSee;
    }
    //endregion================手机相关====================================

    //手机号码
//    private int MOBILE_PHONE_TYPE;
//    private String telNum;

    //token数据
    TokenData.DataBean tokenData ;
    //初始化数据
    InitData.DataBean initData;
    //查阅是否订阅数据
    CheckSubBean.DataBean checkSubData;

    public boolean ifHaveNoSim(){
        return TextUtils.isEmpty(m_iIMSI);
    }


    private static MemExchange instance;

    public MemExchange() {
        clear();
    }

    public static MemExchange getInstance(){
        if(instance == null){
            instance = new MemExchange();
        }
        return instance;
    }

    public void clear(){
        Logger.e(Logger.DEBUG_TAG,"MemExchange,clear()");

        requestBean = null;
        requestTokenTimes = 0;
        isTokenInvalid = false;
        m_sPhoneNumber = "";
        canSee = false;

        haveSendMsg = false;

        m_iIMSI = "";
        m_iIMSI1 = "";
        m_iIMSI2 = "";
        m_iIMEI = "";

        //初始化三步骤的数据
        tokenData = null;
        initData = null;
        checkSubData = null;

        //图片
        photoPopularPageIndex = 0;
        photoPopularList = null;
        popularHeights = null;

        photoPortrayPageIndex = 0;
        photoPortrayList = null;
        photoPortrayHeights = null;

        photoSceneryPageIndex = 0;
        photoSceneryList = null;
        photoSceneryHeights = null;

        //视频
        videoPopularPageIndex = 0;
        videoPopularList = null;
        videoPopularHeights = null;

        videoFunnyPageIndex = 0;
        videoFunnyList = null;
        videoFunnyHeights = null;

        videoSportPageIndex = 0;
        videoSportList = null;
        videoSportHeights = null;

        //卡通
        cartoonPopularPageIndex = 0;
        cartoonPopularList = null;
        cartoonPopularHeights = null;

        cartoonFunnyPageIndex = 0;
        cartoonFunnyList = null;
        cartoonFunnyHeights = null;

        cartoonHorrorPageIndex = 0;
        cartoonHorrorList = null;
        cartoonHorrorHeights = null;

    }

    public void setTokenData(TokenData.DataBean data){
        if(data == null){
            Logger.e(Logger.DEBUG_TAG,"setTokenData error");
            return;
        }
        tokenData = data;
    }

    public TokenData.DataBean getTokenData(){
        return tokenData;
    }

    public InitData.DataBean getInitData() {
        return initData;
    }

    public void setInitData(InitData.DataBean initData) {
        if(initData == null){
            Logger.e(Logger.DEBUG_TAG,"setInitData error");
        }
        this.initData = initData;
    }

    public CheckSubBean.DataBean getCheckSubData() {
        return checkSubData;
    }

    public void setCheckSubData(CheckSubBean.DataBean checkSubData) {
        if(checkSubData == null){
            Logger.e(Logger.DEBUG_TAG,"setCheckSubData error");
            return;
        }
        this.checkSubData = checkSubData;
    }



    //region=================图片-流行类==================================

    private int photoPopularPageIndex = 0;//图片流行类，当前页。第一页为1
    private List<CategoryBean.DataBeanX.DataBean> photoPopularList;
    private List<Integer> popularHeights;

    public int getPhotoPopularPageIndex() {
        return photoPopularPageIndex;
    }

    public void setPhotoPopularPageIndex(int photoPopularPageIndex) {
        this.photoPopularPageIndex = photoPopularPageIndex;
    }

    public void addPhotoPopularPageIndex(){
        //只有当请求成功返回新的数据后才更改该index
        photoPopularPageIndex ++;
    }


    /**
     * 第一次数据时调用
     * @param list
     */
    public void setPhotoPopularList(List<CategoryBean.DataBeanX.DataBean> list){
        if ((photoPopularList != null) && (list != photoPopularList)){
            photoPopularList = null;
        }
        photoPopularList = list;
        setPopularHeights(true,photoPopularList.size());
    }

    public void setPopularHeights(boolean ifInit,int size){
        if(popularHeights == null){
            popularHeights = new ArrayList<>();
        }
        if(ifInit) {
            popularHeights.clear();
        }
        for (int i = 0; i < size; i++) {
            popularHeights.add(Utils.getViewRandomHeight(MixApp.getContext()));
        }
    }

    public List<CategoryBean.DataBeanX.DataBean> getPhotoPopularList(){
        if(photoPopularList == null){
            photoPopularList = new ArrayList<>();
        }
        return photoPopularList;
    }



    public List<Integer> getPopularHeights(){
        if(popularHeights == null) {
            popularHeights = new ArrayList<>();
            if(getPhotoPopularList() != null && popularHeights.size() ==0) {
                for (int i = 0; i < getPhotoPopularList().size(); i++) {
                    popularHeights.add(Utils.getViewRandomHeight(MixApp.getContext()));
                }
            }
        }
        return popularHeights;
    }

    //endregion=================图片-流行类==================================

    //region=================图片-画像类==================================

    private int photoPortrayPageIndex = 0;//图片流行类，当前页。第一页为1
    private List<CategoryBean.DataBeanX.DataBean> photoPortrayList;
    private List<Integer> photoPortrayHeights;

    public int getPhotoPortrayPageIndex() {
        return photoPortrayPageIndex;
    }

    public void addPhotoPortrayPageIndex() {
        this.photoPortrayPageIndex++;
    }

    public void setPhotoPortrayPageIndex(int photoPortrayPageIndex) {
        this.photoPortrayPageIndex = photoPortrayPageIndex;
    }

    public List<CategoryBean.DataBeanX.DataBean> getPhotoPortrayList() {
        if(photoPortrayList == null){
            photoPortrayList = new ArrayList<>();
        }
        return photoPortrayList;
    }

    public void setPhotoPortrayList(List<CategoryBean.DataBeanX.DataBean> list) {

        if ((photoPortrayList != null) && (list != photoPortrayList)){
            photoPortrayList = null;
        }
        photoPortrayList = list;
        setPopularPortrayHeights(true,photoPortrayList.size());
    }

    public List<Integer> getPhotoPortrayHeights() {
        if(photoPortrayHeights == null){
            photoPortrayHeights = new ArrayList<>();
            if(getPhotoPortrayList() != null && photoPortrayHeights.size() == 0) {
                for (int i = 0; i < getPhotoPortrayList().size(); i++) {
                    photoPortrayHeights.add((int) (450 + Math.random() * 200));
                }
            }
        }
        return photoPortrayHeights;
    }

    public void setPopularPortrayHeights(boolean ifInit,int size) {
        if(photoPortrayHeights == null){
            photoPortrayHeights = new ArrayList<>();
        }
        if(ifInit) {
            photoPortrayHeights.clear();
        }
        for (int i = 0; i < size; i++) {
            photoPortrayHeights.add((int) (450 + Math.random() * 200));
        }

    }
    //endregion=================图片-画像类==================================

    //region=================图片-风景类==================================

    private int photoSceneryPageIndex = 0;//图片流行类，当前页。第一页为1
    private List<CategoryBean.DataBeanX.DataBean> photoSceneryList;
    private List<Integer> photoSceneryHeights;

    public int getPhotoSceneryPageIndex() {
        return photoSceneryPageIndex;
    }

    public void addPhotoSceneryPageIndex() {
        this.photoSceneryPageIndex++;
    }

    public void setPhotoSceneryPageIndex(int photoSceneryPageIndex) {
        this.photoSceneryPageIndex = photoSceneryPageIndex;
    }

    public List<CategoryBean.DataBeanX.DataBean> getPhotoSceneryList() {
        if(photoSceneryList == null){
            photoSceneryList = new ArrayList<>();
        }
        return photoSceneryList;
    }

    public void setPhotoSceneryList(List<CategoryBean.DataBeanX.DataBean> list) {

        if ((photoSceneryList != null) && (list != photoSceneryList)){
            photoSceneryList = null;
        }
        photoSceneryList = list;
        setPopularSceneryHeights(true,photoSceneryList.size());
    }

    public List<Integer> getPhotoSceneryHeights() {
        if(photoSceneryHeights == null){
            photoSceneryHeights = new ArrayList<>();
            if(getPhotoSceneryList() != null && photoSceneryHeights.size() == 0) {
                for (int i = 0; i < getPhotoSceneryList().size(); i++) {
                    photoSceneryHeights.add(Utils.getViewRandomHeight(MixApp.getContext()));
                }
            }
        }
        return photoSceneryHeights;
    }

    public void setPopularSceneryHeights(boolean ifInit,int size) {
        if(photoSceneryHeights == null){
            photoSceneryHeights = new ArrayList<>();
        }
        if(ifInit) {
            photoSceneryHeights.clear();
        }
        for (int i = 0; i < size; i++) {
            photoSceneryHeights.add(Utils.getViewRandomHeight(MixApp.getContext()));
        }

    }
    //endregion=================图片-风景类==================================




    //region=================视频-流行类==================================

    private int videoPopularPageIndex = 0;//图片流行类，当前页。第一页为1
    private List<CategoryBean.DataBeanX.DataBean> videoPopularList;
    private List<Integer> videoPopularHeights;

    public int getVideoPopularPageIndex() {
        return videoPopularPageIndex;
    }

    public void addVideoPopularPageIndex() {
        this.videoPopularPageIndex++;
    }

    public void setVideoPopularPageIndex(int videoPopularPageIndex) {
        this.videoPopularPageIndex = videoPopularPageIndex;
    }

    public List<CategoryBean.DataBeanX.DataBean> getVideoPopularList() {
        if(videoPopularList == null){
            videoPopularList = new ArrayList<>();
        }
        return videoPopularList;
    }

    public void setVideoPopularList(List<CategoryBean.DataBeanX.DataBean> list) {

        if ((videoPopularList != null) && (list != videoPopularList)){
            videoPopularList = null;
        }
        videoPopularList = list;
        setVideoPopularHeights(true,videoPopularList.size());
    }

    public List<Integer> getVideoPopularHeights() {
        if(videoPopularHeights == null){
            videoPopularHeights = new ArrayList<>();
            if(getVideoPopularList() != null && videoPopularHeights.size() == 0) {
                for (int i = 0; i < getVideoPopularList().size(); i++) {
                    videoPopularHeights.add(Utils.getViewRandomHeight(MixApp.getContext()));
                }
            }
        }
        return videoPopularHeights;
    }

    public void setVideoPopularHeights(boolean ifInit,int size) {
        if(videoPopularHeights == null){
            videoPopularHeights = new ArrayList<>();
        }
        if(ifInit) {
            videoPopularHeights.clear();
        }
        for (int i = 0; i < size; i++) {
            videoPopularHeights.add(Utils.getViewRandomHeight(MixApp.getContext()));
        }

    }

    //endregion=================视频-流行类==================================


    //region=================视频-有趣类==================================

    private int videoFunnyPageIndex = 0;//图片流行类，当前页。第一页为1
    private List<CategoryBean.DataBeanX.DataBean> videoFunnyList;
    private List<Integer> videoFunnyHeights;

    public int getVideoFunnyPageIndex() {
        return videoFunnyPageIndex;
    }

    public void addVideoFunnyPageIndex() {
        this.videoFunnyPageIndex++;
    }

    public void setVideoFunnyPageIndex(int videoFunnyPageIndex) {
        this.videoFunnyPageIndex = videoFunnyPageIndex;
    }

    public List<CategoryBean.DataBeanX.DataBean> getVideoFunnyList() {
        if(videoFunnyList == null){
            videoFunnyList = new ArrayList<>();
        }
        return videoFunnyList;
    }

    public void setVideoFunnyList(List<CategoryBean.DataBeanX.DataBean> list) {

        if ((videoFunnyList != null) && (list != videoFunnyList)){
            videoFunnyList = null;
        }
        videoFunnyList = list;
        setVideoFunnyHeights(true,videoFunnyList.size());
    }

    public List<Integer> getVideoFunnyHeights() {
        if(videoFunnyHeights == null){
            videoFunnyHeights = new ArrayList<>();
            if(getVideoFunnyList() != null && videoFunnyHeights.size() == 0) {
                for (int i = 0; i < getVideoFunnyList().size(); i++) {
                    videoFunnyHeights.add(Utils.getViewRandomHeight(MixApp.getContext()));
                }
            }
        }
        return videoFunnyHeights;
    }

    public void setVideoFunnyHeights(boolean ifInit,int size) {
        if(videoFunnyHeights == null){
            videoFunnyHeights = new ArrayList<>();
        }
        if(ifInit) {
            videoFunnyHeights.clear();
        }
        for (int i = 0; i < size; i++) {
            videoFunnyHeights.add(Utils.getViewRandomHeight(MixApp.getContext()));
        }

    }

    //endregion=================视频-有趣类==================================


    //region=================视频-运动类==================================

    private int videoSportPageIndex = 0;//图片流行类，当前页。第一页为1
    private List<CategoryBean.DataBeanX.DataBean> videoSportList;
    private List<Integer> videoSportHeights;

    public int getVideoSportPageIndex() {
        return videoSportPageIndex;
    }

    public void addVideoSportPageIndex() {
        this.videoSportPageIndex++;
    }

    public void setVideoSportPageIndex(int videoSportPageIndex) {
        this.videoSportPageIndex = videoSportPageIndex;
    }

    public List<CategoryBean.DataBeanX.DataBean> getVideoSportList() {
        if(videoSportList == null){
            videoSportList = new ArrayList<>();
        }
        return videoSportList;
    }

    public void setVideoSportList(List<CategoryBean.DataBeanX.DataBean> list) {

        if ((videoSportList != null) && (list != videoSportList)){
            videoSportList = null;
        }
        videoSportList = list;
        setVideoSportHeights(true,videoSportList.size());
    }

    public List<Integer> getVideoSportHeights() {
        if(videoSportHeights == null){
            videoSportHeights = new ArrayList<>();
            if(getVideoSportList() != null && videoSportHeights.size() == 0) {
                for (int i = 0; i < getVideoSportList().size(); i++) {
                    videoSportHeights.add(Utils.getViewRandomHeight(MixApp.getContext()));
                }
            }
        }
        return videoSportHeights;
    }

    public void setVideoSportHeights(boolean ifInit,int size) {
        if(videoSportHeights == null){
            videoSportHeights = new ArrayList<>();
        }
        if(ifInit) {
            videoSportHeights.clear();
        }
        for (int i = 0; i < size; i++) {
            videoSportHeights.add(Utils.getViewRandomHeight(MixApp.getContext()));
        }

    }

    //endregion=================视频-运动类==================================




    //region=================卡通-流行类==================================

    private int cartoonPopularPageIndex = 0;//图片流行类，当前页。第一页为1
    private List<CategoryBean.DataBeanX.DataBean> cartoonPopularList;
    private List<Integer> cartoonPopularHeights;

    public int getCartoonPopularPageIndex() {
        return cartoonPopularPageIndex;
    }

    public void addCartoonPopularPageIndex() {
        this.cartoonPopularPageIndex++;
    }

    public void setCartoonPopularPageIndex(int cartoonPopularPageIndex) {
        this.cartoonPopularPageIndex = cartoonPopularPageIndex;
    }

    public List<CategoryBean.DataBeanX.DataBean> getCartoonPopularList() {
        if(cartoonPopularList == null){
            cartoonPopularList = new ArrayList<>();
        }
        return cartoonPopularList;
    }

    public void setCartoonPopularList(List<CategoryBean.DataBeanX.DataBean> list) {

        if ((cartoonPopularList != null) && (list != cartoonPopularList)){
            cartoonPopularList = null;
        }
        cartoonPopularList = list;
        setCartoonPopularHeights(true,cartoonPopularList.size());
    }

    public List<Integer> getCartoonPopularHeights() {
        if(cartoonPopularHeights == null){
            cartoonPopularHeights = new ArrayList<>();
            if(getCartoonPopularList() != null && cartoonPopularHeights.size() == 0) {
                for (int i = 0; i < getCartoonPopularList().size(); i++) {
                    cartoonPopularHeights.add(Utils.getViewRandomHeight(MixApp.getContext()));
                }
            }
        }
        return cartoonPopularHeights;
    }

    public void setCartoonPopularHeights(boolean ifInit,int size) {
        if(cartoonPopularHeights == null){
            cartoonPopularHeights = new ArrayList<>();
        }
        if(ifInit) {
            cartoonPopularHeights.clear();
        }
        for (int i = 0; i < size; i++) {
            cartoonPopularHeights.add(Utils.getViewRandomHeight(MixApp.getContext()));
        }

    }

    //endregion=================卡通-流行类==================================



    //region=================卡通-有趣类==================================

    private int cartoonFunnyPageIndex = 0;//图片流行类，当前页。第一页为1
    private List<CategoryBean.DataBeanX.DataBean> cartoonFunnyList;
    private List<Integer> cartoonFunnyHeights;

    public int getCartoonFunnyPageIndex() {
        return cartoonFunnyPageIndex;
    }

    public void addCartoonFunnyPageIndex() {
        this.cartoonFunnyPageIndex++;
    }

    public void setCartoonFunnyPageIndex(int cartoonFunnyPageIndex) {
        this.cartoonFunnyPageIndex = cartoonFunnyPageIndex;
    }

    public List<CategoryBean.DataBeanX.DataBean> getCartoonFunnyList() {
        if(cartoonFunnyList == null){
            cartoonFunnyList = new ArrayList<>();
        }
        return cartoonFunnyList;
    }

    public void setCartoonFunnyList(List<CategoryBean.DataBeanX.DataBean> list) {

        if ((cartoonFunnyList != null) && (list != cartoonFunnyList)){
            cartoonFunnyList = null;
        }
        cartoonFunnyList = list;
        setCartoonFunnyHeights(true,cartoonFunnyList.size());
    }

    public List<Integer> getCartoonFunnyHeights() {
        if(cartoonFunnyHeights == null){
            cartoonFunnyHeights = new ArrayList<>();
            if(getCartoonFunnyList() != null && cartoonFunnyHeights.size() == 0) {
                for (int i = 0; i < getCartoonFunnyList().size(); i++) {
                    cartoonFunnyHeights.add(Utils.getViewRandomHeight(MixApp.getContext()));
                }
            }
        }
        return cartoonFunnyHeights;
    }

    public void setCartoonFunnyHeights(boolean ifInit,int size) {
        if(cartoonFunnyHeights == null){
            cartoonFunnyHeights = new ArrayList<>();
        }
        if(ifInit) {
            cartoonFunnyHeights.clear();
        }
        for (int i = 0; i < size; i++) {
            cartoonFunnyHeights.add(Utils.getViewRandomHeight(MixApp.getContext()));
        }

    }

    //endregion=================卡通-有趣类==================================



    //region=================卡通-horror==================================

    private int cartoonHorrorPageIndex = 0;//图片流行类，当前页。第一页为1
    private List<CategoryBean.DataBeanX.DataBean> cartoonHorrorList;
    private List<Integer> cartoonHorrorHeights;

    public int getCartoonHorrorPageIndex() {
        return cartoonHorrorPageIndex;
    }

    public void addCartoonHorrorPageIndex() {
        this.cartoonHorrorPageIndex++;
    }

    public void setCartoonHorrorPageIndex(int cartoonHorrorPageIndex) {
        this.cartoonHorrorPageIndex = cartoonHorrorPageIndex;
    }

    public List<CategoryBean.DataBeanX.DataBean> getCartoonHorrorList() {
        if(cartoonHorrorList == null){
            cartoonHorrorList = new ArrayList<>();
        }
        return cartoonHorrorList;
    }

    public void setCartoonHorrorList(List<CategoryBean.DataBeanX.DataBean> list) {

        if ((cartoonHorrorList != null) && (list != cartoonHorrorList)){
            cartoonHorrorList = null;
        }
        cartoonHorrorList = list;
        setCartoonHorrorHeights(true,cartoonHorrorList.size());
    }

    public List<Integer> getCartoonHorrorHeights() {
        if(cartoonHorrorHeights == null){
            cartoonHorrorHeights = new ArrayList<>();
            if(getCartoonHorrorList() != null && cartoonHorrorHeights.size() == 0) {
                for (int i = 0; i < getCartoonHorrorList().size(); i++) {
                    cartoonHorrorHeights.add(Utils.getViewRandomHeight(MixApp.getContext()));
                }
            }
        }
        return cartoonHorrorHeights;
    }

    public void setCartoonHorrorHeights(boolean ifInit,int size) {
        if(cartoonHorrorHeights == null){
            cartoonHorrorHeights = new ArrayList<>();
        }
        if(ifInit) {
            cartoonHorrorHeights.clear();
        }
        for (int i = 0; i < size; i++) {
            cartoonHorrorHeights.add(Utils.getViewRandomHeight(MixApp.getContext()));
        }

    }

    //endregion=================卡通-horror==================================



}
