package lava.bluepay.com.lavaapp.view.adapter;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

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

/**
 * Created by bluepay on 2017/10/14.
 */

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context context;
    private LayoutInflater mInflater;
    private CategoryBean mDatas;
    private List<Integer> mHeights;//item的随机params

    private OnItemClickListener itemClickListener;

    public OnItemClickListener getItemClickListener() {
        return itemClickListener;
    }
    public void setItemClickListener(OnItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    //region===================recycler的底部view=====================

    // 基本的底部类型开始位置  用于viewType
    private static int BASE_ITEM_TYPE_FOOTER = 20000000;

    private SparseArray<View> mFooterViews;

    public SparseArray<View> getmFooterViews() {
        if(mFooterViews == null){
            mFooterViews = new SparseArray<>();
        }
        return mFooterViews;
    }

    /** 是不是底部类型    */
    private boolean isFooterViewType(int viewType){
        int position = mFooterViews.indexOfKey(viewType);
        return position >= 0;
    }

    private RecyclerView.ViewHolder createFooterViewHolder(View view){
        RecyclerView.ViewHolder holder = new FooterViewHolder(view);
        return holder;
    }

    class FooterViewHolder extends RecyclerView.ViewHolder{
        Button lastPage;
        Button nextPage;
        public FooterViewHolder(View itemView) {
            super(itemView);
            lastPage = (Button) itemView.findViewById(R.id.btn_last_page);
            nextPage = (Button) itemView.findViewById(R.id.btn_next_page);
        }

    }

    // 添加底部
    public void addFooterView(View view) {
        int position = mFooterViews.indexOfValue(view);
        if (position < 0) {
            mFooterViews.put(BASE_ITEM_TYPE_FOOTER++, view);
        }
//        notifyDataSetChanged();
    }

    private boolean isFooterPosition(int pos){
        if(mDatas == null || mDatas.getData() == null || mDatas.getData().getData() == null || mDatas.getData().getData().size() == 0){
            Logger.e(Logger.DEBUG_TAG,"isFooterPosition,aaaa"+pos);
            return true;
        }
        Logger.e(Logger.DEBUG_TAG,"isFooterPosition,bbb"+pos);
        return pos >= mDatas.getData().getData().size();
    }

    //endregion===================recycler的底部view=====================


    /**
     * 默认设置为缓存中的数据
     * @param context
     */
    public RecyclerViewAdapter(Context context) {
        this.context = context;
        mInflater = LayoutInflater.from(context);
        mDatas = MemExchange.getInstance().getPhotoPopularList();
        mHeights = MemExchange.getInstance().getPopularHeights();

        mFooterViews = new SparseArray<>();
    }

    public void setmDatas(CategoryBean mDatas,List<Integer> mHeights) {
        if(mDatas == null || mDatas.getData().getData().size() == 0 || mHeights == null || mHeights.size() == 0){
            Logger.e(Logger.DEBUG_TAG,"RecyclerViewAdapter,setmDatas(),error");
            return;
        }
        this.mDatas = mDatas;
        this.mHeights = mHeights;
        notifyDataSetChanged();
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if(isFooterViewType(viewType)){
            View footer = mFooterViews.get(viewType);
            return createFooterViewHolder(footer);
        }
        RecyclerView.ViewHolder myViewHolder = new MyViewHolder(mInflater.inflate(R.layout.activity_recyclerview_gridv_item,parent,false));
        return myViewHolder;
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {

            if(isFooterPosition(position)){
                ((FooterViewHolder)holder).nextPage.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                    }
                });
                ((FooterViewHolder)holder).lastPage.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                    }
                });
                return;
            }

            if(mDatas == null || mDatas.getData() == null || mDatas.getData().getData() == null || mDatas.getData().getData().size() == 0){
                Logger.e(Logger.DEBUG_TAG,"RecyclerViewAdapter,onBindViewHolder(),mDatas error");
                return;
            }

            final CategoryBean.DataBeanX.DataBean data = mDatas.getData().getData().get(position);
            if(data == null || TextUtils.isEmpty(data.getThumb())){
                return;
            }
            //todo 测试
            data.setThumb("http://photocdn.sohu.com/20121119/Img358016160.jpg");

            //todo 图片的url一定要统一
            if(data.getThumb().lastIndexOf(File.separator) == -1 || data.getThumb().lastIndexOf(FileUtils.FILE_EXTENSION_SEPARATOR) == -1){
                Logger.e(Logger.DEBUG_TAG,"pic url error");
                return;
            }

            ViewGroup.LayoutParams lp = ((MyViewHolder)holder).imageView.getLayoutParams();
            lp.height = mHeights.get(position);

//            int imageHeight = lp.height;

            ((MyViewHolder)holder).imageView.setLayoutParams(lp);
            if (TextUtils.isEmpty(data.getThumb())) {//默认
                ((MyViewHolder)holder).imageView.setBackgroundColor(context.getResources().getColor(android.R.color.holo_red_light));
            } else {

                //订阅用户、正常显示
//                holder.imageView.setImageURI(Uri.parse(data.getPictureImg()));

                //非订阅用户


                //原图路径
                final String localFilePath = Config.PHOTO_PATH + data.getThumb().substring(data.getThumb().lastIndexOf(File.separator));//绝对路径
                File localFile = new File(localFilePath);
                //模糊图片路径
                String localBufPath = localFilePath + Config.bufFileEnd;
                File localBufFile = new File(localBufPath);

                if(localBufFile.exists()){
                    Logger.i(Logger.DEBUG_TAG,"模糊图片存在,pos"+position);
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

                    setImageBlur(blur,((MyViewHolder)holder).imageView);
                    return;
                }

                if(localFile.exists()){
                    Logger.i(Logger.DEBUG_TAG,"原图片存在,模糊图片不存在,pos"+position);
                    //模糊处理得到相同大小的bitmap
                    final Bitmap blur;
                    Uri uri = Uri.parse(localFilePath);
                    Bitmap bitmap = BitmapFactory.decodeFile(uri.toString());

//                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
//                        blur = ImageUtils.blur(context, bitmap);
//                    }else{
                        blur = ImageUtils.newBlur(bitmap,((MyViewHolder)holder).imageView);
//                    }

                    setImageBlur(blur,((MyViewHolder)holder).imageView);
                    ImageUtils.saveBitmap2File(blur,localBufPath);
                }else{
                    Logger.i(Logger.DEBUG_TAG,"两图均不存在,pos"+position);
                    ThreadManager.executeOnSubThread1(new Runnable() {
                        @Override
                        public void run() {
                            //保存原图到本地
                            FrescoHelper.downPic(context,data.getThumb(),localFilePath,new OnBitmapDownloadListener(){
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
                ((MyViewHolder)holder).imageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Logger.e(Logger.DEBUG_TAG,"22222222222222222222222");
                        getItemClickListener().onItemClick(((MyViewHolder)holder).imageView,position);
                    }
                });

                ((MyViewHolder)holder).imageView.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View view) {
                        return false;
                    }
                });


                ((MyViewHolder)holder).cardview_item.setClickable(true);
                ((MyViewHolder)holder).cardview_item.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Logger.e(Logger.DEBUG_TAG,"11111111111111111111111");
                        getItemClickListener().onItemClick(((MyViewHolder)holder).imageView,position);
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
        if(mDatas == null || mDatas.getData() == null || mDatas.getData().getData() == null || mDatas.getData().getData().size() == 0) {
            return getmFooterViews().size();
        }
        return mDatas.getData().getData().size()+getmFooterViews().size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder{
        SimpleDraweeView imageView;
        CardView cardview_item;
        public MyViewHolder(View itemView) {
            super(itemView);
            cardview_item = (CardView) itemView.findViewById(R.id.cardview_item);
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
