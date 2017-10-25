package lava.bluepay.com.lavaapp.view.fragment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
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
import lava.bluepay.com.lavaapp.view.activity.ViewPagerActivity;
import lava.bluepay.com.lavaapp.view.adapter.RecyclerViewAdapter;
import lava.bluepay.com.lavaapp.view.adapter.ViewPagerAdapter;
import lava.bluepay.com.lavaapp.view.widget.EmptyRecyclerView;
import lava.bluepay.com.lavaapp.view.widget.swiperefresh.SwipeLoadLayout;

/**
 * Created by bluepay on 2017/10/9.
 */

public class PhotoFragment extends BaseFragment {

    public static final String TAG = "photoFragment";

    private ViewPager vp_photo;

    //region=========类别1==============

    private SwipeLoadLayout swipe_container_photo_popular;
    private EmptyRecyclerView rvPopular;
    private RecyclerViewAdapter rvPopularAdapter;
    private SwipeLoadLayout.OnLoadMoreListener rvPopularLoadMoreListener;

    //endregion=========类别1==============

    //region=========类别2==============
    private SwipeLoadLayout swipe_container_photo_portray;
    private EmptyRecyclerView rv_photo_portray;
    private RecyclerViewAdapter rvPortrayAdapter;
    private SwipeLoadLayout.OnLoadMoreListener rvPortrayLoadMoreListener;

    //endregion=========类别2==============

    //region=========类别3==============
    private SwipeLoadLayout swipe_container_photo_scenery;
    private EmptyRecyclerView rv_photo_scenery;
    private RecyclerViewAdapter rvSceneryAdapter;
    private SwipeLoadLayout.OnLoadMoreListener rvSceneryLoadMoreListener;


    //endregion=========类别3==============



    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        return initView(inflater,container);
    }

    @Override
    public void onResume() {
        Logger.i(Logger.DEBUG_TAG,"PhotoFragment,onResume()");
        super.onResume();

        //进行网络请求
        //((MainActivity)getActivity()).发送请求();
    }

    private View initView(LayoutInflater inflater, ViewGroup container){
        View view  = inflater.inflate(R.layout.fragment_photo,container,false);

        List<View> views = new ArrayList<>();

        //region==========类别1===========================================================================

        View popularView = inflater.inflate(R.layout.fragment_photo_popular,null);
        rvPopularLoadMoreListener = new SwipeLoadLayout.OnLoadMoreListener() {
            @Override
            public void onLoadMore() {
                ((MainActivity)getActivity()).sendCategoryDataListRequest(MemExchange.getInstance().getPhotoPopularPageIndex()+1, Config.CategoryPhotoPopular, ApiUtils.requestPhotoPopular);
            }
        };
        swipe_container_photo_popular = (SwipeLoadLayout) popularView.findViewById(R.id.swipe_container_photo_popular);
        swipe_container_photo_popular.setLoadingMore(false);
        swipe_container_photo_popular.setOnLoadMoreListener(rvPopularLoadMoreListener);

        rvPopular = (EmptyRecyclerView) popularView.findViewById(R.id.swipe_target);
        rvPopularAdapter = new RecyclerViewAdapter(getActivity(),Config.CategoryPhotoPopular);
        rvPopular.setLayoutManager(new StaggeredGridLayoutManager(2,StaggeredGridLayoutManager.VERTICAL));
        rvPopular.setItemAnimator(new DefaultItemAnimator());//动画
        rvPopular.setEmptyView(inflater.inflate(R.layout.view_empty,null));

//        rvPopular.addItemDecoration(new DividerGridItemDecoration(getActivity()));
        rvPopularAdapter.setItemClickListener(new RecyclerViewAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Intent intent = new Intent();
                intent.setClass(getContext(), ViewPagerActivity.class);
                intent.putExtra("categoryId",Config.CategoryPhotoPopular);//大类id
                intent.putExtra("index",position);//为大类中的index
                startActivity(intent);
            }

            @Override
            public void onItemLongClick(View view, int position) {
                Logger.e(Logger.DEBUG_TAG,"onItemLongClick");
            }

        });
        rvPopular.setAdapter(rvPopularAdapter);

        views.add(popularView);

        //endregion==========类别1===========================================================================

        //region==========类别2===========================================================================



        View portrayView = inflater.inflate(R.layout.fragment_photo_portray,null);

        //下拉控件
        swipe_container_photo_portray = (SwipeLoadLayout) portrayView.findViewById(R.id.swipe_container_photo_portray);
        swipe_container_photo_portray.setLoadingMore(false);
        rvPortrayLoadMoreListener = new SwipeLoadLayout.OnLoadMoreListener() {
            @Override
            public void onLoadMore() {
                ((MainActivity)getActivity()).sendCategoryDataListRequest(MemExchange.getInstance().getPhotoPortrayPageIndex()+1, Config.CategoryPhotoPortray, ApiUtils.requestPhotoPortray);
            }
        };
        swipe_container_photo_portray.setOnLoadMoreListener(rvPortrayLoadMoreListener);

        //adapter
        rvPortrayAdapter = new RecyclerViewAdapter(getActivity(),Config.CategoryPhotoPortray);
        rvPortrayAdapter.setItemClickListener(new RecyclerViewAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Intent intent = new Intent();
                intent.setClass(getContext(), ViewPagerActivity.class);
                intent.putExtra("categoryId",Config.CategoryPhotoPortray);//大类id
                intent.putExtra("index",position);//为大类中的index
                startActivity(intent);
            }

            @Override
            public void onItemLongClick(View view, int position) {
                Logger.e(Logger.DEBUG_TAG,"onItemLongClick");
            }

        });

        //recyclerView
        rv_photo_portray = (EmptyRecyclerView) portrayView.findViewById(R.id.swipe_target);
        rv_photo_portray.setLayoutManager(new StaggeredGridLayoutManager(2,StaggeredGridLayoutManager.VERTICAL));//改为Horizontal则表现为滚动的GridView效果
        rv_photo_portray.setItemAnimator(new DefaultItemAnimator());//动画
        rv_photo_portray.setEmptyView(inflater.inflate(R.layout.view_empty,null));
        rv_photo_portray.setAdapter(rvPortrayAdapter);



        views.add(portrayView);


        //endregion==========类别2===========================================================================

        //region==========类别3===========================================================================

        View sceneryView = inflater.inflate(R.layout.fragment_photo_scenery,null);


        Button btn = (Button) sceneryView.findViewById(R.id.bt_to_video_activity);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent =new Intent(getActivity(), PlayVideoActivity.class);
                startActivity(intent);
            }
        });

        //下拉控件
        swipe_container_photo_scenery = (SwipeLoadLayout) sceneryView.findViewById(R.id.swipe_container_photo_portray);
        swipe_container_photo_scenery.setLoadingMore(false);
        rvSceneryLoadMoreListener = new SwipeLoadLayout.OnLoadMoreListener() {
            @Override
            public void onLoadMore() {
                ((MainActivity)getActivity()).sendCategoryDataListRequest(MemExchange.getInstance().getPhotoSceneryPageIndex()+1, Config.CategoryPhotoScenery, ApiUtils.requestPhotoScenery);
            }
        };
        swipe_container_photo_scenery.setOnLoadMoreListener(rvSceneryLoadMoreListener);


        //adapter
        rvSceneryAdapter = new RecyclerViewAdapter(getActivity(),Config.CategoryPhotoScenery);
        rvSceneryAdapter.setItemClickListener(new RecyclerViewAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Intent intent = new Intent();
                intent.setClass(getContext(), ViewPagerActivity.class);
                intent.putExtra("categoryId",Config.CategoryPhotoScenery);//大类id
                intent.putExtra("index",position);//为大类中的index
                startActivity(intent);
            }

            @Override
            public void onItemLongClick(View view, int position) {
                Logger.e(Logger.DEBUG_TAG,"onItemLongClick");
            }

        });

        //recyclerView
        rv_photo_scenery = (EmptyRecyclerView) sceneryView.findViewById(R.id.swipe_target);
        rv_photo_scenery.setLayoutManager(new StaggeredGridLayoutManager(2,StaggeredGridLayoutManager.VERTICAL));//改为Horizontal则表现为滚动的GridView效果
        rv_photo_scenery.setItemAnimator(new DefaultItemAnimator());//动画
        rv_photo_scenery.setEmptyView(inflater.inflate(R.layout.view_empty,null));
        rv_photo_scenery.setAdapter(rvSceneryAdapter);





        views.add(sceneryView);


        //endregion==========类别3===========================================================================



        vp_photo = (ViewPager) view.findViewById(R.id.vp_photo);
        if(vp_photo==null){
            Logger.e(Logger.DEBUG_TAG,"PhotoFragment->error");
            return view;
        }
        ViewPagerAdapter adapter = new ViewPagerAdapter(views,getContext());
        vp_photo.setOffscreenPageLimit(views.size() - 1);
        vp_photo.setAdapter(adapter);
        vp_photo.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {//0,1,2
                //默认选中第一页时不加载数据,第二第三页才加载数据
                switch(position){
                    case 1:
                        if(MemExchange.getInstance().getPhotoPortrayList().size() == 0){
                            ((MainActivity)getActivity()).sendCategoryDataListRequest(1, Config.CategoryPhotoPortray, ApiUtils.requestPhotoPortray);
                        }
                        break;
                    case 2:
                        if(MemExchange.getInstance().getPhotoSceneryList().size() == 0){
                            ((MainActivity)getActivity()).sendCategoryDataListRequest(1, Config.CategoryPhotoScenery, ApiUtils.requestPhotoScenery);
                        }
                        break;
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        //todo bug
        Activity activity = getActivity();
        String[] titles;
        if(activity!=null && activity instanceof MainActivity){
            titles = new String[]{getContext().getString(R.string.photo_popular),
            getContext().getString(R.string.photo_portray),getContext().getString(R.string.photo_scenery)};
            ((MainActivity)activity).setToolbar(false);
            ((MainActivity)activity).setIndicator(vp_photo,titles);
        }
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

    }


    //region==========类别1,swipeLayout===========================================================================


    public void refreshPopular(){
        if(getPopularList()!=null && getPopularList().size() > 0){
            rvPopularAdapter.setmDatas(getPopularList(),getPopularHeight());
        }
    }
    private List<CategoryBean.DataBeanX.DataBean> getPopularList(){
        return MemExchange.getInstance().getPhotoPopularList();
    }
    private List<Integer> getPopularHeight(){
        return MemExchange.getInstance().getPopularHeights();
    }


    /**
     * 显示加载完成
     */
    public void stopLoading() {
        if (swipe_container_photo_popular != null) {
            swipe_container_photo_popular.setLoadMoreEnabled(true);//当列表内有数据的时候才可以上拉加载
            swipe_container_photo_popular.setLoadingMore(false);
        }
    }

    /**
     * 显示没有更多
     */
    public void stopLoadNothing() {
        if (swipe_container_photo_popular != null)
            swipe_container_photo_popular.setLoadingNothing();
    }

    /**
     * 显示加载失败
     */
    public void stopLoadError() {
        if (swipe_container_photo_popular != null)
            swipe_container_photo_popular.setLoadingError();
    }

    //endregion==========类别1,swipeLayout===========================================================================


    //region==========类别2,swipeLayout===========================================================================

    public void refreshPortray(){
        if(getPortrayList()!=null && getPortrayList().size() > 0){
            rvPortrayAdapter.setmDatas(getPortrayList(),getPortrayHeight());
        }
    }
    private List<CategoryBean.DataBeanX.DataBean> getPortrayList(){
        return MemExchange.getInstance().getPhotoPortrayList();
    }
    private List<Integer> getPortrayHeight(){
        return MemExchange.getInstance().getPhotoPortrayHeights();
    }

    /**
     * 显示加载完成
     */
    public void portrayListStopLoading() {
        if (swipe_container_photo_portray != null) {
            swipe_container_photo_portray.setLoadMoreEnabled(true);//当列表内有数据的时候才可以上拉加载
            swipe_container_photo_portray.setLoadingMore(false);
        }
    }

    /**
     * 显示没有更多
     */
    public void portrayListStopLoadNothing() {
        if (swipe_container_photo_portray != null)
            swipe_container_photo_portray.setLoadingNothing();
    }

    /**
     * 显示加载失败
     */
    public void portrayListStopLoadError() {
        if (swipe_container_photo_portray != null)
            swipe_container_photo_portray.setLoadingError();
    }

    //endregion==========类别2,swipeLayout===========================================================================


    //region==========类别3,swipeLayout===========================================================================

    public void refreshScenery(){
        if(getSceneryList()!=null && getSceneryList().size() > 0){
            rvSceneryAdapter.setmDatas(getSceneryList(),getSceneryHeight());
        }
    }
    private List<CategoryBean.DataBeanX.DataBean> getSceneryList(){
        return MemExchange.getInstance().getPhotoSceneryList();
    }
    private List<Integer> getSceneryHeight(){
        return MemExchange.getInstance().getPhotoSceneryHeights();
    }

    /**
     * 显示加载完成
     */
    public void sceneryListStopLoading() {
        if (swipe_container_photo_scenery != null) {
            swipe_container_photo_scenery.setLoadMoreEnabled(true);//当列表内有数据的时候才可以上拉加载
            swipe_container_photo_scenery.setLoadingMore(false);
        }
    }

    /**
     * 显示没有更多
     */
    public void sceneryListStopLoadNothing() {
        if (swipe_container_photo_scenery != null)
            swipe_container_photo_scenery.setLoadingNothing();
    }

    /**
     * 显示加载失败
     */
    public void sceneryListStopLoadError() {
        if (swipe_container_photo_scenery != null)
            swipe_container_photo_scenery.setLoadingError();
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
