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
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import lava.bluepay.com.lavaapp.Config;
import lava.bluepay.com.lavaapp.R;
import lava.bluepay.com.lavaapp.common.Logger;
import lava.bluepay.com.lavaapp.common.Utils;
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

public class PhotoFragment extends BaseFragment {

    public static final String TAG = "photoFragment";

    private ViewPager vp_photo;

    //region=========页面刷新相关==============
    public int getVPNowIndex(){
        int nowIndex = -1;
        if(vp_photo!=null){
            nowIndex = vp_photo.getCurrentItem();
        }
        return nowIndex;
    }
    public void notifyIndexAdapter(int index){

        switch (index){
            case 0:
                rvPopular.smoothScrollToPosition(0);
                break;
            case 1:
                rv_photo_portray.smoothScrollToPosition(0);
                break;
            case 2:
                rv_photo_scenery.smoothScrollToPosition(0);
                break;
        }
        if(rvPopularAdapter!=null) {
            rvPopularAdapter.notifyItemRangeChanged(0, (getPopularList().size() < 4) ? getPopularList().size() : 4);
        }
        if(rvPortrayAdapter!=null) {
            rvPortrayAdapter.notifyItemRangeChanged(0, (getPortrayList().size() < 4) ? getPortrayList().size() : 4);
        }
        if(rvSceneryAdapter!=null) {
            rvSceneryAdapter.notifyItemRangeChanged(0, (getSceneryList().size() < 4) ? getSceneryList().size() : 4);
        }
    }

    //endregion=========页面刷新相关==============

    //region=========类别1==============

    private SwipeLoadLayout swipe_container_photo_popular;
    private EmptyRecyclerView rvPopular;
    private RecyclerViewAdapter rvPopularAdapter;
    private SwipeLoadLayout.OnLoadMoreListener rvPopularLoadMoreListener;

//    private int firstVisiblePopularIndex = -1;
//    private int lastVisiblePopularIndex = -1;
//    private RecyclerView.OnScrollListener popularScrollListener;

    //endregion=========类别1==============

    //region=========类别2==============
    private SwipeLoadLayout swipe_container_photo_portray;
    private EmptyRecyclerView rv_photo_portray;
    private RecyclerViewAdapter rvPortrayAdapter;
    private SwipeLoadLayout.OnLoadMoreListener rvPortrayLoadMoreListener;

//    private int firstVisiblePortrayIndex = -1;
//    private int lastVisiblePortrayIndex = -1;
//    private RecyclerView.OnScrollListener portrayScrollListener;
    //endregion=========类别2==============

    //region=========类别3==============
    private SwipeLoadLayout swipe_container_photo_scenery;
    private EmptyRecyclerView rv_photo_scenery;
    private RecyclerViewAdapter rvSceneryAdapter;
    private SwipeLoadLayout.OnLoadMoreListener rvSceneryLoadMoreListener;

//    private int firstVisibleSceneryIndex = -1;
//    private int lastVisibleSceneryIndex = -1;
//    private RecyclerView.OnScrollListener sceneryScrollListener;

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

//        popularScrollListener = new RecyclerView.OnScrollListener() {
//            @Override
//            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
//                super.onScrollStateChanged(recyclerView, newState);
//                if(newState == RecyclerView.SCROLL_STATE_IDLE){
//                    RecyclerView.LayoutManager layoutManager = recyclerView.getLayoutManager();
//                    //因为StaggeredGridLayoutManager的特殊性可能导致最后显示的item存在多个，所以这里取到的是一个数组
//                    //得到这个数组后再取到数组中position值最大的那个就是最后显示的position值了
//                    int[] lastPositions = new int[2];
//                    int[] firstPositions = new int[2];
//                    ((StaggeredGridLayoutManager) layoutManager).findLastVisibleItemPositions(lastPositions);
//                    lastVisiblePopularIndex = Utils.findMax(lastPositions);
//                    ((StaggeredGridLayoutManager) layoutManager).findFirstVisibleItemPositions(firstPositions);
//                    firstVisiblePopularIndex = Utils.findMin(firstPositions);
//
//                }
//            }
//        };
//        rvPopular.addOnScrollListener(popularScrollListener);


//        rvPopular.addItemDecoration(new DividerGridItemDecoration(getActivity()));
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
                    bundle.putInt("categoryId", Config.CategoryPhotoPopular);//大类id
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
        rvPopular.setAdapter(rvPopularAdapter);


//        Button bt_test = (Button) popularView.findViewById(R.id.bt_test);
//        bt_test.setText("bug before");//bug after
//        bt_test.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                String time = MemExchange.getInstance().getInitData().getTime();
//                if (time.indexOf(",") == -1) {
//                    return;
//                }
//                Logger.e(Logger.DEBUG_TAG,Utils.ifTimeIn(time)?"true":"false");
//            }
//        });
//
//        Button btn_update = (Button) popularView.findViewById(R.id.btn_update);
//        btn_update.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                ((MainActivity)getActivity()).andFixUpdate();
//            }
//        });
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
                if(MemExchange.getInstance().ifHaveNoSim()){
                    Toast.makeText((getActivity()),getActivity().getResources().getString(R.string.sms_miss_can_not_see),Toast.LENGTH_SHORT).show();
                    return;
                }
                //订阅了的则进入
                if(CheckSubBean.ifHaveSubscribe(MemExchange.getInstance().getCheckSubData())){
                    Intent intent = new Intent();
                    intent.setClass(getContext(), ViewPagerActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putInt("categoryId", Config.CategoryPhotoPortray);//大类id
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
        rv_photo_portray = (EmptyRecyclerView) portrayView.findViewById(R.id.swipe_target);
        rv_photo_portray.setLayoutManager(new StaggeredGridLayoutManager(2,StaggeredGridLayoutManager.VERTICAL));//改为Horizontal则表现为滚动的GridView效果
        rv_photo_portray.setItemAnimator(new DefaultItemAnimator());//动画
        rv_photo_portray.setEmptyView(inflater.inflate(R.layout.view_empty,null));

//        portrayScrollListener = new RecyclerView.OnScrollListener() {
//            @Override
//            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
//                super.onScrollStateChanged(recyclerView, newState);
//                if(newState == RecyclerView.SCROLL_STATE_IDLE){
//                    RecyclerView.LayoutManager layoutManager = recyclerView.getLayoutManager();
//                    //因为StaggeredGridLayoutManager的特殊性可能导致最后显示的item存在多个，所以这里取到的是一个数组
//                    //得到这个数组后再取到数组中position值最大的那个就是最后显示的position值了
//                    int[] lastPositions = new int[2];
//                    int[] firstPositions = new int[2];
//                    ((StaggeredGridLayoutManager) layoutManager).findLastVisibleItemPositions(lastPositions);
//                    lastVisiblePortrayIndex = Utils.findMax(lastPositions);
//                    ((StaggeredGridLayoutManager) layoutManager).findFirstVisibleItemPositions(firstPositions);
//                    firstVisiblePortrayIndex = Utils.findMin(firstPositions);
//
//                }
//            }
//        };
//        rv_photo_portray.addOnScrollListener(portrayScrollListener);
        rv_photo_portray.setAdapter(rvPortrayAdapter);



        views.add(portrayView);


        //endregion==========类别2===========================================================================

        //region==========类别3===========================================================================

        View sceneryView = inflater.inflate(R.layout.fragment_photo_scenery,null);

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
                if(MemExchange.getInstance().ifHaveNoSim()){
                    Toast.makeText((getActivity()),getActivity().getResources().getString(R.string.sms_miss_can_not_see),Toast.LENGTH_SHORT).show();
                    return;
                }
                //订阅了的则进入
                if(CheckSubBean.ifHaveSubscribe(MemExchange.getInstance().getCheckSubData())){
                    Intent intent = new Intent();
                    intent.setClass(getContext(), ViewPagerActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putInt("categoryId", Config.CategoryPhotoScenery);//大类id
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
        rv_photo_scenery = (EmptyRecyclerView) sceneryView.findViewById(R.id.swipe_target);
        rv_photo_scenery.setLayoutManager(new StaggeredGridLayoutManager(2,StaggeredGridLayoutManager.VERTICAL));//改为Horizontal则表现为滚动的GridView效果
        rv_photo_scenery.setItemAnimator(new DefaultItemAnimator());//动画
        rv_photo_scenery.setEmptyView(inflater.inflate(R.layout.view_empty,null));


//        sceneryScrollListener = new RecyclerView.OnScrollListener() {
//            @Override
//            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
//                super.onScrollStateChanged(recyclerView, newState);
//                if(newState == RecyclerView.SCROLL_STATE_IDLE){
//                    RecyclerView.LayoutManager layoutManager = recyclerView.getLayoutManager();
//                    //因为StaggeredGridLayoutManager的特殊性可能导致最后显示的item存在多个，所以这里取到的是一个数组
//                    //得到这个数组后再取到数组中position值最大的那个就是最后显示的position值了
//                    int[] lastPositions = new int[2];
//                    int[] firstPositions = new int[2];
//                    ((StaggeredGridLayoutManager) layoutManager).findLastVisibleItemPositions(lastPositions);
//                    lastVisibleSceneryIndex = Utils.findMax(lastPositions);
//                    ((StaggeredGridLayoutManager) layoutManager).findFirstVisibleItemPositions(firstPositions);
//                    firstVisibleSceneryIndex = Utils.findMin(firstPositions);
//
//                }
//            }
//        };
//        rv_photo_scenery.addOnScrollListener(sceneryScrollListener);

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
                Logger.i(Logger.DEBUG_TAG,"onPageSelected(),pos="+position);
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
        vp_photo.setCurrentItem(0);

        //todo bug
        final Activity activity = getActivity();

        if(activity!=null && activity instanceof MainActivity){
            final String[] titles = new String[]{getContext().getString(R.string.photo_popular),
            getContext().getString(R.string.photo_portray),getContext().getString(R.string.photo_scenery)};
            ((MainActivity)activity).setToolbar(false);
            ((MainActivity)(activity)).getIndicator().post(new Runnable() {
                @Override
                public void run() {
                    ((MainActivity)activity).setIndicator(vp_photo,titles);
                }
            });

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
            if(rvPopularAdapter!=null) {
                rvPopularAdapter.setmDatas(getPopularList(), getPopularHeight());
            }else{
                Logger.e(Logger.DEBUG_TAG,"refresh error");
            }
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
            if(rvPortrayAdapter!=null) {
                rvPortrayAdapter.setmDatas(getPortrayList(), getPortrayHeight());
            }else{
                Logger.e(Logger.DEBUG_TAG,"refresh error");
            }
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
            if(rvSceneryAdapter!=null) {
                rvSceneryAdapter.setmDatas(getSceneryList(), getSceneryHeight());
            }else{
                Logger.e(Logger.DEBUG_TAG,"refresh error");
            }
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
//        clearData();
        if(MemExchange.getInstance().getPhotoPopularList().size()> Config.PerPageSize){
            MemExchange.getInstance().setPhotoPopularList(MemExchange.getInstance().getPhotoPopularList().subList(0,10));
            MemExchange.getInstance().setPhotoPopularPageIndex(1);
        }
        if(MemExchange.getInstance().getPhotoPortrayList().size()> Config.PerPageSize){
            MemExchange.getInstance().setPhotoPortrayList(MemExchange.getInstance().getPhotoPortrayList().subList(0,10));
            MemExchange.getInstance().setPhotoPortrayPageIndex(1);
        }
        if(MemExchange.getInstance().getPhotoSceneryList().size()> Config.PerPageSize){
            MemExchange.getInstance().setPhotoSceneryList(MemExchange.getInstance().getPhotoSceneryList().subList(0,10));
            MemExchange.getInstance().setPhotoSceneryPageIndex(1);
        }
        super.onDestroy();
    }

//    private void clearData(){
//        firstVisiblePopularIndex = -1;
//        lastVisiblePopularIndex = -1;
//        firstVisiblePortrayIndex = -1;
//        lastVisiblePortrayIndex = -1;
//        firstVisibleSceneryIndex = -1;
//        lastVisibleSceneryIndex = -1;
//    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        if (isVisibleToUser){
            Logger.e(Logger.DEBUG_TAG,TAG+"isVisibleToUser:"+(isVisibleToUser?"true":"false"));
        }
        super.setUserVisibleHint(isVisibleToUser);
    }
}
