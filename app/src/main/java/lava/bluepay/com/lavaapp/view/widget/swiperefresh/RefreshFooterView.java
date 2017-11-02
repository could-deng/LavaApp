package lava.bluepay.com.lavaapp.view.widget.swiperefresh;

import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.util.AttributeSet;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import lava.bluepay.com.lavaapp.R;


public class RefreshFooterView extends RelativeLayout implements SwipeTrigger, SwipeLoadMoreTrigger {

//    private ImageView iv_loading;
    private TextView tv_load_info;
//    private ImageView iv_pull;
//    private AnimationDrawable mAnimDrawableLoading;
//    private AnimationDrawable mAnimDrawablePull;
    private int mFooterHeight = 100;
    private Context context;

    public RefreshFooterView(Context context) {
        this(context, null);
    }

    public RefreshFooterView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RefreshFooterView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
//        iv_loading = (ImageView) findViewById(R.id.iv_loading);
//        iv_pull = (ImageView) findViewById(R.id.iv_pull);
        tv_load_info = (TextView) findViewById(R.id.tv_load_info);
//        mAnimDrawableLoading = (AnimationDrawable) iv_loading.getBackground();
//        mAnimDrawablePull = (AnimationDrawable) iv_pull.getBackground();
    }

    @Override
    public void onLoadMore() {
        String text = tv_load_info.getText().toString();
        if (text != null && !text.equals(context.getString(R.string.swipelayout_loading))) {
            tv_load_info.setText(context.getString(R.string.swipelayout_loading));
        }
    }

    @Override
    public void onLoadNothing() {
        String text = tv_load_info.getText().toString();
        if (text != null && !text.equals(context.getString(R.string.swipelayout_load_nothing))) {
            tv_load_info.setText(context.getString(R.string.swipelayout_load_nothing));
        }
    }

    @Override
    public void onLoadError() {
        String text = tv_load_info.getText().toString();
        if (text != null && !text.equals(context.getString(R.string.swipelayout_load_failure))) {
            tv_load_info.setText(context.getString(R.string.swipelayout_load_failure));
        }
    }

    @Override
    public void onPrepare() {

    }

    @Override
    public void onMove(int y, boolean isComplete, boolean automatic) {
        if (!isComplete) {
//            if (!mAnimDrawablePull.isRunning()) {
//                mAnimDrawablePull.start();
//            }
            if (-y >= mFooterHeight) {
                if (tv_load_info.getText() != context.getString(R.string.swipelayout_release))
                    tv_load_info.setText(context.getString(R.string.swipelayout_release));
            } else {
                String text = tv_load_info.getText().toString();
                if (text != null && !text.equals(context.getString(R.string.swipelayout_to_load_more))) {
                    tv_load_info.setText(context.getString(R.string.swipelayout_to_load_more));
                }
            }
        }
    }

    @Override
    public void onRelease() {
//        iv_loading.setVisibility(VISIBLE);
//        iv_pull.setVisibility(GONE);
//        if (!mAnimDrawableLoading.isRunning()) {
//            mAnimDrawableLoading.start();
//            if (mAnimDrawablePull.isRunning()) {
//                mAnimDrawablePull.stop();
//            }
//        }
    }

    @Override
    public void onComplete() {
        String text = tv_load_info.getText().toString();
        if (text != null && !text.equals(context.getString(R.string.swipelayout_load_complete))) {
            tv_load_info.setText(context.getString(R.string.swipelayout_load_complete));
        }
    }

    @Override
    public void onReset() {
//        if (mAnimDrawableLoading.isRunning()) {
//            mAnimDrawableLoading.stop();
//        }
//        if (mAnimDrawablePull.isRunning()) {
//            mAnimDrawablePull.stop();
//        }
//        iv_loading.setVisibility(GONE);
//        iv_pull.setVisibility(VISIBLE);
        String text = tv_load_info.getText().toString();
        if (text != null && !text.equals(context.getString(R.string.swipelayout_to_load_more))) {
            tv_load_info.setText(context.getString(R.string.swipelayout_to_load_more));
        }
    }
}
