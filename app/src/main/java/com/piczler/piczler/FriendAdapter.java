package com.piczler.piczler;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.viethoa.RecyclerViewFastScroller;

import java.util.ArrayList;

/**
 * Created by pk on 4/22/15.
 */
public class FriendAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>   implements RecyclerViewFastScroller.BubbleTextGetter {
    private ArrayList<GettersAndSetters> mItemList;
    Context _c;

    public FriendAdapter(ArrayList<GettersAndSetters> itemList, Context c) {
        mItemList = itemList;
        this._c = c;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.friends_item, parent, false);
        return FriendViewHolder.newInstance(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
        FriendViewHolder holder = (FriendViewHolder) viewHolder;
        GettersAndSetters Details=mItemList.get(position);
        holder.setItemText(Details,_c,position);
    }

    @Override
    public int getItemCount() {
        return mItemList == null ? 0 : mItemList.size();
    }



    @Override
    public String getTextToShowInBubble(int pos) {
        if (pos < 0 || pos >= mItemList.size())
            return null;

        String name = mItemList.get(pos).name;
        if (name == null || name.length() < 1)
            return null;

        return mItemList.get(pos).name.substring(0, 1);
    }

}
