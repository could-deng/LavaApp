package lava.bluepay.com.lavaapp.view.adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.drawable.Animatable;
import android.os.Build;
import android.support.v4.view.PagerAdapter;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.backends.pipeline.PipelineDraweeControllerBuilder;
import com.facebook.drawee.controller.BaseControllerListener;
import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.imagepipeline.image.ImageInfo;

import java.util.List;

import lava.bluepay.com.lavaapp.common.ImageUtils;
import lava.bluepay.com.lavaapp.common.ThreadManager;
import lava.bluepay.com.lavaapp.view.widget.ViewUtils;
import lava.bluepay.com.lavaapp.view.widget.photodraweeview.PhotoDraweeView;

/**
 * Created by bluepay on 2017/10/16.
 */

public class DraweePagerAdapter extends PagerAdapter {


    public interface OnCustomClickListener {
        void onSingleTap();
    }
    private OnCustomClickListener clickListener;
    private List<String> picUrlList;
    private Context context;


    public void setClickListener(OnCustomClickListener clickListener) {
        this.clickListener = clickListener;
    }

    public DraweePagerAdapter(Context context) {
        this.context = context;
    }

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
    public Object instantiateItem(ViewGroup viewGroup, final int position) {
        if(picUrlList == null || picUrlList.size()<=0){
            return null;
        }
        final PhotoDraweeView photoDraweeView = new PhotoDraweeView(viewGroup.getContext());
        PipelineDraweeControllerBuilder controller = Fresco.newDraweeControllerBuilder();

//            controller.setUri(Uri.parse(picUrlList.get(position)));

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
        ThreadManager.executeOnSubThread1(new Runnable() {
            @Override
            public void run() {
                final Bitmap bm = ImageUtils.GetLocalOrNetBitmap(picUrlList.get(position));
                Bitmap bb = null;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                    bb = ImageUtils.blur(context,bm);
                }else {
                    bb = ImageUtils.newBlur(bm, photoDraweeView);
                }
                final Bitmap tempBm = bb;
                ((Activity)(context)).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        photoDraweeView.setImageBitmap(tempBm);
                    }
                });
            }
        });
        try {
            viewGroup.addView(photoDraweeView, ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT);
            photoDraweeView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.e("TT","PhotoDraweeView,onclick()");
                    if(clickListener!=null){
                        clickListener.onSingleTap();
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }

        return photoDraweeView;
    }



    /**
     * @param target 需要处理的bitmap
     * @return 正确比例的bitmap
     */
    public Bitmap zoomBitmap(Bitmap target) {
        if (target == null) return null;
        int width = target.getWidth();
        int height = target.getHeight();
        Matrix matrix = new Matrix();
        float scaleWidth = getX() * 1.0f / width;
        float scaleHeight = getY() * 1.0f / height;
        if (scaleWidth <= 0 || scaleHeight <= 0) {
            return null;
        }
        matrix.postScale(scaleWidth, scaleHeight);
        Bitmap bitmap = Bitmap.createBitmap(target, 0, 0, width, height, matrix, true);
        target.recycle();
        return bitmap;

    }
    /**
     * @return 屏幕的宽度
     */
    private int getX() {
//            Display screen = getWindowManager().getDefaultDisplay();
//            Point size = new Point();
//            screen.getSize(size);
//            return size.x;
        return ViewUtils.getScreenWidth(context);
    }

    /**
     * @return 屏幕的高度
     */
    private int getY() {
//            Display screen = getWindowManager().getDefaultDisplay();
//            Point size = new Point();
//            screen.getSize(size);
//            return size.y;
        return ViewUtils.getScreenHeight(context);
    }

}