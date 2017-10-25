package lava.bluepay.com.lavaapp.view.fragment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import lava.bluepay.com.lavaapp.Config;
import lava.bluepay.com.lavaapp.R;
import lava.bluepay.com.lavaapp.common.Logger;
import lava.bluepay.com.lavaapp.model.MemExchange;
import lava.bluepay.com.lavaapp.model.api.ApiUtils;
import lava.bluepay.com.lavaapp.model.api.bean.CategoryBean;
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


    private View initView(LayoutInflater inflater,ViewGroup container){
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
                Intent intent = new Intent();
                intent.setClass(getContext(), PlayVideoActivity.class);
                //todo 设置视屏源
                startActivity(intent);
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
                Intent intent = new Intent();
                intent.setClass(getContext(), PlayVideoActivity.class);
                //todo 设置视屏源
                startActivity(intent);
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
                Intent intent = new Intent();
                intent.setClass(getContext(), PlayVideoActivity.class);
                //todo 设置视屏源
                startActivity(intent);
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


        //region==========类别3===========================================================================


        vp_video = (ViewPager) view.findViewById(R.id.vp_video);
        if(vp_video ==null){
            Log.e("TT","PhotoFragment->error");
            return view;
        }
        ViewPagerAdapter adapter = new ViewPagerAdapter(views,getContext());
        vp_video.setOffscreenPageLimit(views.size() - 1);
        vp_video.setAdapter(adapter);

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
            rvPopularAdapter.setmDatas(getPopularList(),getPopularHeight());
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
            rvFunnyAdapter.setmDatas(getFunnyList(),getFunnyHeight());
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
            rvSportAdapter.setmDatas(getSportList(),getSportHeight());
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
