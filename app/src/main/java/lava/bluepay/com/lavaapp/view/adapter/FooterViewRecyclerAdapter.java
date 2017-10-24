package lava.bluepay.com.lavaapp.view.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import java.util.ArrayList;
import java.util.List;

import lava.bluepay.com.lavaapp.R;
import lava.bluepay.com.lavaapp.common.Logger;

/**
 * Recyclerview的底部View的adapter
 */

public class FooterViewRecyclerAdapter extends RecyclerView.Adapter {

    private RecyclerView.Adapter adapter;
    private List<View> mfooterViews;

    public FooterViewRecyclerAdapter(RecyclerView.Adapter adapter, List<View> footerViews) {
        this.adapter = adapter;
        if(footerViews == null){
            mfooterViews = new ArrayList<>();
        }else {
            mfooterViews = footerViews;
        }

    }

    @Override
    public int getItemViewType(int position) {
        int adapterCount = 0;
        //正常条目
        if(adapter !=null){
            adapterCount = adapter.getItemCount();
            if(position < adapterCount){
                return adapter.getItemViewType(position);
            }
        }
        //尾部条目
        return RecyclerView.INVALID_TYPE;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if(viewType == RecyclerView.INVALID_TYPE){
            return new FooterViewHolder(mfooterViews.get(0));
        }
        return adapter.onCreateViewHolder(parent,viewType);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        int  adapterCount = 0;
        //正常条目
        if(adapter!=null){
            adapterCount = adapter.getItemCount();
            if(position < adapterCount){
                adapter.onBindViewHolder(holder,position);
                return;
            }
        }
        //底部条目
        ((FooterViewRecyclerAdapter.FooterViewHolder)holder).lastPage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Logger.e(Logger.DEBUG_TAG,"asdfasdf");
            }
        });

    }

    @Override
    public int getItemCount() {
        if(adapter!=null){
            return adapter.getItemCount()+mfooterViews.size();
        }
        return mfooterViews.size();
    }

    private static class FooterViewHolder extends RecyclerView.ViewHolder{

        Button lastPage;
        Button nextPage;

        public FooterViewHolder(View itemView) {
            super(itemView);
            lastPage = (Button) itemView.findViewById(R.id.btn_last_page);
            nextPage = (Button) itemView.findViewById(R.id.btn_next_page);
        }

    }
}
