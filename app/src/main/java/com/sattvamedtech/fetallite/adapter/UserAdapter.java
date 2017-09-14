package com.sattvamedtech.fetallite.adapter;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.sattvamedtech.fetallite.R;
import com.sattvamedtech.fetallite.fragment.AdminDashboardFragment;
import com.sattvamedtech.fetallite.fragment.HospitalDetailsFragment;
import com.sattvamedtech.fetallite.helper.MessageHelper;
import com.sattvamedtech.fetallite.model.User;

import java.util.ArrayList;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.UserHolder> {

    private Context mContext;
    private LayoutInflater mInflater;
    private ArrayList<User> mUserList;
    private ArrayList<UserAdapter.UserHolder> mUserHolderList;
    private UserClickListener mUserClickListener;

    public UserAdapter(Context iContext, ArrayList<User> iUserList, UserClickListener iUserClickListener) {
        mContext = iContext;
        mInflater = LayoutInflater.from(iContext);
        mUserList = iUserList;
        mUserClickListener = iUserClickListener;
        mUserHolderList = new ArrayList<UserAdapter.UserHolder>();
    }

    @Override
    public UserHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        UserAdapter.UserHolder aTemp = new UserHolder(mInflater.inflate(R.layout.item_user_doctor, parent, false));
        mUserHolderList.add(aTemp);
        return aTemp;
    }

    @Override
    public void onBindViewHolder(UserHolder holder, int position) {
        holder.mTvName.setText(mUserList.get(position).userid);
        HospitalDetailsFragment.TOGGLED_BY_SYSTEM = true;
        //holder.mIvEdit.setEnabled(mUserList.get(position).enable);
        holder.mUserSwitch.setChecked(mUserList.get(position).enable);
        HospitalDetailsFragment.TOGGLED_BY_SYSTEM = false;
    }

    @Override
    public int getItemCount() {
        return mUserList.size();
    }

    public void switchUser(int iPosition,boolean iEnable){
        HospitalDetailsFragment.TOGGLED_BY_SYSTEM = true;
        if(iEnable)
            mUserHolderList.get(iPosition).mUserSwitch.setChecked(true);
        else
            mUserHolderList.get(iPosition).mUserSwitch.setChecked(false);
        HospitalDetailsFragment.TOGGLED_BY_SYSTEM = false;
    }


//    public void resetSwitchesUser(){
//        for (int i = 0;i < mUserHolderList.size();i++)
//        {
//            if(mUserList.get(i).enable) {
//                mUserHolderList.get(i).mUserSwitch.setChecked(true);
//                Log.e("UserAdapter", ": " + mUserList.get(i).enable);
//            }
//            else
//                mUserHolderList.get(i).mUserSwitch.setChecked(false);
//        }
//    }




    public class UserHolder extends RecyclerView.ViewHolder {

        TextView mTvName;
        ImageView mIvEdit, mIvDelete;
        Switch mUserSwitch;

        public UserHolder(View itemView) {
            super(itemView);
            mTvName = (TextView) itemView.findViewById(R.id.tvName);
            mIvEdit = (ImageView) itemView.findViewById(R.id.ivEdit);
            mIvDelete = (ImageView) itemView.findViewById(R.id.ivDelete);
            mUserSwitch = (Switch) itemView.findViewById(R.id.ivUserSwitch);
            //Log.e("mUserSwitch", ": " );


            mIvEdit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mUserClickListener.onEditClick(getAdapterPosition());
                }
            });
            mIvDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mUserClickListener.onDeleteClick(getAdapterPosition());
                }
            });

            mUserSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                    mUserClickListener.onUserSwitch(getAdapterPosition(),isChecked);
                }
            });

        }
    }

    public interface UserClickListener {
        void onEditClick(int iPosition);

        void onDeleteClick(int iPosition);

        void onUserSwitch(int iPosition,boolean isChecked);
    }
}
