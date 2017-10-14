package lava.bluepay.com.lavaapp.view.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import lava.bluepay.com.lavaapp.R;
import lava.bluepay.com.lavaapp.view.activity.MainActivity;
import lava.bluepay.com.lavaapp.view.adapter.ViewPagerAdapter;

/**
 * Created by bluepay on 2017/10/9.
 */

public class VideoFragment extends BaseFragment {

    public static final String TAG = "videoFragment";

    private ViewPager vp_video;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        return initView(inflater,container);
    }


    private View initView(LayoutInflater inflater,ViewGroup container){
        View view  = inflater.inflate(R.layout.fragment_video,container,false);

        List<View> views = new ArrayList<>();
        View popularView = inflater.inflate(R.layout.fragment_video_popular,null);
        views.add(popularView);
        View funnyView = inflater.inflate(R.layout.fragment_video_funny,null);
        views.add(funnyView);
        View sportsView = inflater.inflate(R.layout.fragment_video_sports,null);
        views.add(sportsView);

        vp_video = (ViewPager) view.findViewById(R.id.vp_video);
        if(vp_video ==null){
            Log.e("TT","PhotoFragment->error");
            return view;
        }
        ViewPagerAdapter adapter = new ViewPagerAdapter(views,getContext());
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
}
