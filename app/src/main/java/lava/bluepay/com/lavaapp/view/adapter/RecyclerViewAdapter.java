package lava.bluepay.com.lavaapp.view.adapter;

import android.content.Context;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.facebook.drawee.view.SimpleDraweeView;
import java.util.List;
import lava.bluepay.com.lavaapp.R;
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
            PhotoBean data = mDatas.get(position);
            if(data == null){
                return;
            }
            ViewGroup.LayoutParams lp = holder.imageView.getLayoutParams();
            lp.height = mHeights.get(position);
            holder.imageView.setLayoutParams(lp);
            if (TextUtils.isEmpty(data.getPictureImg())) {//默认
                holder.imageView.setBackgroundColor(context.getResources().getColor(android.R.color.holo_red_light));
            } else {
                holder.imageView.setImageURI(Uri.parse(data.getPictureImg()));
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
//            holder.tv.setText(data);
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

}
