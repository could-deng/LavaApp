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
import lava.bluepay.com.lavaapp.Config;
import lava.bluepay.com.lavaapp.R;
import lava.bluepay.com.lavaapp.model.MemExchange;
import lava.bluepay.com.lavaapp.model.api.bean.CategoryBean;
import lava.bluepay.com.lavaapp.view.adapter.DraweePagerAdapter;
import lava.bluepay.com.lavaapp.view.widget.MultiTouchViewPager;
import lava.bluepay.com.lavaapp.view.widget.ViewUtils;


public class ViewPagerActivity extends BaseActivity {

    MultiTouchViewPager viewPager;
    private LinearLayout dotContainer;
    private ViewPager.OnPageChangeListener listener;
    private List<ImageView> dotViewList = new ArrayList<>();

    private DraweePagerAdapter pagerAdapter;

    private List<CategoryBean.DataBeanX.DataBean> picData;
    private int nowIndex;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_viewpager);
        initToolbar();
        setToolbar();
        initData();

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

        pagerAdapter.setPicUrlList(getVPData());
        viewPager.setAdapter(pagerAdapter);
        viewPager.addOnPageChangeListener(getPageChangeListener());

        if (pagerAdapter.getPicUrlList().size() > 1) {//当有两个banner 才可以来回滚动
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    ViewUtils.dp2px(this, 7), ViewUtils.dp2px(this, 7));
            params.setMargins(ViewUtils.dp2px(this, 3), ViewUtils.dp2px(this, 1), ViewUtils.dp2px(this, 3), ViewUtils.dp2px(this, 1));

            dotContainer.removeAllViews();
            dotViewList.clear();
            for (int i = 0; i < pagerAdapter.getPicUrlList().size(); i++) {
                ImageView dot = new ImageView(this);
                if (i == nowIndex) {
                    dot.setImageResource(R.drawable.dot_focused);
                } else {
                    dot.setImageResource(R.drawable.dot_normal);
                }
                dotContainer.addView(dot, params);
                dotViewList.add(dot);
            }
            dotContainer.setGravity(Gravity.CENTER);
            // 切换到指定页面
            viewPager.setCurrentItem(nowIndex, false);
        }

    }

//    @Override
//    protected void onSaveInstanceState(Bundle outState) {
//        super.onSaveInstanceState(outState);
//        outState.put
//    }
//
//    @Override
//    protected void onRestoreInstanceState(Bundle savedInstanceState) {
//        super.onRestoreInstanceState(savedInstanceState);
//
//    }


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


    private void initData(){
        Bundle bundle = getIntent().getExtras();
        if(bundle==null){
            return;
        }
        int categoryId = bundle.getInt("categoryId",-1);
        int index = bundle.getInt("index",-1);
        if(categoryId == -1 || index == -1){
            return;
        }
        int startIndex = (index -Config.ViewPagerMaxSize/2)<0?0:index-(Config.ViewPagerMaxSize/2);//包括
        int endIndex = (index+ Config.ViewPagerMaxSize/2)>getCategoryList(categoryId).size()?getCategoryList(categoryId).size():(index+ (Config.ViewPagerMaxSize/2));//包括
        if(picData == null){
            picData = new ArrayList<>();
        }
        picData = getCategoryList(categoryId).subList(startIndex,endIndex);
        nowIndex = index-startIndex;//包括

    }

    private List<CategoryBean.DataBeanX.DataBean> getCategoryList(int categoryId){
        if(categoryId == Config.CategoryPhotoPopular){
            return MemExchange.getInstance().getPhotoPopularList();
        }else if(categoryId == Config.CategoryPhotoPortray){
            return MemExchange.getInstance().getPhotoPortrayList();
        }else if(categoryId == Config.CategoryPhotoScenery){
            return MemExchange.getInstance().getPhotoSceneryList();
        }
        else if(categoryId == Config.CategoryVideoPopular){
            return MemExchange.getInstance().getVideoPopularList();
        }else if(categoryId == Config.CategoryVideoFunny){
            return MemExchange.getInstance().getVideoFunnyList();
        }else if(categoryId == Config.CategoryVideoSport){
            return MemExchange.getInstance().getVideoSportList();
        }
        else if (categoryId == Config.CategoryCartoonPopular) {
            return MemExchange.getInstance().getCartoonPopularList();
        } else if (categoryId == Config.CategoryCartoonFunny) {
            return MemExchange.getInstance().getCartoonFunnyList();
        }
//        else if (categoryId == Config.CategoryCartoonhorror) {
            return MemExchange.getInstance().getCartoonHorrorList();
//        }
    }

    private List<CategoryBean.DataBeanX.DataBean> getVPData(){
        if(picData == null){
            picData = new ArrayList<>();
        }
        return picData;
    }

    @Override
    protected void onDestroy() {
//        RefWatcher refWatcher = MixApp.getRefWatcher(this);
//        refWatcher.watch(this);
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
