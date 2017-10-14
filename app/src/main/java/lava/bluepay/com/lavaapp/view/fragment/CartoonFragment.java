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

public class CartoonFragment extends BaseFragment {

    public static final String TAG = "cartoonFragment";

    private ViewPager vp_cartoon;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        return initView(inflater,container);
    }


    private View initView(LayoutInflater inflater,ViewGroup container){
        View view  = inflater.inflate(R.layout.fragment_cartoon,container,false);

        List<View> views = new ArrayList<>();
        View popularView = inflater.inflate(R.layout.fragment_cartoon_popular,null);
        views.add(popularView);
        View funnyView = inflater.inflate(R.layout.fragment_cartoon_funny,null);
        views.add(funnyView);
        View horrorView = inflater.inflate(R.layout.fragment_cartoon_horror,null);
        views.add(horrorView);

        vp_cartoon = (ViewPager) view.findViewById(R.id.vp_cartoon);
        if(vp_cartoon==null){
            Log.e("TT","PhotoFragment->error");
            return view;
        }
        ViewPagerAdapter adapter = new ViewPagerAdapter(views,getContext());
        vp_cartoon.setAdapter(adapter);

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
}
