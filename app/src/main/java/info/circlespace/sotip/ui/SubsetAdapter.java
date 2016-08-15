/*
 * Copyright (C) 2016 Dylan Chu
 */
package info.circlespace.sotip.ui;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import info.circlespace.sotip.R;

/**
 * An adapter for the grid showing the number of projects for each subset.
 */
public class SubsetAdapter extends RecyclerView.Adapter<SubsetAdapter.VwHldr> {

    public static final String LOG_TAG = SubsetAdapter.class.getSimpleName();
    public static final String SELECTED_NDX = "selectedNx";

    private Context mCtx;
    private List<SubsetData> mDataSet;
    private int mSelectedItemNdx;
    private SubsetAdapter.ItemClickHandler mClickHdlr;
    private ViewGroup mClickedContainer;

    public class VwHldr extends RecyclerView.ViewHolder implements View.OnClickListener {
        public ViewGroup container;
        public TextView label;
        public TextView count;

        public VwHldr(LayoutInflater inflater, ViewGroup parent) {
            super(inflater.inflate(R.layout.list_item_subset, parent, false));

            container = (ViewGroup) itemView.findViewById(R.id.subset_container);
            label = (TextView) itemView.findViewById(R.id.label);
            count = (TextView) itemView.findViewById(R.id.count);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            mSelectedItemNdx = getAdapterPosition();
            if (mSelectedItemNdx < 0) {
                return;
            }

            // change the background of the previously clicked item back to normal
            if (mClickedContainer != null) {
                mClickedContainer.setBackgroundDrawable(mCtx.getResources().getDrawable(R.drawable.primary_box));
            }

            // highlight the currently clicked item
            container.setBackgroundDrawable(mCtx.getResources().getDrawable(R.drawable.primary_light_box));
            mClickedContainer = container;

            SubsetData data = mDataSet.get(mSelectedItemNdx);

            // trigger the event on the view holding this adapter
            mClickHdlr.onClick(data, this);
        }

    }


    /**
     * This interface allows the adapter to pass item click events to the view containing it.
     */
    public static interface ItemClickHandler {
        void onClick(SubsetData data, VwHldr vh);
    }


    public SubsetAdapter(Context context, SubsetAdapter.ItemClickHandler hdlr) {
        mCtx = context;
        mClickHdlr = hdlr;
        mDataSet = new ArrayList<SubsetData>();
    }


    @Override
    public VwHldr onCreateViewHolder(ViewGroup parent, int viewType) {
        return new VwHldr(LayoutInflater.from(parent.getContext()), parent);
    }


    @Override
    public void onBindViewHolder(VwHldr holder, int position) {
        SubsetData subset = mDataSet.get(position);

        if (mSelectedItemNdx == position) {
            holder.container.setBackgroundDrawable(mCtx.getResources().getDrawable(R.drawable.primary_light_box));
            mClickedContainer = holder.container;
        } else {
            holder.container.setBackgroundDrawable(mCtx.getResources().getDrawable(R.drawable.primary_box));
        }
        holder.label.setText(subset.getLabel());
        holder.count.setText(Integer.toString(subset.getCount()));
    }


    @Override
    public int getItemCount() {
        if (mDataSet == null) return 0;
        return mDataSet.size();
    }


    public void setDataSet(List<SubsetData> data) {
        mDataSet = data;
        notifyDataSetChanged();
    }


    public List<SubsetData> getDataSet() {
        return mDataSet;
    }


    public void selectView(RecyclerView.ViewHolder viewHolder) {
        if (viewHolder instanceof VwHldr) {
            VwHldr vfh = (VwHldr) viewHolder;
            vfh.onClick(vfh.itemView);
        }
    }


    public void setSelectedNdx(int ndx) {
        mSelectedItemNdx = ndx;
    }


    public int getSelectViewNdx() {
        return mSelectedItemNdx;
    }


    public void onSaveInstanceState(Bundle out) {
        // saves the adapter position of the currently selected item
        out.putInt(SELECTED_NDX, mSelectedItemNdx);
    }


    public void onRestoreInstanceState(Bundle in) {
        // restores the adapter position of the previously selected item
        mSelectedItemNdx = in.getInt(SELECTED_NDX);
    }

}
