package lava.bluepay.com.lavaapp.view.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.Gravity;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import java.lang.ref.WeakReference;
import lava.bluepay.com.lavaapp.R;
import lava.bluepay.com.lavaapp.common.Logger;
import lava.bluepay.com.lavaapp.common.Utils;
import okhttp3.internal.Util;

/**
 * viewPager的指示器
 * Created by bluepay on 2017/10/11.
 */

public class NewVPIndicator extends LinearLayout implements ViewPager.OnPageChangeListener {

    private WeakReference<ViewPager> mViewpager;
    private int mIndicatorColor;//指示器颜色
    private float mIndicatorWidth;
    private float mIndicatorHeight;
    private int mTextColor;//文字颜色
    private int mTextSize;
    private int mCurrentPosition = 0;

    private int mTabCount;//指示器item的个数
    private float mTabWidth;//指示器每个item的width


    private float mTranslationX;//底部线开始位置
    private Paint mPaint;//画底部线
    private LinearLayout tabGroup;
    private OnTabClickListener tabClickListener;


    private String[] mTitles;

    public void setmTitles(String[] titles){
        mTitles = titles;
    }

    private OnTabClickListener mListener;

    public interface OnTabClickListener {
        void onTabClick(int index);
    }

    /**
     * 设置tab点击回调事件
     */
    public void setOnTabClickListener(OnTabClickListener listener) {
        mListener = listener;
    }


    public NewVPIndicator(Context context) {
        super(context);
        init(context,null);
    }

    public NewVPIndicator(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context,attrs);
    }

    private void init(Context context,AttributeSet attrs){
        setOrientation(LinearLayout.HORIZONTAL);
        setGravity(Gravity.CENTER);
        if(isInEditMode()){//view还没加载完成
            return;
        }
        //todo 资源
        mIndicatorColor = getResources().getColor(R.color.activity_main_nav_radio_bg_check);

        mIndicatorWidth = ViewUtils.dp2px(getContext().getResources(),65);
        mIndicatorHeight = ViewUtils.dp2px(context.getResources(),1);
        mTextColor = getResources().getColor(R.color.activity_indicator_textColor_def);
        mTextSize = 18;

        mPaint = new Paint();
        mPaint.setColor(mIndicatorColor);
        mPaint.setStrokeWidth(2 * mIndicatorHeight);
    }

    /**
     * 主动设置viewPager
     * @param viewPager
     */
    public void setViewPager(ViewPager viewPager){
        mViewpager = new WeakReference<>(viewPager);
        if(mViewpager.get()!=null){
            mCurrentPosition = mViewpager.get().getCurrentItem();
            mViewpager.get().addOnPageChangeListener(this);
            onPageSelected(mCurrentPosition);
            mTabCount = mTitles.length;
            if(mTabCount>0) {
                mTabWidth = ((getWidth()== 0)?(ViewUtils.dp2px(getContext(),180)):getWidth())/mTabCount;
            }
            generateTitleView();
        }
    }

    private void generateTitleView(){
        if (getChildCount() > 0)
            this.removeAllViews();

        //创建TabItemView
        tabGroup = new LinearLayout(getContext());
        tabGroup.setOrientation(RadioGroup.HORIZONTAL);
        LayoutParams layoutParams = new LayoutParams(LayoutParams.MATCH_PARENT,
                LayoutParams.MATCH_PARENT);
        tabGroup.setLayoutParams(layoutParams);
        int count = mTitles.length;

        int[] colors = new int[]{
                mTextColor,
                mIndicatorColor
        };
        tabGroup.setWeightSum(count);
        for (int i = 0; i < count; i++) {
            LayoutParams lp = new LayoutParams(0, LayoutParams.MATCH_PARENT, 1);
            TabItemView tabItemView = new TabItemView(getContext());
            tabItemView.setTitle(mTitles[i]);
            tabItemView.setColor(colors, mTextSize);
            final int index = i;
            tabItemView.setOnCheckedListener(new TabItemView.onCheckedListener() {
                @Override
                public void onChecked(boolean isChecked) {
                    if (mListener != null) {
                        mListener.onTabClick(index);
                    }
                    if(mViewpager.get() != null) {
                        mViewpager.get().setCurrentItem(index, false);
                    }
                }
            });
            tabGroup.addView(tabItemView, i, lp);
        }
        addView(tabGroup);
//        if(mTabCount>0)
//            this.removeAllViews();
//        tabGroup = new LinearLayout(getContext());
//        tabGroup.setOrientation(LinearLayout.HORIZONTAL);
//        LayoutParams layoutParams = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
//                ViewGroup.LayoutParams.MATCH_PARENT);
//        tabGroup.setLayoutParams(layoutParams);
//        int count = mTitles.length;
//        tabGroup.setWeightSum(count);
//
//        int[] colors = new int[]{
//                mTextColor,
//                mIndicatorColor
//        };
//
//        for(int i =0;i<count;i++) {
//            RadioButton itemTab = new RadioButton(getContext());
//            LayoutParams params = new LayoutParams(0, ViewGroup.LayoutParams.MATCH_PARENT,1);
//            itemTab.setLayoutParams(params);
//            itemTab.setButtonDrawable(null);
//            itemTab.setText(mTitles[i]);
//            int[][] states = new int[][]{
//                    new int[]{-android.R.attr.state_checked}, // unchecked
//                    new int[]{android.R.attr.state_checked}  // checked
//            };
//
//            ColorStateList radioButtonTextColor = new ColorStateList(states, colors);
//            itemTab.setTextColor(radioButtonTextColor);
//            itemTab.setTextSize(TypedValue.COMPLEX_UNIT_SP,mTextSize);
//            final int index = i;
//            itemTab.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
//                @Override
//                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
//
//                    if(tabClickListener!=null) {
//                        tabClickListener.onViewClick(index);
//                    }
//                    if(mViewpager.get()!=null){
//                        mViewpager.get().setCurrentItem(index,false);//回调用onPageScrolled()方法
//                    }
//                }
//            });
//            tabGroup.addView(itemTab);
//        }
//        addView(tabGroup);

    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        if(mTabCount>0){
            mTabWidth = w/mTabCount;
        }
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        super.dispatchDraw(canvas);
        canvas.save();
        canvas.translate(mTranslationX,getHeight()-4);
        canvas.drawLine((mTabWidth - mIndicatorWidth) / 2, 0, (mTabWidth + mIndicatorWidth) / 2, 0, mPaint);
        canvas.restore();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        scroll(position,positionOffset);
    }

    private void scroll(final int position, float offset){
        mTranslationX = ((getWidth()== 0)?(ViewUtils.dp2px(getContext(),180)):getWidth()) / mTabCount * (position + offset);
        if (tabGroup != null && position < tabGroup.getChildCount()) {
            if (tabGroup.getChildAt(position) instanceof TabItemView) {
                for (int i = 0; i < tabGroup.getChildCount(); i++) {
                    if (i == position) {//防止一个radioButton 重复选中 会出现混乱
                        final TabItemView tabView = (TabItemView) tabGroup.getChildAt(i);
                        tabView.tabTitle.setOnCheckedChangeListener(null);//移除监听 //除非手动点击的才监听点击事件
                        tabView.setChecked(true);
                        tabView.tabTitle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {//开始监听
                            @Override
                            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                                if (!isChecked) return;
                                tabView.mCheckedListener.onChecked(true);
                            }
                        });
                    } else {
                        ((TabItemView) tabGroup.getChildAt(i)).setChecked(false);
                    }
                }

            }
        }
        invalidate();
//        if(getWidth() == 0){
//            Log.e("TT","NewVPIndicator-->onscroll,getWidth() == 0");
//            return;
//        }
//        mTranslationX = getWidth() / mTabCount * (position + offset);
//        if(tabGroup!=null && position<tabGroup.getChildCount()){
//            if(tabGroup.getChildAt(position) instanceof RadioButton){
//                for(int i =0;i<tabGroup.getChildCount();i++){
//                    if(i == position) {
//                        final RadioButton rb = (RadioButton) tabGroup.getChildAt(position);
//                        rb.setOnCheckedChangeListener(null);
//                        if(rb.isChecked() != true){
//                            rb.setChecked(true);
//                        }
//                        rb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
//                            @Override
//                            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
//                                if(!isChecked) return;
//                                if(mViewpager.get()!=null){
//                                    mViewpager.get().setCurrentItem(position,false);
//                                }
//                            }
//                        });
//                    }
//                    else{
//                        RadioButton rb = (RadioButton) tabGroup.getChildAt(position);
//                        if(rb.isChecked()!=false){
//                            rb.setChecked(false);
//                        }
//                    }
//                }
//            }
//        }
    }
    @Override
    public void onPageSelected(int position) {
        mCurrentPosition = position;
        Logger.i(Logger.DEBUG_TAG,"NewVPIndicator,onPageSelected(),pos="+position);
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }
//
//    /**
//     * 点击时间
//     */
//    public interface OnTabClickListener {
//        void onViewClick(int index);
//    }

}
