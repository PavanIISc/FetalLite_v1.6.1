package com.sattvamedtech.fetallite.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.sattvamedtech.fetallite.R;
import com.sattvamedtech.fetallite.helper.DateUtils;
import com.sattvamedtech.fetallite.model.Test;

import java.util.ArrayList;

public class TestMiniAdapter extends RecyclerView.Adapter<TestMiniAdapter.TestMiniHolder> {

    private Context mContext;
    private LayoutInflater mInflater;
    private ArrayList<Test> mTestList;

    public TestMiniAdapter(Context iContext, ArrayList<Test> iTestList) {
        mContext = iContext;
        mInflater = LayoutInflater.from(iContext);
        mTestList = iTestList;
    }

    @Override
    public TestMiniHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new TestMiniHolder(mInflater.inflate(R.layout.item_test_list_mini, parent, false));
    }

    @Override
    public void onBindViewHolder(TestMiniHolder holder, int position) {
//        holder.mTvTestId.setText(mTestList.get(position).id);
//        holder.mTvDate.setText(DateUtils.convertDateToLongHumanReadable(mTestList.get(position).testTime));
//        holder.mTvTime.setText(DateUtils.convertTimeToHumanReadable(mTestList.get(position).testTime));
//        holder.mTvTestDuration.setText(mContext.getString(R.string.label_mins, mTestList.get(position).testDurationInMinutes));

        holder.mTvTestId.setText(mTestList.get(position).id);
        //holder.mTvDate.setText(DateUtils.convertDateToLongHumanReadable(mTestList.get(position).testTime));
        //holder.mTvTime.setText(DateUtils.convertTimeToHumanReadable(mTestList.get(position).testTime));
        holder.mTvTestDuration.setText(mContext.getString(R.string.label_mins, mTestList.get(position).testDurationInMinutes));
    }

    @Override
    public int getItemCount() {
        return mTestList.size();
    }

    public class TestMiniHolder extends RecyclerView.ViewHolder {

        private TextView mTvTestId, mTvDate, mTvTime, mTvTestDuration;

        public TestMiniHolder(View itemView) {
            super(itemView);
            mTvTestId = (TextView) itemView.findViewById(R.id.tvTestId);
            mTvDate = (TextView) itemView.findViewById(R.id.tvDate);
            mTvTime = (TextView) itemView.findViewById(R.id.tvTime);
            mTvTestDuration = (TextView) itemView.findViewById(R.id.tvTestDuration);
        }
    }
}
