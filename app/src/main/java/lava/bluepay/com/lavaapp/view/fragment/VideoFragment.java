package lava.bluepay.com.lavaapp.view.fragment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.umeng.analytics.MobclickAgent;

import java.util.ArrayList;
import java.util.List;

import lava.bluepay.com.lavaapp.Config;
import lava.bluepay.com.lavaapp.R;
import lava.bluepay.com.lavaapp.common.Logger;
import lava.bluepay.com.lavaapp.model.MemExchange;
import lava.bluepay.com.lavaapp.model.api.ApiUtils;
import lava.bluepay.com.lavaapp.model.api.bean.CategoryBean;
import lava.bluepay.com.lavaapp.model.api.bean.CheckSubBean;
import lava.bluepay.com.lavaapp.view.activity.MainActivity;
import lava.bluepay.com.lavaapp.view.activity.PlayVideoActivity;
import lava.bluepay.com.lavaapp.view.adapter.RecyclerViewAdapter;
import lava.bluepay.com.lavaapp.view.adapter.ViewPagerAdapter;
import lava.bluepay.com.lavaapp.view.widget.EmptyRecyclerView;
import lava.bluepay.com.lavaapp.view.widget.swiperefresh.SwipeLoadLayout;

/**
 * Created by bluepay on 2017/10/9.
 */

public class VideoFragment extends BaseFragment {

    public static final String TAG = "videoFragment";

    private ViewPager vp_video;

    //region=========页面刷新相关==============
    public int getVPNowIndex(){
        int nowIndex = -1;
        if(vp_video!=null){
            nowIndex = vp_video.getCurrentItem();
        }
        return nowIndex;
    }
    public void notifyIndexAdapter(int index){

        switch (index){
            case 0:
                rv_video_popular.smoothScrollToPosition(0);
                break;
            case 1:
                rv_video_funny.smoothScrollToPosition(0);
                break;
            case 2:
                rv_video_sport.smoothScrollToPosition(0);
                break;
        }
        if(rvPopularAdapter!=null) {
            rvPopularAdapter.notifyItemRangeChanged(0, (getPopularList().size() < 4) ? getPopularList().size() : 4);
        }
        if(rvFunnyAdapter!=null) {
            rvFunnyAdapter.notifyItemRangeChanged(0, (getFunnyList().size() < 4) ? getFunnyList().size() : 4);
        }
        if(rvSportAdapter!=null) {
            rvSportAdapter.notifyItemRangeChanged(0, (getSportList().size() < 4) ? getSportList().size() : 4);
        }
    }

    //endregion=========页面刷新相关==============


    //region=========类别1==============

    private SwipeLoadLayout swipe_container_video_popular;
    private EmptyRecyclerView rv_video_popular;
    private RecyclerViewAdapter rvPopularAdapter;
    private SwipeLoadLayout.OnLoadMoreListener rvPopularLoadMoreListener;

    //endregion=========类别1==============

    //region=========类别2==============

    private SwipeLoadLayout swipe_container_video_funny;
    private EmptyRecyclerView rv_video_funny;
    private RecyclerViewAdapter rvFunnyAdapter;
    private SwipeLoadLayout.OnLoadMoreListener rvFunnyLoadMoreListener;

    //endregion=========类别2==============

    //region=========类别3==============

    private SwipeLoadLayout swipe_container_video_sport;
    private EmptyRecyclerView rv_video_sport;
    private RecyclerViewAdapter rvSportAdapter;
    private SwipeLoadLayout.OnLoadMoreListener rvSportLoadMoreListener;


    //endregion=========类别3==============


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        return initView(inflater,container);
    }


    private View initView(final LayoutInflater inflater, ViewGroup container){
        View view  = inflater.inflate(R.layout.fragment_video,container,false);

        List<View> views = new ArrayList<>();




        //region==========类别1===========================================================================



        View popularView = inflater.inflate(R.layout.fragment_video_popular,null);

        //下拉控件
        swipe_container_video_popular = (SwipeLoadLayout) popularView.findViewById(R.id.swipe_container_video_popular);
        swipe_container_video_popular.setLoadingMore(false);
        rvPopularLoadMoreListener = new SwipeLoadLayout.OnLoadMoreListener() {
            @Override
            public void onLoadMore() {
                ((MainActivity)getActivity()).sendCategoryDataListRequest(MemExchange.getInstance().getVideoPopularPageIndex()+1, Config.CategoryVideoPopular, ApiUtils.requestVideoPopular);
            }
        };
        swipe_container_video_popular.setOnLoadMoreListener(rvPopularLoadMoreListener);

        //adapter
        rvPopularAdapter = new RecyclerViewAdapter(getActivity(),Config.CategoryVideoPopular);
        rvPopularAdapter.setItemClickListener(new RecyclerViewAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
//                if(MemExchange.getInstance().ifHaveNoSim()){
//                    Toast.makeText((getActivity()),getActivity().getResources().getString(R.string.sms_miss_can_not_see),Toast.LENGTH_SHORT).show();
//                    return;
//                }
                //订阅了的则进入
                if(CheckSubBean.ifHaveSubscribe(MemExchange.m_iIMSI)) {
                    if (rvPopularAdapter.getmDatas() != null) {
                        CategoryBean.DataBeanX.DataBean bean = rvPopularAdapter.getmDatas().get(position);
                        if (bean == null || TextUtils.isEmpty(bean.getTitle()) || TextUtils.isEmpty(bean.getSeeds())) {
                            return;
                        }
                        Intent intent = new Intent();
                        Bundle data = new Bundle();
                        data.putString("title", bean.getTitle());
                        data.putString("urlPath", bean.getSeeds());
                        intent.putExtras(data);
                        intent.setClass(getContext(), PlayVideoActivity.class);

                        startActivity(intent);
                    }
                }else{
                    //未订阅的则提示是否订阅
//                    if(((MainActivity)getActivity()).getIsInCheck()){
//                        Toast.makeText(getContext(),getResources().getString(R.string.try_later),Toast.LENGTH_SHORT).show();
//                        return;
//                    }
                    ((MainActivity)getActivity()).showSubscripDialog();
                }
            }

            @Override
            public void onItemLongClick(View view, int position) {
                Logger.e(Logger.DEBUG_TAG,"onItemLongClick");
            }

        });

        //recyclerView
        rv_video_popular = (EmptyRecyclerView) popularView.findViewById(R.id.swipe_target);
        rv_video_popular.setLayoutManager(new StaggeredGridLayoutManager(2,StaggeredGridLayoutManager.VERTICAL));//改为Horizontal则表现为滚动的GridView效果
        rv_video_popular.setItemAnimator(new DefaultItemAnimator());//动画
        rv_video_popular.setEmptyView(inflater.inflate(R.layout.view_empty,null));
        rv_video_popular.setAdapter(rvPopularAdapter);



        views.add(popularView);


        //endregion==========类别1===========================================================================

        //region==========类别2===========================================================================

        View funnyView = inflater.inflate(R.layout.fragment_video_funny,null);

        //下拉控件
        swipe_container_video_funny = (SwipeLoadLayout) funnyView.findViewById(R.id.swipe_container_video_funny);
        swipe_container_video_funny.setLoadingMore(false);
        rvFunnyLoadMoreListener = new SwipeLoadLayout.OnLoadMoreListener() {
            @Override
            public void onLoadMore() {
                ((MainActivity)getActivity()).sendCategoryDataListRequest(MemExchange.getInstance().getVideoFunnyPageIndex()+1, Config.CategoryVideoFunny, ApiUtils.requestVideoFunny);
            }
        };
        swipe_container_video_funny.setOnLoadMoreListener(rvFunnyLoadMoreListener);

        //adapter
        rvFunnyAdapter = new RecyclerViewAdapter(getActivity(),Config.CategoryVideoFunny);
        rvFunnyAdapter.setItemClickListener(new RecyclerViewAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
//                if(MemExchange.getInstance().ifHaveNoSim()){
//                    Toast.makeText((getActivity()),getActivity().getResources().getString(R.string.sms_miss_can_not_see),Toast.LENGTH_SHORT).show();
//                    return;
//                }
                //订阅了的则进入
                if(CheckSubBean.ifHaveSubscribe(MemExchange.m_iIMSI)) {
                    CategoryBean.DataBeanX.DataBean bean = rvFunnyAdapter.getmDatas().get(position);
                    if (bean == null || TextUtils.isEmpty(bean.getTitle()) || TextUtils.isEmpty(bean.getSeeds())) {
                        return;
                    }
                    Intent intent = new Intent();
                    Bundle data = new Bundle();
                    data.putString("title", bean.getTitle());
                    data.putString("urlPath", bean.getSeeds());
                    intent.putExtras(data);
                    intent.setClass(getContext(), PlayVideoActivity.class);

                    startActivity(intent);
                }else{
//                    if(((MainActivity)getActivity()).getIsInCheck()){
//                        Toast.makeText(getContext(),getResources().getString(R.string.try_later),Toast.LENGTH_SHORT).show();
//                        return;
//                    }
                    //未订阅的则提示是否订阅
                    ((MainActivity)getActivity()).showSubscripDialog();
                }
            }

            @Override
            public void onItemLongClick(View view, int position) {
                Logger.e(Logger.DEBUG_TAG,"onItemLongClick");
            }

        });

        //recyclerView
        rv_video_funny = (EmptyRecyclerView) funnyView.findViewById(R.id.swipe_target);
        rv_video_funny.setLayoutManager(new StaggeredGridLayoutManager(2,StaggeredGridLayoutManager.VERTICAL));//改为Horizontal则表现为滚动的GridView效果
        rv_video_funny.setItemAnimator(new DefaultItemAnimator());//动画
        rv_video_funny.setEmptyView(inflater.inflate(R.layout.view_empty,null));
        rv_video_funny.setAdapter(rvFunnyAdapter);


        views.add(funnyView);

        //endregion==========类别2===========================================================================

        //region==========类别3===========================================================================

        View sportsView = inflater.inflate(R.layout.fragment_video_sports,null);

        //下拉控件
        swipe_container_video_sport = (SwipeLoadLayout) sportsView.findViewById(R.id.swipe_container_video_sport);
        swipe_container_video_sport.setLoadingMore(false);
        rvSportLoadMoreListener = new SwipeLoadLayout.OnLoadMoreListener() {
            @Override
            public void onLoadMore() {
                ((MainActivity)getActivity()).sendCategoryDataListRequest(MemExchange.getInstance().getVideoSportPageIndex()+1, Config.CategoryVideoSport, ApiUtils.requestVideoSport);
            }
        };
        swipe_container_video_sport.setOnLoadMoreListener(rvSportLoadMoreListener);

        //adapter
        rvSportAdapter = new RecyclerViewAdapter(getActivity(),Config.CategoryVideoSport);
        rvSportAdapter.setItemClickListener(new RecyclerViewAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
//                if(MemExchange.getInstance().ifHaveNoSim()){
//                    Toast.makeText((getActivity()),getActivity().getResources().getString(R.string.sms_miss_can_not_see),Toast.LENGTH_SHORT).show();
//                    return;
//                }
                //订阅了的则进入
                if(CheckSubBean.ifHaveSubscribe(MemExchange.m_iIMSI)) {
                    CategoryBean.DataBeanX.DataBean bean = rvSportAdapter.getmDatas().get(position);
                    if (bean == null || TextUtils.isEmpty(bean.getTitle()) || TextUtils.isEmpty(bean.getSeeds())) {
                        return;
                    }
                    Intent intent = new Intent();
                    Bundle data = new Bundle();
                    data.putString("title", bean.getTitle());
                    data.putString("urlPath", bean.getSeeds());
                    intent.putExtras(data);
                    intent.setClass(getContext(), PlayVideoActivity.class);

                    startActivity(intent);
                }else{
//                    if(((MainActivity)getActivity()).getIsInCheck()){
//                        Toast.makeText(getContext(),getResources().getString(R.string.try_later),Toast.LENGTH_SHORT).show();
//                        return;
//                    }
                    //未订阅的则提示是否订阅
                    ((MainActivity)getActivity()).showSubscripDialog();
                }
            }

            @Override
            public void onItemLongClick(View view, int position) {
                Logger.e(Logger.DEBUG_TAG,"onItemLongClick");
            }

        });

        //recyclerView
        rv_video_sport = (EmptyRecyclerView) sportsView.findViewById(R.id.swipe_target);
        rv_video_sport.setLayoutManager(new StaggeredGridLayoutManager(2,StaggeredGridLayoutManager.VERTICAL));//改为Horizontal则表现为滚动的GridView效果
        rv_video_sport.setItemAnimator(new DefaultItemAnimator());//动画
        rv_video_sport.setEmptyView(inflater.inflate(R.layout.view_empty,null));
        rv_video_sport.setAdapter(rvSportAdapter);




        views.add(sportsView);


        //endregion==========类别3===========================================================================


        vp_video = (ViewPager) view.findViewById(R.id.vp_video);
        if(vp_video ==null){
            Log.e("TT","PhotoFragment->error");
            return view;
        }
        ViewPagerAdapter adapter = new ViewPagerAdapter(views,getContext());
        vp_video.setOffscreenPageLimit(views.size() - 1);
        vp_video.setAdapter(adapter);
        ViewPager.OnPageChangeListener pageChangeListener = new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                switch(position){
                    case 0:
                        if(MemExchange.getInstance().getVideoPopularList().size() == 0){
                            ((MainActivity)getActivity()).sendCategoryDataListRequest(1, Config.CategoryVideoPopular, ApiUtils.requestVideoPopular);
                        }
                        break;
                    case 1:
                        if(MemExchange.getInstance().getVideoFunnyList().size() == 0){
                            ((MainActivity)getActivity()).sendCategoryDataListRequest(1, Config.CategoryVideoFunny, ApiUtils.requestVideoFunny);
                        }
                        break;
                    case 2:
                        if(MemExchange.getInstance().getVideoSportList().size() == 0){
                            ((MainActivity)getActivity()).sendCategoryDataListRequest(1, Config.CategoryVideoSport, ApiUtils.requestVideoSport);
                        }
                        break;
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        };
        vp_video.addOnPageChangeListener(pageChangeListener);
        pageChangeListener.onPageSelected(0);

        Activity activity = getActivity();
        String[] titles;
        if(getActivity()!=null){
            titles = new String[]{getContext().getString(R.string.video_popular),
                    getContext().getString(R.string.video_funny),getContext().getString(R.string.video_sports)};
            ((MainActivity)activity).setToolbar(false);
            ((MainActivity)activity).setIndicator(vp_video,titles);
        }
        return view;
    }


    //region==========类别1,swipeLayout===========================================================================


    public void refreshPopular(){
        if(getPopularList()!=null && getPopularList().size() > 0){
            if(rvPopularAdapter!=null) {
                rvPopularAdapter.setmDatas(getPopularList(), getPopularHeight());
            }else{
                Logger.e(Logger.DEBUG_TAG,"refresh error");
            }
        }
    }
    private List<CategoryBean.DataBeanX.DataBean> getPopularList(){
        return MemExchange.getInstance().getVideoPopularList();
    }
    private List<Integer> getPopularHeight(){
        return MemExchange.getInstance().getVideoPopularHeights();
    }


    /**
     * 显示加载完成
     */
    public void stopLoading() {
        if (swipe_container_video_popular != null) {
            swipe_container_video_popular.setLoadMoreEnabled(true);//当列表内有数据的时候才可以上拉加载
            swipe_container_video_popular.setLoadingMore(false);
        }
    }

    /**
     * 显示没有更多
     */
    public void stopLoadNothing() {
        if (swipe_container_video_popular != null)
            swipe_container_video_popular.setLoadingNothing();
    }

    /**
     * 显示加载失败
     */
    public void stopLoadError() {
        if (swipe_container_video_popular != null)
            swipe_container_video_popular.setLoadingError();
    }

    //endregion==========类别1,swipeLayout===========================================================================



    //region==========类别2,swipeLayout===========================================================================


    public void refreshFunny(){
        if(getFunnyList()!=null && getFunnyList().size() > 0){
            if(rvFunnyAdapter!=null) {
                rvFunnyAdapter.setmDatas(getFunnyList(), getFunnyHeight());
            }else{
                Logger.e(Logger.DEBUG_TAG,"refresh error");
            }
        }
    }
    private List<CategoryBean.DataBeanX.DataBean> getFunnyList(){
        return MemExchange.getInstance().getVideoFunnyList();
    }
    private List<Integer> getFunnyHeight(){
        return MemExchange.getInstance().getVideoFunnyHeights();
    }


    /**
     * 显示加载完成
     */
    public void funnyListStopLoading() {
        if (swipe_container_video_funny != null) {
            swipe_container_video_funny.setLoadMoreEnabled(true);//当列表内有数据的时候才可以上拉加载
            swipe_container_video_funny.setLoadingMore(false);
        }
    }

    /**
     * 显示没有更多
     */
    public void funnyListStopLoadNothing() {
        if (swipe_container_video_funny != null)
            swipe_container_video_funny.setLoadingNothing();
    }

    /**
     * 显示加载失败
     */
    public void funnyListStopLoadError() {
        if (swipe_container_video_funny != null)
            swipe_container_video_funny.setLoadingError();
    }

    //endregion==========类别2,swipeLayout===========================================================================


    //region==========类别3,swipeLayout===========================================================================


    public void refreshSport(){
        if(getSportList()!=null && getSportList().size() > 0){
            if(rvSportAdapter!=null) {
                rvSportAdapter.setmDatas(getSportList(), getSportHeight());
            }else{
                Logger.e(Logger.DEBUG_TAG,"refresh error");
            }
        }
    }
    private List<CategoryBean.DataBeanX.DataBean> getSportList(){
        return MemExchange.getInstance().getVideoSportList();
    }
    private List<Integer> getSportHeight(){
        return MemExchange.getInstance().getVideoSportHeights();
    }


    /**
     * 显示加载完成
     */
    public void sportListStopLoading() {
        if (swipe_container_video_sport != null) {
            swipe_container_video_sport.setLoadMoreEnabled(true);//当列表内有数据的时候才可以上拉加载
            swipe_container_video_sport.setLoadingMore(false);
        }
    }

    /**
     * 显示没有更多
     */
    public void sportListStopLoadNothing() {
        if (swipe_container_video_sport != null)
            swipe_container_video_sport.setLoadingNothing();
    }

    /**
     * 显示加载失败
     */
    public void sportListStopLoadError() {
        if (swipe_container_video_sport != null)
            swipe_container_video_sport.setLoadingError();
    }

    //endregion==========类别3,swipeLayout===========================================================================


    @Override
    public void onDestroy() {
        Logger.e(Logger.DEBUG_TAG,TAG+"onDestroy()");
        if(MemExchange.getInstance().getVideoPopularList().size()> Config.PerPageSize){
            MemExchange.getInstance().setVideoPopularList(MemExchange.getInstance().getVideoPopularList().subList(0,10));
            MemExchange.getInstance().setVideoPopularPageIndex(1);
        }
        if(MemExchange.getInstance().getVideoFunnyList().size()> Config.PerPageSize){
            MemExchange.getInstance().setVideoFunnyList(MemExchange.getInstance().getVideoFunnyList().subList(0,10));
            MemExchange.getInstance().setVideoFunnyPageIndex(1);
        }
        if(MemExchange.getInstance().getVideoSportList().size()> Config.PerPageSize){
            MemExchange.getInstance().setVideoSportList(MemExchange.getInstance().getVideoSportList().subList(0,10));
            MemExchange.getInstance().setVideoSportPageIndex(1);
        }

        super.onDestroy();
    }
    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        if (isVisibleToUser){
            Logger.e(Logger.DEBUG_TAG,TAG+"isVisibleToUser:"+(isVisibleToUser?"true":"false"));
        }
        super.setUserVisibleHint(isVisibleToUser);
    }

}
