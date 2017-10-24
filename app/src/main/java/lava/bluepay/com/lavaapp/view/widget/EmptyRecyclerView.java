package lava.bluepay.com.lavaapp.view.widget;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.View;
import java.util.ArrayList;
import java.util.List;

import lava.bluepay.com.lavaapp.view.adapter.FooterViewRecyclerAdapter;

/**
 * Created by yuanqiang on 2017/5/10.
 */

public class EmptyRecyclerView extends RecyclerView {

    private List<View> mFootViewInfos = new ArrayList<>();

    private View emptyView;
    private Adapter mAdapter;

    public EmptyRecyclerView(Context context) {
        super(context);
    }

    public EmptyRecyclerView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public EmptyRecyclerView(Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }
    private void checkIfEmpty(){
        Adapter adapter = getAdapter();
        if(adapter == null|| emptyView==null){
            return;
        }
        if(adapter.getItemCount() == 0){
            emptyView.setVisibility(View.VISIBLE);
            EmptyRecyclerView.this.setVisibility(View.GONE);
        }else{
            emptyView.setVisibility(View.GONE);
            EmptyRecyclerView.this.setVisibility(View.VISIBLE);
        }
    }

    private final AdapterDataObserver observer = new AdapterDataObserver() {
        @Override
        public void onChanged() {
            checkIfEmpty();
        }

        @Override
        public void onItemRangeChanged(int positionStart, int itemCount) {
            onChanged();
        }

        @Override
        public void onItemRangeChanged(int positionStart, int itemCount, Object payload) {
            onChanged();
        }

        @Override
        public void onItemRangeInserted(int positionStart, int itemCount) {
            onChanged();
        }

        @Override
        public void onItemRangeRemoved(int positionStart, int itemCount) {
            onChanged();
        }

        @Override
        public void onItemRangeMoved(int fromPosition, int toPosition, int itemCount) {
            onChanged();
        }
    };

    public void setEmptyView(View emptyView) {
        this.emptyView = emptyView;
        checkIfEmpty();
    }

    public void addFootView(View v){
        mFootViewInfos.add(v);
        if(mAdapter!=null){
            if(!(mAdapter instanceof FooterViewRecyclerAdapter)){
                mAdapter = new FooterViewRecyclerAdapter(mAdapter,mFootViewInfos);
            }
        }
    }


    @Override
    public void setAdapter(Adapter adapter) {

        if(mFootViewInfos.size()>0){
            mAdapter = new FooterViewRecyclerAdapter(adapter,mFootViewInfos);
            super.setAdapter(mAdapter);
        }
        else {
            mAdapter = adapter ;
            super.setAdapter(mAdapter);

            final Adapter oldAdapter = getAdapter();
            if (oldAdapter != null) {
                oldAdapter.unregisterAdapterDataObserver(observer);
            }
            super.setAdapter(mAdapter);
            if (mAdapter != null) {
                mAdapter.registerAdapterDataObserver(observer);
            }
        }
    }
}
