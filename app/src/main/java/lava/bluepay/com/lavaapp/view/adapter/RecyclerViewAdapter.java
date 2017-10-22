package lava.bluepay.com.lavaapp.view.adapter;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Build;
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
import lava.bluepay.com.lavaapp.view.bean.PhotoBean;

/**
 * Created by bluepay on 2017/10/14.
 */

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.MyViewHolder> {

    private Context context;
    private LayoutInflater mInflater;
    private List<PhotoBean> mDatas;
    private List<Integer> mHeights;//item的随机params

    private OnItemClickListener itemClickListener;

    public OnItemClickListener getItemClickListener() {
        return itemClickListener;
    }
    public void setItemClickListener(OnItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }


    /**
     * 默认设置为缓存中的数据
     * @param context
     */
    public RecyclerViewAdapter(Context context) {
        this.context = context;
        mInflater = LayoutInflater.from(context);
        mDatas = MemExchange.getInstance().getPhotoPopularList();
        mHeights = MemExchange.getInstance().getPopularHeights();
    }

    public void setmDatas(List<PhotoBean> mDatas,List<Integer> mHeights) {
        if(mDatas == null || mDatas.size() == 0 || mHeights == null || mHeights.size() == 0){
            Logger.e(Logger.DEBUG_TAG,"RecyclerViewAdapter,setmDatas(),error");
            return;
        }
        this.mDatas = mDatas;
        this.mHeights = mHeights;
        notifyDataSetChanged();
    }


    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        MyViewHolder myViewHolder = new MyViewHolder(mInflater.inflate(R.layout.activity_recyclerview_gridv_item,parent,false));
        return myViewHolder;
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
            if(mDatas == null || mDatas.size() == 0){
                Logger.e(Logger.DEBUG_TAG,"RecyclerViewAdapter,onBindViewHolder(),mDatas error");
                return;
            }
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

            int imageHeight = lp.height;

            holder.imageView.setLayoutParams(lp);
            if (TextUtils.isEmpty(data.getPictureImg())) {//默认
                holder.imageView.setBackgroundColor(context.getResources().getColor(android.R.color.holo_red_light));
            } else {

                //订阅用户、正常显示
//                holder.imageView.setImageURI(Uri.parse(data.getPictureImg()));

                //非订阅用户


                //文件名称可能出现错误的情况
                int tempSep = data.getPictureImg().lastIndexOf(File.separator);
                if(tempSep == -1){
                    Logger.e(Logger.DEBUG_TAG,"RecyclerViewAdapter,error");
                    return;
                }

                //原图路径
                final String localFilePath = Config.PHOTO_PATH + data.getPictureImg().substring(tempSep);//绝对路径
                File localFile = new File(localFilePath);
                //模糊图片路径
                String localBufPath = localFilePath + ".buf";
                File localBufFile = new File(localBufPath);

                //如果模糊图片存在
                if(localBufFile.exists()){
                    Logger.i(Logger.DEBUG_TAG,"模糊图片存在,pos"+position);
                    //todo 将图片File转为bitmap，bitmap大小拉升至控件的大小
                    Bitmap temp;
                    Uri uri = Uri.parse(localBufPath);
                    temp = BitmapFactory.decodeFile(uri.toString());
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
                    Bitmap blur = Bitmap.createScaledBitmap(temp,temp.getWidth(),imageHeight,true);
                    temp.recycle();
                    setImageBlur(blur,holder.imageView);

//                    setImageBlur(temp,holder.imageView);

                    return;
                }

                if(localFile.exists()){
                    Logger.i(Logger.DEBUG_TAG,"原图片存在,模糊图片不存在,pos"+position);
                    //模糊处理得到相同大小的bitmap
                    final Bitmap blur;
                    Uri uri = Uri.parse(localFilePath);
                    Bitmap bitmap = BitmapFactory.decodeFile(uri.toString());

//                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                        blur = ImageUtils.blur(context, bitmap);
//                    }else{
//                        blur = ImageUtils.newBlurToViewSize(bitmap,holder.imageView);
//                    }
                    //拉伸至控件大小并存储和设置

                    Bitmap bb = Bitmap.createScaledBitmap(blur,blur.getWidth(),imageHeight,true);

                    blur.recycle();

                    setImageBlur(bb,holder.imageView);
                    ImageUtils.saveBitmap2File(bb,localBufPath);
                    //本地加载File绝对路径
//                    holder.imageView.setImageURI(Uri.fromFile(localFile));
                }else{
                    Logger.i(Logger.DEBUG_TAG,"两图均不存在,pos"+position);
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
                    view.setScaleType(ImageView.ScaleType.FIT_XY);
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
