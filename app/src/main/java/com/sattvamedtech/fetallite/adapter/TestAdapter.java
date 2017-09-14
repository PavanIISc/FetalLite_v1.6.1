package com.sattvamedtech.fetallite.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.sattvamedtech.fetallite.R;
import com.sattvamedtech.fetallite.helper.DateUtils;
import com.sattvamedtech.fetallite.model.Test;

import java.util.ArrayList;

public class TestAdapter extends RecyclerView.Adapter<TestAdapter.TestHolder> {

    private Context mContext;
    private LayoutInflater mInflater;
    private ArrayList<Test> mTestList;
    private TestClickListener mTestClickListener;

    public TestAdapter(Context iContext, ArrayList<Test> iTestList, TestClickListener iTestClickListener) {
        mContext = iContext;
        mInflater = LayoutInflater.from(iContext);
        mTestList = iTestList;
        mTestClickListener = iTestClickListener;
    }

    @Override
    public TestHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new TestHolder(mInflater.inflate(R.layout.item_test_list, parent, false));
    }

    @Override
    public void onBindViewHolder(TestHolder holder, int position) {
       holder.mTvTestId.setText(mTestList.get(position).id);
        holder.mTvPatientNameId.setText(mTestList.get(position).patient.firstName + " " + mTestList.get(position).patient.lastName);
        holder.mTvTestPatientID.setText(mTestList.get(position).patient.id);

        holder.mTvTestDuration.setText(mContext.getString(R.string.label_mins, mTestList.get(position).testDurationInMinutes));
        holder.mTvAge.setText(""+ mTestList.get(position).patient.age);
        holder.mTvTime.setText(DateUtils.convertDateToLongHumanReadable(mTestList.get(position).testTime) + " | " + DateUtils.convertTimeToHumanReadable(mTestList.get(position).testTime));
    }

    @Override
    public int getItemCount() {
        return mTestList.size();
    }

    public class TestHolder extends RecyclerView.ViewHolder {

        private LinearLayout mLlRoot;
        private TextView mTvTestId, mTvPatientName, mTvTestDuration, mTvAge, mTvTime, mTvPatientNameId, mTvTestPatientID, mTvTestResult;

        public TestHolder(View itemView) {
            super(itemView);
            mLlRoot = (LinearLayout) itemView.findViewById(R.id.llRoot);
            mTvTestId = (TextView) itemView.findViewById(R.id.tvTestId);
            mTvPatientNameId = (TextView) itemView.findViewById(R.id.tvPatientNameId);
            mTvTestPatientID = (TextView) itemView.findViewById(R.id.tvTestListPatientId);
            mTvTestDuration = (TextView) itemView.findViewById(R.id.tvTestDuration);
            mTvAge = (TextView) itemView.findViewById(R.id.tvDate);
            mTvTime = (TextView) itemView.findViewById(R.id.tvTime);
            mLlRoot.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mTestClickListener.onTestClick(getAdapterPosition());
                }
            });
        }
    }

    public interface TestClickListener {
        void onTestClick(int iPosition);
    }
}
