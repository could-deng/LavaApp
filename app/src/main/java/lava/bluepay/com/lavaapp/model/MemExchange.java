package lava.bluepay.com.lavaapp.model;

import java.util.ArrayList;
import java.util.List;

import lava.bluepay.com.lavaapp.common.Logger;
import lava.bluepay.com.lavaapp.model.api.bean.CategoryListBean;
import lava.bluepay.com.lavaapp.model.api.bean.CheckSubBean;
import lava.bluepay.com.lavaapp.model.api.bean.InitData;
import lava.bluepay.com.lavaapp.model.api.bean.TokenData;
import lava.bluepay.com.lavaapp.view.bean.PhotoBean;

/**
 * Created by bluepay on 2017/10/19.
 */

public class MemExchange {

    //手机号码
    private int MOBILE_PHONE_TYPE;
    private String telNum;

    //token数据
    TokenData.DataBean tokenData ;
    //初始化数据
    InitData.DataBean initData;
    //查阅是否订阅数据
    CheckSubBean.DataBean checkSubData;

    private List<PhotoBean> photoPopularList;
    private List<Integer> popularHeights;


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
        photoPopularList = null;
        popularHeights = null;
        tokenData = null;
        initData = null;
        checkSubData = null;
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


    public void setPhotoPopularList(List<PhotoBean> list){
        if ((photoPopularList != null) && (list != photoPopularList)){
            photoPopularList.clear();
        }
        photoPopularList = list;

        if(popularHeights == null){
            popularHeights = new ArrayList<>();
        }
        popularHeights.clear();
        for (int i = 0; i < photoPopularList.size(); i++) {
            popularHeights.add((int) (300 + Math.random() * 300));
        }

    }

    public List<PhotoBean> getPhotoPopularList(){
        if(photoPopularList == null){
            photoPopularList = new ArrayList<>();
            for (int i = 'A'; i < 'E'; i++)
            {
                photoPopularList.add(new PhotoBean(""+(char)i,"http://photocdn.sohu.com/20121119/Img358016160.jpg"));
            }

        }
        return photoPopularList;
    }
    public List<Integer> getPopularHeights(){
        if(popularHeights == null) {
            popularHeights = new ArrayList<>();
            for (int i = 0; i < getPhotoPopularList().size(); i++) {
                popularHeights.add((int) (300 + Math.random() * 300));
            }
        }
        return popularHeights;
    }


}
