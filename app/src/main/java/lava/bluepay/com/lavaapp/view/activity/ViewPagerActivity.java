package lava.bluepay.com.lavaapp.view.activity;

import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.Gravity;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import java.util.ArrayList;
import java.util.List;
import lava.bluepay.com.lavaapp.R;
import lava.bluepay.com.lavaapp.model.MemExchange;
import lava.bluepay.com.lavaapp.view.adapter.DraweePagerAdapter;
import lava.bluepay.com.lavaapp.view.widget.MultiTouchViewPager;
import lava.bluepay.com.lavaapp.view.widget.ViewUtils;


public class ViewPagerActivity extends BaseActivity {

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
        initToolbar();
        setToolbar();

        dotContainer = (LinearLayout) findViewById(R.id.dot_container);
        viewPager = (MultiTouchViewPager) findViewById(R.id.view_pager);
        pagerAdapter = new DraweePagerAdapter(this);
        pagerAdapter.setClickListener(new DraweePagerAdapter.OnCustomClickListener() {
            @Override
            public void onSingleTap() {
                if (isActionBarShow) {
                    hideActionBar();
                } else {
                    showActionBar();
                }
            }
        });
        viewPager.setClickable(true);
        viewPager.setAdapter(pagerAdapter);
        viewPager.addOnPageChangeListener(getPageChangeListener());


        pagerAdapter.setPicUrlList(getVPData());

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
            dotContainer.setGravity(Gravity.CENTER);
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


//    private List<String> setVPData(Bundle bundle){
//        if(picData == null){
//            picData = new ArrayList<>();
//        }
//       //todo 如果某一类的条目很多需要只显示一部分
//       int categoryId = bundle.getInt("categoryId",-1);
//        switch (categoryId){
//            case 0:
//                picData = MemExchange.getInstance().getPhotoPopularList().getData().;
//                break;
//        }
//    }

    private List<String> getVPData(){
        if(picData == null){
            picData = new ArrayList<>();
            picData.add("https://timgsa.baidu.com/timg?image&quality=80&size=b10000_10000&sec=1507971152&di=60abb664a9da6550d3a887afc8bfcdfc&src=http://attach.bbs.miui.com/forum/201501/25/203109lxh7tun7gy15l5x2.jpg");
            picData.add("https://timgsa.baidu.com/timg?image&quality=80&size=b10000_10000&sec=1507971152&di=60abb664a9da6550d3a887afc8bfcdfc&src=http://attach.bbs.miui.com/forum/201501/25/203109lxh7tun7gy15l5x2.jpg");
            picData.add("https://timgsa.baidu.com/timg?image&quality=80&size=b10000_10000&sec=1507971152&di=60abb664a9da6550d3a887afc8bfcdfc&src=http://attach.bbs.miui.com/forum/201501/25/203109lxh7tun7gy15l5x2.jpg");
        }
        return picData;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

    }


    /**
     * 标记ActionBar当前是否出现
     */
    private boolean isActionBarShow = true;

    private void setToolbar(){
        if (toolbar == null) return;
        toolbar.setNavigationIcon(null);
        toolbar.setNavigationOnClickListener(null);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeAsUpIndicator(R.drawable.toolbar_back);
        }
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    /**
     * 动画隐藏actionbar
     */
    private void hideActionBar() {
        isActionBarShow = false;
        Animation hideAnim = AnimationUtils.loadAnimation(this, R.anim.action_bar_hide);
        hideAnim.setFillAfter(true);
        hideAnim.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                toolbar.setVisibility(View.GONE);
                toolbar.clearAnimation();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        toolbar.startAnimation(hideAnim);
    }
    /**
     * 动画展示actionbar
     */
    private void showActionBar() {
        isActionBarShow = true;
        toolbar.setVisibility(View.VISIBLE);
        Animation startAnim = AnimationUtils.loadAnimation(this, R.anim.action_bar_show);
        startAnim.setFillAfter(true);
        toolbar.startAnimation(startAnim);
    }


}
