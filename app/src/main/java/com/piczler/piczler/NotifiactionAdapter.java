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
public class NotifiactionAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private ArrayList<GettersAndSetters> mItemList;
    Context _c;

    public NotifiactionAdapter(ArrayList<GettersAndSetters> itemList, Context c) {
        mItemList = itemList;
        this._c = c;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.notification_item, parent, false);
        return NotificationViewHolder.newInstance(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
        NotificationViewHolder holder = (NotificationViewHolder) viewHolder;
        GettersAndSetters Details=mItemList.get(position);
        holder.setItemText(Details,_c,position);
    }

    @Override
    public int getItemCount() {
        return mItemList == null ? 0 : mItemList.size();
    }



}
