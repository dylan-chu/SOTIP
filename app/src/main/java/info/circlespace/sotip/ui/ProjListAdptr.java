/*
 * Copyright (C) 2016 Dylan Chu
 */
package info.circlespace.sotip.ui;

import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import info.circlespace.sotip.R;
import info.circlespace.sotip.SotipApp;
import info.circlespace.sotip.api.ProjectInfo;
import info.circlespace.sotip.data.SotipContract.ProjectEntry;


public class ProjListAdptr extends RecyclerView.Adapter<ProjListAdptr.VwHldr> {

    public static final String LOG_TAG = ProjListAdptr.class.getSimpleName();
    public static final String SELECTED_NDX = "selectedNx";

    public static final String[] DATA_COLUMNS = {
            ProjectEntry.TABLE_NAME + "." + ProjectEntry._ID,
            ProjectEntry.TABLE_NAME + "." + ProjectEntry.COL_AGC_CODE,
            ProjectEntry.TABLE_NAME + "." + ProjectEntry.COL_UNIQ_INVMT_ID,
            ProjectEntry.TABLE_NAME + "." + ProjectEntry.COL_INVMT_TITLE,
            ProjectEntry.TABLE_NAME + "." + ProjectEntry.COL_PROJ_NAME,
            ProjectEntry.TABLE_NAME + "." + ProjectEntry.COL_SCH_VAR,
            ProjectEntry.TABLE_NAME + "." + ProjectEntry.COL_COST_VAR
    };

    static final int COL_ITEM_ID = 0;
    static final int COL_AGC_CODE = 1;
    static final int COL_UNIQ_INVMT_ID = 2;
    static final int COL_INVMT_TITLE = 3;
    static final int COL_PROJ_NAME = 4;
    static final int COL_SCH_VAR = 5;
    static final int COL_COST_VAR = 6;

    public static final String SORT_HIERARCHY_ASC = ProjectEntry.COL_AGC_CODE + " ASC, "
            + ProjectEntry.COL_INVMT_TITLE + " ASC, "
            + ProjectEntry._ID + " ASC";

    private Context mCtx;
    private Cursor mCursor;
    private int mSelectedItemNdx;
    private OnItemSelectedListener mClickHdlr;


    public class VwHldr extends RecyclerView.ViewHolder implements View.OnClickListener {
        //public TextView agcCode;
        public ImageView schColour;
        public ImageView costColour;
        public TextView invmtTitle;
        public TextView projectName;

        public VwHldr(View view) {
            super(view);

            schColour = (ImageView) view.findViewById(R.id.schColour);
            costColour = (ImageView) view.findViewById(R.id.costColour);
            invmtTitle = (TextView) view.findViewById(R.id.invmtTitle);
            projectName = (TextView) view.findViewById(R.id.projectName);

            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            mSelectedItemNdx = getAdapterPosition();
            if (mSelectedItemNdx < 0) {
                return;
            }

            mCursor.moveToPosition(mSelectedItemNdx);

            int itemId = mCursor.getInt(COL_ITEM_ID);
            String agcCode = mCursor.getString(COL_AGC_CODE);
            String invmtId = mCursor.getString(COL_UNIQ_INVMT_ID);

            ProjectInfo item = new ProjectInfo();
            item.setID(itemId);
            item.setAc(agcCode);
            item.setUii(invmtId);

            // trigger the event on the view holding this adapter
            mClickHdlr.onClick(item, this);
        }

    }


    /**
     * This interface allows the adapter to pass item click events to the view containing it.
     */
    public static interface OnItemSelectedListener {
        void onClick(ProjectInfo project, VwHldr vh);
    }


    public ProjListAdptr(Context context, ProjListAdptr.OnItemSelectedListener hdlr) {
        mCtx = context;
        mClickHdlr = hdlr;
    }


    @Override
    public VwHldr onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_project, parent, false);
        VwHldr vh = new VwHldr(view);
        return vh;
    }


    @Override
    public void onBindViewHolder(VwHldr holder, int position) {
        String prevUniqInvmtId = "";
        if (position > 0) {
            mCursor.moveToPosition(position - 1);
            prevUniqInvmtId = mCursor.getString(COL_UNIQ_INVMT_ID);
        }

        mCursor.moveToPosition(position);
        String uniqInvmtId = mCursor.getString(COL_UNIQ_INVMT_ID);

        holder.schColour.setImageResource(SotipApp.getVarColour(mCursor.getInt(COL_SCH_VAR)));
        holder.costColour.setImageResource(SotipApp.getVarColour(mCursor.getInt(COL_COST_VAR)));

        if (uniqInvmtId.equals(prevUniqInvmtId)) {
            holder.invmtTitle.setVisibility(View.GONE);
        } else {
            holder.invmtTitle.setText(mCursor.getString(COL_INVMT_TITLE));
            holder.invmtTitle.setVisibility(View.VISIBLE);
        }

        holder.projectName.setText(mCursor.getString(COL_PROJ_NAME));
    }


    @Override
    public int getItemCount() {
        if (null == mCursor) return 0;
        return mCursor.getCount();
    }


    public void swapCursor(Cursor newCursor) {
        mCursor = newCursor;
        notifyDataSetChanged();
    }


    public Cursor getCursor() {
        return mCursor;
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
