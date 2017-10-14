package lava.bluepay.com.lavaapp.view.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Animatable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.backends.pipeline.PipelineDraweeControllerBuilder;
import com.facebook.drawee.controller.BaseControllerListener;
import com.facebook.imagepipeline.image.ImageInfo;

import java.util.ArrayList;
import java.util.List;

import lava.bluepay.com.lavaapp.R;
import lava.bluepay.com.lavaapp.view.widget.MultiTouchViewPager;
import lava.bluepay.com.lavaapp.view.widget.ViewUtils;
import lava.bluepay.com.lavaapp.view.widget.photodraweeview.PhotoDraweeView;


public class ViewPagerActivity extends AppCompatActivity {

    MultiTouchViewPager viewPager;
    private LinearLayout dotContainer;
    private ViewPager.OnPageChangeListener listener;
    private List<ImageView> dotViewList = new ArrayList<>();

    private DraweePagerAdapter pagerAdapter;

    private List<String> picData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_viewpager);

        ((Toolbar) findViewById(R.id.toolbar)).setNavigationOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        onBackPressed();
                    }
                });

        dotContainer = (LinearLayout) findViewById(R.id.dot_container);
        viewPager = (MultiTouchViewPager) findViewById(R.id.view_pager);
        pagerAdapter = new DraweePagerAdapter();
        viewPager.setAdapter(pagerAdapter);
        viewPager.addOnPageChangeListener(getPageChangeListener());


        pagerAdapter.setPicUrlList(setVPData());

        if (pagerAdapter.getPicUrlList().size() > 1) {//当有两个banner 才可以来回滚动
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    ViewUtils.dp2px(this, 7), ViewUtils.dp2px(this, 7));
            params.setMargins(ViewUtils.dp2px(this, 3), ViewUtils.dp2px(this, 1), ViewUtils.dp2px(this, 3), ViewUtils.dp2px(this, 1));

            dotContainer.removeAllViews();
            dotViewList.clear();
            for (int i = 0; i < pagerAdapter.getPicUrlList().size(); i++) {
                ImageView dot = new ImageView(this);
                if (i == 0) {
                    dot.setImageResource(R.drawable.dot_focused);
                } else {
                    dot.setImageResource(R.drawable.dot_normal);
                }
                dotContainer.addView(dot, params);
                dotViewList.add(dot);
            }
            viewPager.setCurrentItem(0, false);
        }

    }

    private ViewPager.OnPageChangeListener getPageChangeListener() {
        if (listener == null) {
            listener = new ViewPager.OnPageChangeListener() {
                @Override
                public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                }

                @Override
                public void onPageSelected(int position) {
                    if (viewPager != null && pagerAdapter != null && dotViewList != null) {
                        for (int i = 0; i < dotViewList.size(); i++) {
                            if(dotViewList.get(i) != null) {
                                (dotViewList.get(i)).setImageResource(R.drawable.dot_normal);
                            }
                        }
                        if(position >= 0 && position < dotViewList.size()) {
                            if(dotViewList.get(position) != null) {
                                (dotViewList.get(position)).setImageResource(R.drawable.dot_focused);
                            }
                        }
                    }
                }

                @Override
                public void onPageScrollStateChanged(int state) {
                }
            };
        }
        return listener;
    }


    private List<String> setVPData(){
       if(picData == null){
           picData = new ArrayList<>();
           picData.add("https://timgsa.baidu.com/timg?image&quality=80&size=b10000_10000&sec=1507971152&di=60abb664a9da6550d3a887afc8bfcdfc&src=http://attach.bbs.miui.com/forum/201501/25/203109lxh7tun7gy15l5x2.jpg");
           picData.add("https://timgsa.baidu.com/timg?image&quality=80&size=b10000_10000&sec=1507971152&di=60abb664a9da6550d3a887afc8bfcdfc&src=http://attach.bbs.miui.com/forum/201501/25/203109lxh7tun7gy15l5x2.jpg");
           picData.add("https://timgsa.baidu.com/timg?image&quality=80&size=b10000_10000&sec=1507971152&di=60abb664a9da6550d3a887afc8bfcdfc&src=http://attach.bbs.miui.com/forum/201501/25/203109lxh7tun7gy15l5x2.jpg");
       }
       return picData;
    }
    public class DraweePagerAdapter extends PagerAdapter {

        private List<String> picUrlList;


        public void setPicUrlList(List<String> picUrlList){
            this.picUrlList = picUrlList;
            notifyDataSetChanged();
        }
        public List<String> getPicUrlList(){
            return picUrlList;
        }
        @Override
        public int getCount() {
            if(picUrlList!=null && picUrlList.size()>0){
                return picUrlList.size();
            }
            return 0;
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }

        @Override
        public Object instantiateItem(ViewGroup viewGroup, int position) {
            if(picUrlList == null || picUrlList.size()<=0){
                return null;
            }
            final PhotoDraweeView photoDraweeView = new PhotoDraweeView(viewGroup.getContext());
            PipelineDraweeControllerBuilder controller = Fresco.newDraweeControllerBuilder();
            controller.setUri(Uri.parse(picUrlList.get(position)));
//            controller.setUri(Uri.parse("https://timgsa.baidu.com/timg?image&quality=80&size=b10000_10000&sec=1507971152&di=60abb664a9da6550d3a887afc8bfcdfc&src=http://attach.bbs.miui.com/forum/201501/25/203109lxh7tun7gy15l5x2.jpg"));
            controller.setOldController(photoDraweeView.getController());
            controller.setControllerListener(new BaseControllerListener<ImageInfo>() {
                @Override
                public void onFinalImageSet(String id, ImageInfo imageInfo, Animatable animatable) {
                    super.onFinalImageSet(id, imageInfo, animatable);
                    if (imageInfo == null) {
                        return;
                    }
                    photoDraweeView.update(imageInfo.getWidth(), imageInfo.getHeight());
                }
            });
            photoDraweeView.setController(controller.build());

            try {
                viewGroup.addView(photoDraweeView, ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT);
            } catch (Exception e) {
                e.printStackTrace();
            }

            return photoDraweeView;
        }
    }
}
