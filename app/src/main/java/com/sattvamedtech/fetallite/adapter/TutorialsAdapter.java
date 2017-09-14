package com.sattvamedtech.fetallite.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.sattvamedtech.fetallite.R;
import com.sattvamedtech.fetallite.model.Tutorial;

import java.util.ArrayList;

public class TutorialsAdapter extends RecyclerView.Adapter<TutorialsAdapter.TutorialItemHolder> {

    private Context mContext;
    private LayoutInflater mInflater;
    private ArrayList<Tutorial> mTutorials;
    private TutorialItemClickListener mTutorialItemClickListener;

    public TutorialsAdapter(Context iContext, ArrayList<Tutorial> iTutorials, TutorialItemClickListener iTutorialItemClickListener) {
        mContext = iContext;
        mInflater = LayoutInflater.from(iContext);
        mTutorials = iTutorials;
        mTutorialItemClickListener = iTutorialItemClickListener;
    }

    @Override
    public TutorialItemHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new TutorialItemHolder(mInflater.inflate(R.layout.item_tutorial, parent, false));
    }

    @Override
    public void onBindViewHolder(TutorialItemHolder holder, int position) {
        holder.mTvTitle.setText(mTutorials.get(position).id + ". " + mTutorials.get(position).title);
    }

    @Override
    public int getItemCount() {
        return mTutorials.size();
    }

    public class TutorialItemHolder extends RecyclerView.ViewHolder {

        TextView mTvTitle;

        public TutorialItemHolder(View itemView) {
            super(itemView);
            mTvTitle = (TextView) itemView.findViewById(R.id.tvTitle);
            mTvTitle.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mTutorialItemClickListener.onTutorialItemClick(getAdapterPosition());
                }
            });
        }
    }

    public interface TutorialItemClickListener {
        void onTutorialItemClick(int iPosition);
    }
}
