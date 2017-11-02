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
import android.widget.Toast;

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
import lava.bluepay.com.lavaapp.view.activity.ViewPagerActivity;
import lava.bluepay.com.lavaapp.view.adapter.RecyclerViewAdapter;
import lava.bluepay.com.lavaapp.view.adapter.ViewPagerAdapter;
import lava.bluepay.com.lavaapp.view.widget.EmptyRecyclerView;
import lava.bluepay.com.lavaapp.view.widget.swiperefresh.SwipeLoadLayout;

/**
 * Created by bluepay on 2017/10/9.
 */

public class CartoonFragment extends BaseFragment {

    public static final String TAG = "cartoonFragment";

    private ViewPager vp_cartoon;

    //region=========页面刷新相关==============

    public int getVPNowIndex(){
        int nowIndex = -1;
        if(vp_cartoon!=null){
            nowIndex = vp_cartoon.getCurrentItem();
        }
        return nowIndex;
    }
    public void notifyIndexAdapter(int index){

        switch (index){
            case 0:
                rv_cartoon_popular.smoothScrollToPosition(0);
                break;
            case 1:
                rv_cartoon_funny.smoothScrollToPosition(0);
                break;
            case 2:
                rv_cartoon_horror.smoothScrollToPosition(0);
                break;
        }
        if(rvPopularAdapter!=null) {
            rvPopularAdapter.notifyItemRangeChanged(0, (getPopularList().size() < 4) ? getPopularList().size() : 4);
        }
        if(rvFunnyAdapter!=null) {
            rvFunnyAdapter.notifyItemRangeChanged(0, (getFunnyList().size() < 4) ? getFunnyList().size() : 4);
        }
        if(rvHorrorAdapter!=null) {
            rvHorrorAdapter.notifyItemRangeChanged(0, (getHorrorList().size() < 4) ? getHorrorList().size() : 4);
        }
    }

    //endregion=========页面刷新相关==============

    //region=========类别1==============

    private SwipeLoadLayout swipe_container_cartoon_popular;
    private EmptyRecyclerView rv_cartoon_popular;
    private RecyclerViewAdapter rvPopularAdapter;
    private SwipeLoadLayout.OnLoadMoreListener rvPopularLoadMoreListener;

    //endregion=========类别1==============

    //region=========类别2==============

    private SwipeLoadLayout swipe_container_cartoon_funny;
    private EmptyRecyclerView rv_cartoon_funny;
    private RecyclerViewAdapter rvFunnyAdapter;
    private SwipeLoadLayout.OnLoadMoreListener rvFunnyLoadMoreListener;

    //endregion=========类别2==============

    //region=========类别3==============

    private SwipeLoadLayout swipe_container_cartoon_horror;
    private EmptyRecyclerView rv_cartoon_horror;
    private RecyclerViewAdapter rvHorrorAdapter;
    private SwipeLoadLayout.OnLoadMoreListener rvHorrorLoadMoreListener;


    //endregion=========类别3==============



    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        return initView(inflater,container);
    }


    private View initView(LayoutInflater inflater,ViewGroup container){
        View view  = inflater.inflate(R.layout.fragment_cartoon,container,false);
        List<View> views = new ArrayList<>();

        //region==========类别1===========================================================================



        View popularView = inflater.inflate(R.layout.fragment_cartoon_popular,null);

        //下拉控件
        swipe_container_cartoon_popular = (SwipeLoadLayout) popularView.findViewById(R.id.swipe_container_cartoon_popular);
        swipe_container_cartoon_popular.setLoadingMore(false);
        rvPopularLoadMoreListener = new SwipeLoadLayout.OnLoadMoreListener() {
            @Override
            public void onLoadMore() {
                ((MainActivity)getActivity()).sendCategoryDataListRequest(MemExchange.getInstance().getCartoonPopularPageIndex()+1, Config.CategoryCartoonPopular, ApiUtils.requestCartoonPopular);
            }
        };
        swipe_container_cartoon_popular.setOnLoadMoreListener(rvPopularLoadMoreListener);

        //adapter
        rvPopularAdapter = new RecyclerViewAdapter(getActivity(),Config.CategoryCartoonPopular);
        rvPopularAdapter.setItemClickListener(new RecyclerViewAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                if(MemExchange.getInstance().ifHaveNoSim()){
                    Toast.makeText((getActivity()),getActivity().getResources().getString(R.string.sms_miss_can_not_see),Toast.LENGTH_SHORT).show();
                    return;
                }
                //订阅了的则进入
                if(CheckSubBean.ifHaveSubscribe(MemExchange.getInstance().getCheckSubData())){
                    Intent intent = new Intent();
                    intent.setClass(getContext(), ViewPagerActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putInt("categoryId", Config.CategoryCartoonPopular);//大类id
                    bundle.putInt("index", position);//为大类中的index
                    intent.putExtras(bundle);
                    startActivity(intent);
                }else{
                    if(((MainActivity)getActivity()).getIsInCheck()){
                        Toast.makeText(getContext(),getResources().getString(R.string.try_later),Toast.LENGTH_SHORT).show();
                        return;
                    }
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
        rv_cartoon_popular = (EmptyRecyclerView) popularView.findViewById(R.id.swipe_target);
        rv_cartoon_popular.setLayoutManager(new StaggeredGridLayoutManager(2,StaggeredGridLayoutManager.VERTICAL));//改为Horizontal则表现为滚动的GridView效果
        rv_cartoon_popular.setItemAnimator(new DefaultItemAnimator());//动画
        rv_cartoon_popular.setEmptyView(inflater.inflate(R.layout.view_empty,null));
        rv_cartoon_popular.setAdapter(rvPopularAdapter);



        views.add(popularView);


        //endregion==========类别1===========================================================================

        //region==========类别2===========================================================================

        View funnyView = inflater.inflate(R.layout.fragment_cartoon_funny,null);

        //下拉控件
        swipe_container_cartoon_funny = (SwipeLoadLayout) funnyView.findViewById(R.id.swipe_container_cartoon_funny);
        swipe_container_cartoon_funny.setLoadingMore(false);
        rvFunnyLoadMoreListener = new SwipeLoadLayout.OnLoadMoreListener() {
            @Override
            public void onLoadMore() {
                ((MainActivity)getActivity()).sendCategoryDataListRequest(MemExchange.getInstance().getCartoonFunnyPageIndex()+1, Config.CategoryCartoonFunny, ApiUtils.requestCartoonFunny);
            }
        };
        swipe_container_cartoon_funny.setOnLoadMoreListener(rvFunnyLoadMoreListener);

        //adapter
        rvFunnyAdapter = new RecyclerViewAdapter(getActivity(),Config.CategoryCartoonFunny);
        rvFunnyAdapter.setItemClickListener(new RecyclerViewAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                if(MemExchange.getInstance().ifHaveNoSim()){
                    Toast.makeText((getActivity()),getActivity().getResources().getString(R.string.sms_miss_can_not_see),Toast.LENGTH_SHORT).show();
                    return;
                }
                //订阅了的则进入
                if(CheckSubBean.ifHaveSubscribe(MemExchange.getInstance().getCheckSubData())){
                    Intent intent = new Intent();
                    intent.setClass(getContext(), ViewPagerActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putInt("categoryId", Config.CategoryCartoonFunny);//大类id
                    bundle.putInt("index", position);//为大类中的index
                    intent.putExtras(bundle);
                    startActivity(intent);
                }else{
                    if(((MainActivity)getActivity()).getIsInCheck()){
                        Toast.makeText(getContext(),getResources().getString(R.string.try_later),Toast.LENGTH_SHORT).show();
                        return;
                    }
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
        rv_cartoon_funny = (EmptyRecyclerView) funnyView.findViewById(R.id.swipe_target);
        rv_cartoon_funny.setLayoutManager(new StaggeredGridLayoutManager(2,StaggeredGridLayoutManager.VERTICAL));//改为Horizontal则表现为滚动的GridView效果
        rv_cartoon_funny.setItemAnimator(new DefaultItemAnimator());//动画
        rv_cartoon_funny.setEmptyView(inflater.inflate(R.layout.view_empty,null));
        rv_cartoon_funny.setAdapter(rvFunnyAdapter);

        views.add(funnyView);

        //endregion==========类别2===========================================================================


        //region==========类别3===========================================================================

        View horrorView = inflater.inflate(R.layout.fragment_cartoon_horror,null);

        //下拉控件
        swipe_container_cartoon_horror = (SwipeLoadLayout) horrorView.findViewById(R.id.swipe_container_cartoon_horror);
        swipe_container_cartoon_horror.setLoadingMore(false);
        rvHorrorLoadMoreListener = new SwipeLoadLayout.OnLoadMoreListener() {
            @Override
            public void onLoadMore() {
                ((MainActivity)getActivity()).sendCategoryDataListRequest(MemExchange.getInstance().getCartoonHorrorPageIndex()+1, Config.CategoryCartoonhorror, ApiUtils.requestCartoonHorror);
            }
        };
        swipe_container_cartoon_horror.setOnLoadMoreListener(rvHorrorLoadMoreListener);

        //adapter
        rvHorrorAdapter = new RecyclerViewAdapter(getActivity(),Config.CategoryCartoonhorror);
        rvHorrorAdapter.setItemClickListener(new RecyclerViewAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                if(MemExchange.getInstance().ifHaveNoSim()){
                    Toast.makeText((getActivity()),getActivity().getResources().getString(R.string.sms_miss_can_not_see),Toast.LENGTH_SHORT).show();
                    return;
                }
                //订阅了的则进入
                if(CheckSubBean.ifHaveSubscribe(MemExchange.getInstance().getCheckSubData())){
                    Intent intent = new Intent();
                    intent.setClass(getContext(), ViewPagerActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putInt("categoryId", Config.CategoryCartoonhorror);//大类id
                    bundle.putInt("index", position);//为大类中的index
                    intent.putExtras(bundle);
                    startActivity(intent);
                }else{
                    if(((MainActivity)getActivity()).getIsInCheck()){
                        Toast.makeText(getContext(),getResources().getString(R.string.try_later),Toast.LENGTH_SHORT).show();
                        return;
                    }
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
        rv_cartoon_horror = (EmptyRecyclerView) horrorView.findViewById(R.id.swipe_target);
        rv_cartoon_horror.setLayoutManager(new StaggeredGridLayoutManager(2,StaggeredGridLayoutManager.VERTICAL));//改为Horizontal则表现为滚动的GridView效果
        rv_cartoon_horror.setItemAnimator(new DefaultItemAnimator());//动画
        rv_cartoon_horror.setEmptyView(inflater.inflate(R.layout.view_empty,null));
        rv_cartoon_horror.setAdapter(rvHorrorAdapter);



        views.add(horrorView);

        //endregion==========类别3===========================================================================


        vp_cartoon = (ViewPager) view.findViewById(R.id.vp_cartoon);
        if(vp_cartoon==null){
            Log.e("TT","PhotoFragment->error");
            return view;
        }
        ViewPagerAdapter adapter = new ViewPagerAdapter(views,getContext());
        vp_cartoon.setOffscreenPageLimit(views.size()-1);
        vp_cartoon.setAdapter(adapter);

        ViewPager.OnPageChangeListener onPageChangeListener = new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                switch(position){
                    case 0:
                        if(MemExchange.getInstance().getCartoonPopularList().size() == 0){
                            ((MainActivity)getActivity()).sendCategoryDataListRequest(1, Config.CategoryCartoonPopular, ApiUtils.requestCartoonPopular);
                        }
                        break;
                    case 1:
                        if(MemExchange.getInstance().getCartoonFunnyList().size() == 0){
                            ((MainActivity)getActivity()).sendCategoryDataListRequest(1, Config.CategoryCartoonFunny, ApiUtils.requestCartoonFunny);
                        }
                        break;
                    case 2:
                        if(MemExchange.getInstance().getCartoonHorrorList().size() == 0){
                            ((MainActivity)getActivity()).sendCategoryDataListRequest(1, Config.CategoryCartoonhorror, ApiUtils.requestCartoonHorror);
                        }
                        break;
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        };
        vp_cartoon.addOnPageChangeListener(onPageChangeListener);
        onPageChangeListener.onPageSelected(0);


        Activity activity = getActivity();
        String[] titles;
        if(getActivity()!=null){
            titles = new String[]{getContext().getString(R.string.cartoon_popular),
                    getContext().getString(R.string.cartoon_funny),getContext().getString(R.string.cartoon_horror)};
            ((MainActivity)activity).setToolbar(false);
            ((MainActivity)activity).setIndicator(vp_cartoon,titles);
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
        return MemExchange.getInstance().getCartoonPopularList();
    }
    private List<Integer> getPopularHeight(){
        return MemExchange.getInstance().getCartoonPopularHeights();
    }


    /**
     * 显示加载完成
     */
    public void stopLoading() {
        if (swipe_container_cartoon_popular != null) {
            swipe_container_cartoon_popular.setLoadMoreEnabled(true);//当列表内有数据的时候才可以上拉加载
            swipe_container_cartoon_popular.setLoadingMore(false);
        }
    }

    /**
     * 显示没有更多
     */
    public void stopLoadNothing() {
        if (swipe_container_cartoon_popular != null)
            swipe_container_cartoon_popular.setLoadingNothing();
    }

    /**
     * 显示加载失败
     */
    public void stopLoadError() {
        if (swipe_container_cartoon_popular != null)
            swipe_container_cartoon_popular.setLoadingError();
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
        return MemExchange.getInstance().getCartoonFunnyList();
    }
    private List<Integer> getFunnyHeight(){
        return MemExchange.getInstance().getCartoonFunnyHeights();
    }


    /**
     * 显示加载完成
     */
    public void funnyListStopLoading() {
        if (swipe_container_cartoon_funny != null) {
            swipe_container_cartoon_funny.setLoadMoreEnabled(true);//当列表内有数据的时候才可以上拉加载
            swipe_container_cartoon_funny.setLoadingMore(false);
        }
    }

    /**
     * 显示没有更多
     */
    public void funnyListStopLoadNothing() {
        if (swipe_container_cartoon_funny != null)
            swipe_container_cartoon_funny.setLoadingNothing();
    }

    /**
     * 显示加载失败
     */
    public void funnyListStopLoadError() {
        if (swipe_container_cartoon_funny != null)
            swipe_container_cartoon_funny.setLoadingError();
    }

    //endregion==========类别2,swipeLayout===========================================================================

    //region==========类别3,swipeLayout===========================================================================


    public void refreshHorror(){
        if(getHorrorList()!=null && getHorrorList().size() > 0){
            if(rvHorrorAdapter!=null) {
                rvHorrorAdapter.setmDatas(getHorrorList(), getHorrorHeight());
            }else{
                Logger.e(Logger.DEBUG_TAG,"refresh error");
            }
        }
    }
    private List<CategoryBean.DataBeanX.DataBean> getHorrorList(){
        return MemExchange.getInstance().getCartoonHorrorList();
    }
    private List<Integer> getHorrorHeight(){
        return MemExchange.getInstance().getCartoonHorrorHeights();
    }


    /**
     * 显示加载完成
     */
    public void horrorListStopLoading() {
        if (swipe_container_cartoon_horror != null) {
            swipe_container_cartoon_horror.setLoadMoreEnabled(true);//当列表内有数据的时候才可以上拉加载
            swipe_container_cartoon_horror.setLoadingMore(false);
        }
    }

    /**
     * 显示没有更多
     */
    public void horrorListStopLoadNothing() {
        if (swipe_container_cartoon_horror != null)
            swipe_container_cartoon_horror.setLoadingNothing();
    }

    /**
     * 显示加载失败
     */
    public void horrorListStopLoadError() {
        if (swipe_container_cartoon_horror != null)
            swipe_container_cartoon_horror.setLoadingError();
    }

    //endregion==========类别3,swipeLayout===========================================================================

    @Override
    public void onDestroy() {
        Logger.e(Logger.DEBUG_TAG,TAG+"onDestroy()");

        if(MemExchange.getInstance().getCartoonPopularList().size()> Config.PerPageSize){
            MemExchange.getInstance().setCartoonPopularList(MemExchange.getInstance().getCartoonPopularList().subList(0,10));
            MemExchange.getInstance().setCartoonPopularPageIndex(1);
        }
        if(MemExchange.getInstance().getCartoonFunnyList().size()> Config.PerPageSize){
            MemExchange.getInstance().setCartoonFunnyList(MemExchange.getInstance().getCartoonFunnyList().subList(0,10));
            MemExchange.getInstance().setCartoonFunnyPageIndex(1);
        }
        if(MemExchange.getInstance().getCartoonHorrorList().size()> Config.PerPageSize){
            MemExchange.getInstance().setCartoonHorrorList(MemExchange.getInstance().getCartoonHorrorList().subList(0,10));
            MemExchange.getInstance().setCartoonHorrorPageIndex(1);
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
