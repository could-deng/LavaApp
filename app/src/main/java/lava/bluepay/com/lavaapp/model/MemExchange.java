package lava.bluepay.com.lavaapp.model;

import java.util.ArrayList;
import java.util.List;

import lava.bluepay.com.lavaapp.view.bean.PhotoBean;

/**
 * Created by bluepay on 2017/10/19.
 */

public class MemExchange {
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
