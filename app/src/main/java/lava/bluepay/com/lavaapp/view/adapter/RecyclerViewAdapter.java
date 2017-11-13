package lava.bluepay.com.lavaapp.view.adapter;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import com.facebook.drawee.view.SimpleDraweeView;
import java.io.File;
import java.util.List;
import lava.bluepay.com.lavaapp.Config;
import lava.bluepay.com.lavaapp.R;
import lava.bluepay.com.lavaapp.common.FileUtils;
import lava.bluepay.com.lavaapp.common.ImageUtils;
import lava.bluepay.com.lavaapp.common.Logger;
import lava.bluepay.com.lavaapp.common.ThreadManager;
import lava.bluepay.com.lavaapp.common.fresco.FrescoHelper;
import lava.bluepay.com.lavaapp.model.MemExchange;
import lava.bluepay.com.lavaapp.model.api.bean.CategoryBean;
import lava.bluepay.com.lavaapp.model.api.bean.CheckSubBean;

/**
 * Created by bluepay on 2017/10/14.
 */

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements View.OnClickListener{

    private Context context;
    private LayoutInflater mInflater;
    private List<CategoryBean.DataBeanX.DataBean> mDatas;
    private List<Integer> mHeights;//item的随机params

    private OnItemClickListener itemClickListener;

    public OnItemClickListener getItemClickListener() {
        return itemClickListener;
    }

    public void setItemClickListener(OnItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    public List<CategoryBean.DataBeanX.DataBean> getmDatas() {
        return mDatas;
    }

    /**
     * 默认设置为缓存中的数据
     *
     * @param context
     * @param categoryId 对应类别id（） 枚举例如:Config.CategoryPhotoPopular
     */
    public RecyclerViewAdapter(Context context,int categoryId) {
        this.context = context;
        mInflater = LayoutInflater.from(context);
        switch (categoryId) {
            //图片
            case Config.CategoryPhotoPopular:
                mDatas = MemExchange.getInstance().getPhotoPopularList();
                mHeights = MemExchange.getInstance().getPopularHeights();
                break;
            case Config.CategoryPhotoPortray:
                mDatas = MemExchange.getInstance().getPhotoPortrayList();
                mHeights = MemExchange.getInstance().getPhotoPortrayHeights();
                break;
            case Config.CategoryPhotoScenery:
                mDatas = MemExchange.getInstance().getPhotoSceneryList();
                mHeights = MemExchange.getInstance().getPhotoSceneryHeights();
                break;

            //视屏
            case Config.CategoryVideoPopular:
                mDatas = MemExchange.getInstance().getVideoPopularList();
                mHeights = MemExchange.getInstance().getVideoPopularHeights();
                break;
            case Config.CategoryVideoFunny:
                mDatas = MemExchange.getInstance().getVideoFunnyList();
                mHeights = MemExchange.getInstance().getVideoFunnyHeights();
                break;
            case Config.CategoryVideoSport:
                mDatas = MemExchange.getInstance().getVideoSportList();
                mHeights = MemExchange.getInstance().getVideoSportHeights();
                break;


            //卡通
            case Config.CategoryCartoonPopular:

                mDatas = MemExchange.getInstance().getCartoonPopularList();
                mHeights = MemExchange.getInstance().getCartoonPopularHeights();

                break;
            case Config.CategoryCartoonFunny:
                mDatas = MemExchange.getInstance().getCartoonFunnyList();
                mHeights = MemExchange.getInstance().getCartoonFunnyHeights();
                break;
            case Config.CategoryCartoonhorror:
                mDatas = MemExchange.getInstance().getCartoonHorrorList();
                mHeights = MemExchange.getInstance().getCartoonHorrorHeights();
                break;

        }
    }

    public void setmDatas(List<CategoryBean.DataBeanX.DataBean> mDatas, List<Integer> mHeights) {
        if (mDatas == null || mDatas.size() == 0 || mHeights == null || mHeights.size() == 0) {
            Logger.e(Logger.DEBUG_TAG, "RecyclerViewAdapter,setmDatas(),error");
            return;
        }
        this.mDatas = mDatas;
        this.mHeights = mHeights;
        notifyDataSetChanged();
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = mInflater.inflate(R.layout.activity_recyclerview_gridv_item, parent, false);
        view.setOnClickListener(this);
        MyViewHolder myViewHolder = new MyViewHolder(view);

        myViewHolder.setListener(getItemClickListener());
        return myViewHolder;
    }


    @Override
    public void onClick(View v) {
        if (getItemClickListener() != null) {
            if (v.getTag() != null) {
                getItemClickListener().onItemClick(v, (int) v.getTag());
            }
        }
    }


    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {

        holder.itemView.setTag(position);

        if (mDatas == null ||  mDatas.size() == 0) {
            Logger.e(Logger.DEBUG_TAG, "RecyclerViewAdapter,onBindViewHolder(),mDatas error");
            return;
        }
//        ((MyViewHolder) holder).cardview_item.setMaxCardElevation(40);

        final CategoryBean.DataBeanX.DataBean data = mDatas.get(position);


        ViewGroup.LayoutParams lp = ((MyViewHolder) holder).imageView.getLayoutParams();
        lp.height = mHeights.get(position);

        ((MyViewHolder) holder).imageView.setLayoutParams(lp);
        if (data == null ||  TextUtils.isEmpty(data.getThumb())) {//默认
            ((MyViewHolder)holder).imageView.setScaleType(ImageView.ScaleType.FIT_XY);
            ((MyViewHolder)holder).imageView.setImageResource(R.drawable.pic_loading);
        } else {

            if(CheckSubBean.ifHaveSubscribe(MemExchange.m_iIMSI)) {
                //订阅用户、正常显示
                ((MyViewHolder)holder).imageView.setImageURI(Uri.parse(data.getThumb()));
            }else {
                //非订阅用户

                //todo 图片的url一定要统一
                if (data.getThumb().lastIndexOf(File.separator) == -1 || data.getThumb().lastIndexOf(FileUtils.FILE_EXTENSION_SEPARATOR) == -1) {
                    Logger.e(Logger.DEBUG_TAG, "pic url error");
                    ((MyViewHolder)holder).imageView.setImageURI(Uri.parse(data.getThumb()));
                    return;
                }

                //原图路径
                final String localFilePath = Config.PHOTO_PATH + data.getThumb().substring(data.getThumb().lastIndexOf(File.separator));//绝对路径
                File localFile = new File(localFilePath);
                //模糊图片路径
                String localBufPath = localFilePath + Config.bufFileEnd;
                File localBufFile = new File(localBufPath);

                if (localBufFile.exists()) {
                    Logger.i(Logger.DEBUG_TAG, "模糊图片存在,pos" + position);
                    //todo 将图片File转为bitmap，bitmap大小拉升至控件的大小
                    Bitmap blur;
                    Uri uri = Uri.parse(localBufPath);
                    blur = BitmapFactory.decodeFile(uri.toString());
//                    float scaleWidth = imageWidth * 1.0f / temp.getWidth();
//                    float scaleWidth = 1;
//                    float scaleHeight = imageHeight * 1.0f /temp.getHeight();
//                    if(scaleWidth <=0 || scaleHeight <=0){
//                        Logger.e(Logger.DEBUG_TAG,"RecyclerViewAdapter,scaleWidth <=0 || scaleHeight <=0"+imageWidth+","+imageHeight+","+temp.getWidth()+","+temp.getHeight());
//                        return;
//                    }
//                    Matrix matrix = new Matrix();
//                    matrix.postScale(scaleWidth,scaleHeight);
//                    Bitmap blur = Bitmap.createBitmap(temp, 0, 0, temp.getWidth(), temp.getHeight(), matrix, true);

                    setImageBlur(blur, ((MyViewHolder) holder).imageView);
                    return;
                }

                if (localFile.exists()) {
                    Logger.i(Logger.DEBUG_TAG, "原图片存在,模糊图片不存在,pos" + position);
                    //模糊处理得到相同大小的bitmap
                    final Bitmap blur;
                    Uri uri = Uri.parse(localFilePath);
                    Bitmap bitmap = BitmapFactory.decodeFile(uri.toString());

//                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                        blur = ImageUtils.blur(context, bitmap);
//                    }else{
//                    blur = ImageUtils.newBlur(bitmap, ((MyViewHolder) holder).imageView);
//                    }

                    setImageBlur(blur, ((MyViewHolder) holder).imageView);
                    ImageUtils.saveBitmap2File(blur, localBufPath);
                } else {
                    Logger.i(Logger.DEBUG_TAG, "两图均不存在,pos" + position);
                    ThreadManager.executeOnSubThread1(new Runnable() {
                        @Override
                        public void run() {
                            //保存原图到本地
                            FrescoHelper.downPic(context, data.getThumb(), localFilePath, new OnBitmapDownloadListener() {
                                @Override
                                public void onDownloadFinish(final boolean isSuccess) {
                                    ((Activity) context).runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            if (isSuccess) {
                                                notifyItemChanged(position);
                                            }
                                        }
                                    });

                                }

                            });
                        }
                    });

                }
            }
            //模糊处理
//                ThreadManager.executeOnSubThread1(new Runnable() {
//                    @Override
//                    public void run() {
//                        final Bitmap bm = ImageUtils.GetLocalOrNetBitmap(data.getPictureImg());
//                        Bitmap bb = null;
//                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
//                            bb = ImageUtils.blur(context,bm,holder.imageView.getWidth(),holder.imageView.getHeight());
//                        }else {
//                            bb = ImageUtils.newBlurToViewSize(bm, holder.imageView);
//                        }
//                        final Bitmap tempBm = bb;
//                        ((Activity)(context)).runOnUiThread(new Runnable() {
//                            @Override
//                            public void run() {
//                                holder.imageView.setImageBitmap(tempBm);
//                            }
//                        });
//                    }
//                });


        }
    }

    private void setImageBlur(final Bitmap bitmap, final SimpleDraweeView view) {
        ThreadManager.getMainHandler().post(new Runnable() {
            @Override
            public void run() {
                if (bitmap != null) {
                    view.setScaleType(ImageView.ScaleType.FIT_XY);
                    view.setImageBitmap(bitmap);
                } else {
                    Logger.e("TT", "RecyclerViewAdapter,setImageBlur(),bitmap == null");
                }

            }
        });
    }


    @Override
    public int getItemCount() {
        if (mDatas == null || mDatas.size() == 0) {
            return 0;
        }
        return mDatas.size() ;
    }

    class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        SimpleDraweeView imageView;
        CardView cardview_item;

        OnItemClickListener listener;

        public MyViewHolder(View itemView) {
            super(itemView);
            cardview_item = (CardView) itemView.findViewById(R.id.cardview_item);
            imageView = (SimpleDraweeView) itemView.findViewById(R.id.iv_item);
        }
        public void setListener(OnItemClickListener listener){
            this.listener = listener;
        }

        @Override
        public void onClick(View v) {
            if(listener!=null){
                listener.onItemClick(v,getPosition());
            }
        }
    }


    /**
     * 点击事件
     */
    public interface OnItemClickListener {
        void onItemClick(View view, int position);

        void onItemLongClick(View view, int position);
    }

    /**
     * 图片下载完成的回调
     */
    public interface OnBitmapDownloadListener {
        void onDownloadFinish(boolean isSuccess);
    }

}
