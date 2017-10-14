package lava.bluepay.com.lavaapp.view.adapter;

import android.content.Context;
import android.net.Uri;
import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;
import java.util.HashMap;
import java.util.List;

import lava.bluepay.com.lavaapp.R;
import lava.bluepay.com.lavaapp.view.widget.gallery.GalleryViewPager;
import lava.bluepay.com.lavaapp.view.widget.gallery.ZoomImageView;


public class GalleryPagerAdapter extends PagerAdapter {
    private Context mContext;
    /**
     * 展示的图片资源的URL列表
     */
    private List<String> imageUrl;
    /**
     * 存放展示图片的容器，用于删除不用的item
     */
    private HashMap<Integer, ZoomImageView> viewMap = new HashMap<>();

    public GalleryPagerAdapter(Context mContext, List<String> imageUrl){
        this.mContext = mContext;
        this.imageUrl = imageUrl;
    }

    @Override
    public int getCount() {
        return imageUrl.size();
    }

    @Override
    public void setPrimaryItem(ViewGroup container, int position, Object object) {
        super.setPrimaryItem(container, position, object);
        ZoomImageView zoomImage = viewMap.get(position);
        ((GalleryViewPager) container).setZoomView(zoomImage);
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        if(imageUrl == null || imageUrl.size()<=0){
            return null;
        }
        ZoomImageView zoomImage = new ZoomImageView(mContext);
        String pic_url = imageUrl.get(position);
        zoomImage.setImageURI(Uri.parse(pic_url));

        viewMap.put(position, zoomImage);
        container.addView(viewMap.get(position));
        return viewMap.get(position);
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        ZoomImageView zoomImage = viewMap.get(position);
        if (zoomImage != null) {
            zoomImage.setImageBitmap(null);
            viewMap.remove(position);
            container.removeView(zoomImage);
        }
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    public ZoomImageView getZoomImageByIndex(int index){
        return viewMap.get(index);
    }
}
