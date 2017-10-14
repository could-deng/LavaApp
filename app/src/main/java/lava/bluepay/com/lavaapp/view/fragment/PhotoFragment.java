package lava.bluepay.com.lavaapp.view.fragment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import lava.bluepay.com.lavaapp.R;
import lava.bluepay.com.lavaapp.view.activity.GalleryActivity;
import lava.bluepay.com.lavaapp.view.activity.MainActivity;
import lava.bluepay.com.lavaapp.view.activity.ViewPagerActivity;
import lava.bluepay.com.lavaapp.view.adapter.RecyclerViewAdapter;
import lava.bluepay.com.lavaapp.view.adapter.ViewPagerAdapter;
import lava.bluepay.com.lavaapp.view.bean.PhotoBean;
import lava.bluepay.com.lavaapp.view.widget.DividerGridItemDecoration;
import lava.bluepay.com.lavaapp.view.widget.EmptyRecyclerView;

/**
 * Created by bluepay on 2017/10/9.
 */

public class PhotoFragment extends BaseFragment {

    public static final String TAG = "photoFragment";

    private ViewPager vp_photo;

    private EmptyRecyclerView rvPopular;
    private RecyclerViewAdapter rvPopularAdapter;
    private List<PhotoBean> popularDatas;
    private List<Integer> popularHeights;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        return initView(inflater,container);
    }

    @Override
    public void onResume() {
        super.onResume();
        initPopularData();
    }

    public void initPopularData(){
        popularDatas = new ArrayList<>();
        for (int i = 'A'; i < 'K'; i++)
        {
            popularDatas.add(new PhotoBean(""+(char)i,"http://photocdn.sohu.com/20121119/Img358016160.jpg"));
        }
        popularHeights = new ArrayList<>();
        for (int i = 0; i < popularDatas.size(); i++)
        {
            popularHeights.add( (int) (300 + Math.random() * 300));
        }
        rvPopularAdapter.setmDatas(popularDatas,popularHeights);
    }

    private View initView(LayoutInflater inflater, ViewGroup container){
        View view  = inflater.inflate(R.layout.fragment_photo,container,false);

        List<View> views = new ArrayList<>();
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
//                intent.setClass(getContext(),GalleryActivity.class);
//                ArrayList<String> imageUrls = new ArrayList<>();
//                imageUrls.add("https://timgsa.baidu.com/timg?image&quality=80&size=b10000_10000&sec=1507971152&di=60abb664a9da6550d3a887afc8bfcdfc&src=http://attach.bbs.miui.com/forum/201501/25/203109lxh7tun7gy15l5x2.jpg");
//                imageUrls.add("https://timgsa.baidu.com/timg?image&quality=80&size=b10000_10000&sec=1507971152&di=60abb664a9da6550d3a887afc8bfcdfc&src=http://attach.bbs.miui.com/forum/201501/25/203109lxh7tun7gy15l5x2.jpg");
//                imageUrls.add("https://timgsa.baidu.com/timg?image&quality=80&size=b10000_10000&sec=1507971152&di=60abb664a9da6550d3a887afc8bfcdfc&src=http://attach.bbs.miui.com/forum/201501/25/203109lxh7tun7gy15l5x2.jpg");
//                imageUrls.add("https://timgsa.baidu.com/timg?image&quality=80&size=b10000_10000&sec=1507971152&di=60abb664a9da6550d3a887afc8bfcdfc&src=http://attach.bbs.miui.com/forum/201501/25/203109lxh7tun7gy15l5x2.jpg");
//                imageUrls.add("https://timgsa.baidu.com/timg?image&quality=80&size=b10000_10000&sec=1507971152&di=60abb664a9da6550d3a887afc8bfcdfc&src=http://attach.bbs.miui.com/forum/201501/25/203109lxh7tun7gy15l5x2.jpg");
//                intent.putExtra("imageUrl", imageUrls);
                intent.setClass(getContext(), ViewPagerActivity.class);
                startActivity(intent);
            }

            @Override
            public void onItemLongClick(View view, int position) {
                Log.e("TT","onItemLongClick");
            }
        });
        rvPopular.setAdapter(rvPopularAdapter);


        views.add(popularView);
        View sceneryView = inflater.inflate(R.layout.fragment_photo_scenery,null);
        views.add(sceneryView);
        View portrayView = inflater.inflate(R.layout.fragment_photo_portray,null);
        views.add(portrayView);

        vp_photo = (ViewPager) view.findViewById(R.id.vp_photo);
        if(vp_photo==null){
            Log.e("TT","PhotoFragment->error");
            return view;
        }
        ViewPagerAdapter adapter = new ViewPagerAdapter(views,getContext());
        vp_photo.setAdapter(adapter);

        Activity activity = getActivity();
        String[] titles;
        if(getActivity()!=null){
            titles = new String[]{getContext().getString(R.string.photo_popular),
            getContext().getString(R.string.photo_portray),getContext().getString(R.string.photo_scenery)};
            ((MainActivity)activity).setToolbar(false);
            ((MainActivity)activity).setIndicator(vp_photo,titles);
        }
        return view;
    }
}
