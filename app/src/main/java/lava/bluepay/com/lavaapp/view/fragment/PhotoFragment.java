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
import lava.bluepay.com.lavaapp.R;
import lava.bluepay.com.lavaapp.common.Logger;
import lava.bluepay.com.lavaapp.model.MemExchange;
import lava.bluepay.com.lavaapp.model.api.bean.CategoryBean;
import lava.bluepay.com.lavaapp.view.activity.MainActivity;
import lava.bluepay.com.lavaapp.view.activity.PlayVideoActivity;
import lava.bluepay.com.lavaapp.view.activity.ViewPagerActivity;
import lava.bluepay.com.lavaapp.view.adapter.RecyclerViewAdapter;
import lava.bluepay.com.lavaapp.view.adapter.ViewPagerAdapter;
import lava.bluepay.com.lavaapp.view.bean.PhotoBean;
import lava.bluepay.com.lavaapp.view.widget.EmptyRecyclerView;

/**
 * Created by bluepay on 2017/10/9.
 */

public class PhotoFragment extends BaseFragment {

    public static final String TAG = "photoFragment";

    private ViewPager vp_photo;

    private EmptyRecyclerView rvPopular;
    private RecyclerViewAdapter rvPopularAdapter;


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
//        ((MainActivity)getActivity()).sendPhotoPopularListRequest();
    }


    public void refreshPopular(){
        if(getPopularList()!=null && getPopularList().getData()!=null && getPopularList().getData().getData()!=null
                && getPopularList().getData().getData().size() > 0){
//            List<CategoryBean.DataBeanX.DataBean> dateList = getPopularList().getData().getData();
//            List<Integer> popularHeights = new ArrayList<>();
//            for (int i = 0; i < dateList.size(); i++)
//            {
//                popularHeights.add( (int) (300 + Math.random() * 300));
//            }

            rvPopularAdapter.setmDatas(getPopularList(),getPopularHeight());
        }
    }


    private CategoryBean getPopularList(){
//        for(int i=0;i<MemExchange.getInstance().getPhotoPopularList();i++)
        return MemExchange.getInstance().getPhotoPopularList();
    }
    private List<Integer> getPopularHeight(){
        return MemExchange.getInstance().getPopularHeights();
    }


    private View initView(LayoutInflater inflater, ViewGroup container){
        View view  = inflater.inflate(R.layout.fragment_photo,container,false);

        List<View> views = new ArrayList<>();

        //region==========类别1===========================================================================

        View popularView = inflater.inflate(R.layout.fragment_photo_popular,null);
        rvPopular = (EmptyRecyclerView) popularView.findViewById(R.id.rv_photo_popular);
        rvPopularAdapter = new RecyclerViewAdapter(getActivity());
        rvPopular.setLayoutManager(new StaggeredGridLayoutManager(2,StaggeredGridLayoutManager.VERTICAL));//改为Horizontal则表现为滚动的GridView效果
        rvPopular.setItemAnimator(new DefaultItemAnimator());//动画
//        rvPopular.addItemDecoration(new DividerGridItemDecoration(getActivity()));
        rvPopularAdapter.setItemClickListener(new RecyclerViewAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Intent intent = new Intent();
                intent.setClass(getContext(), ViewPagerActivity.class);
                startActivity(intent);
            }

            @Override
            public void onItemLongClick(View view, int position) {
                Logger.e(Logger.DEBUG_TAG,"onItemLongClick");
            }
        });
        rvPopularAdapter.addFooterView(LayoutInflater.from(getContext()).inflate(R.layout.view_footer,null));

        rvPopular.setAdapter(rvPopularAdapter);


        views.add(popularView);

        //endregion==========类别1===========================================================================

        //region==========类别2===========================================================================

        View sceneryView = inflater.inflate(R.layout.fragment_photo_scenery,null);
        Button btn = (Button) sceneryView.findViewById(R.id.bt_to_video_activity);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent =new Intent(getActivity(), PlayVideoActivity.class);
                startActivity(intent);
            }
        });
        views.add(sceneryView);

        //endregion==========类别2===========================================================================

        //region==========类别3===========================================================================

        View portrayView = inflater.inflate(R.layout.fragment_photo_portray,null);
        views.add(portrayView);

        //endregion==========类别3===========================================================================

        vp_photo = (ViewPager) view.findViewById(R.id.vp_photo);
        if(vp_photo==null){
            Logger.e(Logger.DEBUG_TAG,"PhotoFragment->error");
            return view;
        }
        ViewPagerAdapter adapter = new ViewPagerAdapter(views,getContext());
        vp_photo.setOffscreenPageLimit(views.size() - 1);
        vp_photo.setAdapter(adapter);

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
}
