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
public class RecyclerAdapterSearch extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private ArrayList<GettersAndSetters> mItemList;
    Context _c;

    public RecyclerAdapterSearch(ArrayList<GettersAndSetters> itemList, Context c) {
        mItemList = itemList;
        this._c = c;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        if(viewType == StaticVariables.TITLETYPE){
            final View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.search_title, parent, false);
            return SearchViewHolder.newInstance(view);
        }else
        {
            final View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.search_content, parent, false);
            return SearchViewHolder.newInstance(view);
        }

    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {

        GettersAndSetters Details=mItemList.get(position);

        SearchViewHolder holder2 = (SearchViewHolder) viewHolder;
                holder2.setItemText(Details,_c,position);





    }

    @Override
    public int getItemCount() {
        return mItemList == null ? 0 : mItemList.size();
    }



    @Override
    public int getItemViewType(int position) {
        return mItemList.get(position).layouttype;
    }


    private int getType(int position) {
        return mItemList.get(position).layouttype;
    }


}
