package lava.bluepay.com.lavaapp.view.adapter;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

/**
 * 主界面viewpager适配器
 */

public class ViewPagerAdapter extends PagerAdapter {

    private List<View> views;
    private List<String> titles;
    private Context context;

    public ViewPagerAdapter(List<View> views, List<String> titles, Context context) {
        this.views = views;
        this.titles = titles;
        this.context = context;
    }

    public ViewPagerAdapter(List<View> views,Context context){
        this.views = views;
        this.context = context;
    }
    public void setViews(List<View> views){
        this.views = views;
        notifyDataSetChanged();
    }

    public List<View> getViews() {
//        if(views == null){
//            views = new ArrayList<>();
//        }
        return views;
    }

    public View getViewOfIndex(int index){
        if(getViews()==null){
            return null;
        }
        if(index<0 || index>getCount()){
            return null;
        }
        return getViews().get(index);
    }

    @Override
    public int getCount() {
        if(views!=null){
            return views.size();
        }
        return 0;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        if(container!=null) {
            container.addView(getViewOfIndex(position));
        }
        return getViewOfIndex(position);
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        try {
            container.removeView(getViewOfIndex(position));
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }


}
