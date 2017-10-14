package lava.bluepay.com.lavaapp.view.widget.gallery;

import android.content.Context;
import android.graphics.Color;
import android.graphics.PointF;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.RelativeLayout;

import lava.bluepay.com.lavaapp.view.widget.ViewUtils;


public class DraftFinishView extends RelativeLayout {
    private final int BACKGROUND_COLOR = Color.parseColor("#e6eaf0");
    private IZoomView ui;
    private PointF last = null;
    /**
     * 滑动删除距离（px）
     */
    private final int FINISH_DISTANCE = 200;
    /**
     * 透明度变化距离（px）
     */
    private final int ALPHA_DISTANCE = 800;
    /**
     * 大小缩放距离（px）
     */
    private final int SCALE_DISTANCE = 800;
    /**
     * 到达滑动距离的时候的图片缩小比例
     */
    private final int FINISH_PERCENT = 50;
    private int mWidth = 0;
    private int mHeight = 0;
    private OnFinishListener onFinishListener = null;

    private Context context;
    public DraftFinishView(Context context) {
        this(context, null);
    }

    public DraftFinishView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public DraftFinishView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
        init();
    }

    private void init() {
        setBackgroundColor(BACKGROUND_COLOR);
        getBackground().setAlpha(255);
        mWidth = ViewUtils.getScreenWidth(context);
        mHeight = ViewUtils.getScreenHeight(context);
    }

    public void setZoomView(IZoomView ui) {
        this.ui = ui;
    }

    /**
     * 事件拦截
     * 需要由自己处理的事件：
     * 1、图片处于最小状态时单指滑动操作
     *
     * @param event 用于操作事件
     * @return
     */
    private boolean isAlwaysSingle = true;
    private int mLastX = 0;
    private int mLastY = 0;

    @Override
    public boolean onInterceptTouchEvent(MotionEvent event) {
        boolean intercepted = false;
        int x = (int) event.getX();
        int y = (int) event.getY();

        if (event.getPointerCount() > 1) {
            isAlwaysSingle = false;
            intercepted = false;
        } else if (event.getPointerCount() == 1) {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN: {
                    isAlwaysSingle = true;
                    last = new PointF(event.getX(), event.getY());
                    intercepted = false;
                    break;
                }
                case MotionEvent.ACTION_MOVE: {
                    int deltaX = x - mLastX;
                    int deltaY = y - mLastY;
                    if (Math.abs(deltaY) > Math.abs(deltaX) && ui.isZoomToOriginalSize() && isAlwaysSingle && deltaY > 0 && y >= last.y) {
                        intercepted = true;
                    } else {
                        intercepted = false;
                    }
                    break;
                }
                case MotionEvent.ACTION_UP: {
                    isAlwaysSingle = true;
                    intercepted = false;
                    break;
                }
                default:
                    break;
            }
            mLastX = x;
            mLastY = y;
        }
        return intercepted;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN: {
                last = new PointF(event.getX(), event.getY());
                break;
            }
            case MotionEvent.ACTION_MOVE: {
                setZoomViewChange(event);
                break;
            }
            case MotionEvent.ACTION_UP: {
                PointF curr = new PointF(event.getX(), event.getY());
                float dy = curr.y - last.y;
                if (dy >= FINISH_DISTANCE) {
                    if (onFinishListener != null) {
                        onFinishListener.onFinish();
                    }
                } else {
                    resetView();
                }
                break;
            }
            default:
                break;
        }
        return true;
    }

    private void setZoomViewChange(MotionEvent event) {
        if (event == null) {
            setBackgroundColor(BACKGROUND_COLOR);
            getBackground().setAlpha(255);
            ui.setSize(mWidth, mHeight);
            ui.setMargin(0, 0);
            return;
        }

        PointF curr = new PointF(event.getX(), event.getY());

        //透明度
        float dy = curr.y - last.y;
        int alpha;
        if (dy < 0) {
            alpha = 255;
        } else if (dy >= 0 && dy < ALPHA_DISTANCE) {
            alpha = 255 - (int) (dy * 255 / ALPHA_DISTANCE);
        } else {
            alpha = 0;
        }
        getBackground().setAlpha(alpha);

        //大小
        int scale;
        if (dy < 0) {
            scale = 100;
        } else if (dy >= 0 && dy < SCALE_DISTANCE) {
            scale = (int) (100 - (100 - FINISH_PERCENT) * dy / SCALE_DISTANCE);
        } else {
            scale = FINISH_PERCENT;
        }

        ui.setSize(mWidth * scale / 100, mHeight * scale / 100);

        //移动
        int marginTop;
        int marginLeft;
        marginLeft = (int) (curr.x - last.x * scale / 100);
        marginTop = (int) (curr.y - last.y * scale / 100);
        ui.setMargin(marginLeft, marginTop);
    }

    private void resetView() {
        setZoomViewChange(null);
        ui.reset();
    }

    public void setOnFinishListener(OnFinishListener onFinishListener) {
        this.onFinishListener = onFinishListener;
    }

    public interface OnFinishListener {
        void onFinish();
    }
}
