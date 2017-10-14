package lava.bluepay.com.lavaapp.view.activity;

import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import java.util.ArrayList;
import lava.bluepay.com.lavaapp.R;
import lava.bluepay.com.lavaapp.view.adapter.GalleryPagerAdapter;
import lava.bluepay.com.lavaapp.view.widget.ViewUtils;
import lava.bluepay.com.lavaapp.view.widget.gallery.DraftFinishView;
import lava.bluepay.com.lavaapp.view.widget.gallery.GalleryViewPager;
import lava.bluepay.com.lavaapp.view.widget.gallery.ZoomImageView;


public class GalleryActivity extends BaseActivity {
//    private LinearLayout actionBar = null;
//    private ImageView backBtn = null;
//    private TextView indexText = null;
    private DraftFinishView galleryHolder;
    private GalleryViewPager mGallery = null;
    private GalleryPagerAdapter mAdapter = null;
    private String docName;
    private ArrayList<String> imageUrls = null;
    private int currentItem = 0;
    /**
     * 标记ActionBar当前是否出现
     */
    private boolean isActionBarShow = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        customEnterAnimation();
        setContentView(R.layout.activity_gallery);
        initToolbar();
        initView();
        setToolbar();

        initData();
        initEvents();
    }
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
        hideActionBar();
    }

    @Override
    public void finish() {
        super.finish();
        customExitAnimation();
    }

    protected boolean customEnterAnimation() {
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        return true;
    }

    protected boolean customExitAnimation() {
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        return true;
    }

    private void initView() {
//        actionBar = (LinearLayout) findViewById(R.id.actionbar_image_zoom);
//        backBtn = (ImageView) findViewById(R.id.back_image_zoom);
//        indexText = (TextView) findViewById(R.id.index_image_zoom);
        galleryHolder = (DraftFinishView) findViewById(R.id.gallery_holder);
        mGallery = (GalleryViewPager) findViewById(R.id.zoom_image_container);
    }

    private void initData() {
        imageUrls = getIntent().getStringArrayListExtra("imageUrl");

        currentItem = getIntent().getIntExtra("index", 0);
        if (imageUrls == null || imageUrls.size() == 0) {
            finish();
        }

//        indexText.setText(docName);
        mAdapter = new GalleryPagerAdapter(this, imageUrls);
        mGallery.setPageMargin((int) ViewUtils.dp2px(this.getResources(),10));
        mGallery.setAdapter(mAdapter);
        mGallery.setOffscreenPageLimit(1);
        mGallery.setCurrentItem(currentItem, false);
        galleryHolder.setZoomView(mGallery);
    }

    private void initEvents() {
//        backBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                finish();
//            }
//        });

        mGallery.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                ZoomImageView zoomImage = mAdapter.getZoomImageByIndex(currentItem);
                zoomImage.reset();
                currentItem = position;
//                indexText.setText(docName);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        mGallery.setOnCustomClickListener(new GalleryViewPager.OnCustomClickListener() {
            @Override
            public void onSingleTap() {
                if (isActionBarShow) {
                    hideActionBar();
                } else {
                    showActionBar();
                }
            }
        });

        galleryHolder.setOnFinishListener(new DraftFinishView.OnFinishListener() {
            @Override
            public void onFinish() {
                finish();
            }
        });
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

}
