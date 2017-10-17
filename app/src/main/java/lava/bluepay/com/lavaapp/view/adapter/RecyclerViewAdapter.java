package lava.bluepay.com.lavaapp.view.adapter;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import lava.bluepay.com.lavaapp.view.bean.PhotoBean;

/**
 * Created by bluepay on 2017/10/14.
 */

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.MyViewHolder> {

    private Context context;
    private List<PhotoBean> mDatas;
    private List<Integer> mHeights;//item的随机params

    private OnItemClickListener itemClickListener;

    public OnItemClickListener getItemClickListener() {
        return itemClickListener;
    }
    public void setItemClickListener(OnItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }



    public RecyclerViewAdapter(Context context) {
        this.context = context;
    }

    public RecyclerViewAdapter(Context context, List<PhotoBean> mDatas, List<Integer> mHeights) {
        this.context = context;
        this.mDatas = mDatas;
        this.mHeights = mHeights;
    }

    public void setmDatas(List<PhotoBean> mDatas,List<Integer> mHeights) {
        this.mDatas = mDatas;
        this.mHeights = mHeights;
        notifyDataSetChanged();
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        MyViewHolder myViewHolder = new MyViewHolder(LayoutInflater.from(context).inflate(R.layout.activity_recyclerview_gridv_item,parent,false));
        return myViewHolder;
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
            final PhotoBean data = mDatas.get(position);
            if(data == null || TextUtils.isEmpty(data.getPictureImg())){
                return;
            }
            //todo 图片的url一定要统一
            if(data.getPictureImg().lastIndexOf(File.separator) == -1 || data.getPictureImg().lastIndexOf(FileUtils.FILE_EXTENSION_SEPARATOR) == -1){
                Logger.e(Logger.DEBUG_TAG,"pic url error");
                return;
            }

            ViewGroup.LayoutParams lp = holder.imageView.getLayoutParams();
            lp.height = mHeights.get(position);
            holder.imageView.setLayoutParams(lp);
            if (TextUtils.isEmpty(data.getPictureImg())) {//默认
                holder.imageView.setBackgroundColor(context.getResources().getColor(android.R.color.holo_red_light));
            } else {

                //订阅用户、正常显示
//                holder.imageView.setImageURI(Uri.parse(data.getPictureImg()));

                //非订阅用户


                //原图路径
                String fileName = data.getPictureImg().substring(data.getPictureImg().lastIndexOf(File.separator));
                final String localFilePath = Config.PHOTO_PATH + fileName;//绝对路径
                File localFile = new File(localFilePath);


                if(localFile.exists()){//原图本地路径
                    //本地加载bitmap
                    final Bitmap blur;
                    Uri uri = Uri.parse(localFilePath);
                    Bitmap bitmap = BitmapFactory.decodeFile(uri.toString());
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                        blur = ImageUtils.blur(context, bitmap);
                    }else{
                        blur = ImageUtils.newBlurToViewSize(bitmap,holder.imageView);
                    }
                    setImageBlur(blur,holder.imageView);

                    //本地加载File绝对路径
//                    holder.imageView.setImageURI(Uri.fromFile(localFile));
                }else{

                    //保存缓存图片
                    ThreadManager.executeOnSubThread1(new Runnable() {
                        @Override
                        public void run() {
                            FrescoHelper.saveImage2Local(context,data.getPictureImg(),localFilePath,new OnBitmapDownloadListener(){

                                @Override
                                public void onDownloadFinish(final boolean isSuccess) {
                                    ((Activity)context).runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            if(isSuccess){
                                                notifyItemChanged(position);
                                            }
                                        }
                                    });

                                }

                            });
                        }
                    });

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


//
            if(getItemClickListener()!=null){
                holder.imageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        getItemClickListener().onItemClick(holder.imageView,position);
                    }
                });
                holder.imageView.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View view) {
                        return false;
                    }
                });
            }
    }

    private void setImageBlur(final Bitmap bitmap, final SimpleDraweeView view){
        ThreadManager.getMainHandler().post(new Runnable() {
            @Override
            public void run() {
                if(bitmap!=null){
                    view.setImageBitmap(bitmap);
                }else{
                   Logger.e("TT","RecyclerViewAdapter,setImageBlur(),bitmap == null");
                }

            }
        });
    }


    @Override
    public int getItemCount() {
        if(mDatas == null || mDatas.size() == 0) {
            return 0;
        }
        return mDatas.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder{
                SimpleDraweeView imageView;
        public MyViewHolder(View itemView) {
            super(itemView);
            imageView = (SimpleDraweeView) itemView.findViewById(R.id.iv_item);
        }

    }
    /**
     * 点击事件
     */
    public interface OnItemClickListener{
        void onItemClick(View view, int position);
        void onItemLongClick(View view, int position);
    }
    public interface OnBitmapDownloadListener{
        void onDownloadFinish(boolean isSuccess);
    }

}
