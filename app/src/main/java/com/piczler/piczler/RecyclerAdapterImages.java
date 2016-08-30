package com.piczler.piczler;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

/**
 * Created by pk on 4/22/15.
 */
public class RecyclerAdapterImages extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private ArrayList<GettersAndSetters> mItemList;
    private Context context;

    public RecyclerAdapterImages(ArrayList<GettersAndSetters> itemList, Context context) {
        mItemList = itemList;
        this.context = context;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.image_filter_item, parent, false);
        return RecyclerItemViewHolderImages.newInstance(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
        RecyclerItemViewHolderImages holder = (RecyclerItemViewHolderImages) viewHolder;
        GettersAndSetters itemText = mItemList.get(position);
        holder.setItemText(itemText,position,context);
    }

    @Override
    public int getItemCount() {
        return mItemList == null ? 0 : mItemList.size();
    }



}
