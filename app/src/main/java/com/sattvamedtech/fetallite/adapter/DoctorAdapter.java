package com.sattvamedtech.fetallite.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;

import com.sattvamedtech.fetallite.R;
import com.sattvamedtech.fetallite.fragment.HospitalDetailsFragment;
import com.sattvamedtech.fetallite.model.Doctor;
import com.sattvamedtech.fetallite.model.User;

import java.util.ArrayList;

public class DoctorAdapter extends RecyclerView.Adapter<DoctorAdapter.UserHolder> {

    private Context mContext;
    private LayoutInflater mInflater;
    private ArrayList<Doctor> mDoctorList;
    private DoctorClickListener mDoctorClickListener;
    private boolean toShowEdit;
    private ArrayList<DoctorAdapter.UserHolder> mDoctorHolderList;

    public DoctorAdapter(Context iContext, ArrayList<Doctor> iDoctorList, DoctorClickListener iDoctorClickListener, boolean toShowEdit) {
        mContext = iContext;
        mInflater = LayoutInflater.from(iContext);
        mDoctorList = iDoctorList;
        mDoctorClickListener = iDoctorClickListener;
        this.toShowEdit = toShowEdit;
        mDoctorHolderList = new ArrayList<DoctorAdapter.UserHolder>();
    }

    @Override
    public UserHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        /*************************************5-7-2017**********************/
        DoctorAdapter.UserHolder aTemp = new DoctorAdapter.UserHolder(mInflater.inflate(R.layout.item_user_doctor, parent, false));
        mDoctorHolderList.add(aTemp);
        return aTemp;
        /*******************************************************************/
    }

    @Override
    public void onBindViewHolder(UserHolder holder, int position) {
        holder.mTvName.setText(mDoctorList.get(position).name);
        holder.mTvPhone.setText(mDoctorList.get(position).phoneNumber);
        holder.mTvPhone.setVisibility(View.VISIBLE);
        holder.mIvEdit.setVisibility(toShowEdit ? View.VISIBLE : View.GONE);
        //holder.mIvDelete.setVisibility(toShowEdit ? View.VISIBLE : View.GONE);
        holder.mIvDelete.setVisibility(View.GONE);

        /*************************************5-7-2017**********************/
        HospitalDetailsFragment.TOGGLED_BY_SYSTEM = true;

        //holder.mIvEdit.setEnabled(mUserList.get(position).enable);
        holder.mDoctorSwitch.setChecked(mDoctorList.get(position).enable);

        /*******************************************************************/
        HospitalDetailsFragment.TOGGLED_BY_SYSTEM = false;
    }

    @Override
    public int getItemCount() {
        return mDoctorList.size();
    }

    /*************************************5-7-2017**********************/
    public void switchDoctor(int iPosition,boolean iEnable){
        HospitalDetailsFragment.TOGGLED_BY_SYSTEM = true;
        if(iEnable)
            mDoctorHolderList.get(iPosition).mDoctorSwitch.setChecked(true);
        else
            mDoctorHolderList.get(iPosition).mDoctorSwitch.setChecked(false);
        HospitalDetailsFragment.TOGGLED_BY_SYSTEM = false;
    }
    /*******************************************************************/

    /****************************************05-07-2017******************8 */
    public void resetSwitchesDoctor(){
        for (int i = 0;i < mDoctorHolderList.size();i++)
        {
            if(mDoctorList.get(i).enable) {
                mDoctorHolderList.get(i).mDoctorSwitch.setChecked(true);
            }else {
                mDoctorHolderList.get(i).mDoctorSwitch.setChecked(false);

            }
        }
    }

    /************************************************************** */

    public class UserHolder extends RecyclerView.ViewHolder {

        TextView mTvName, mTvPhone;
        ImageView mIvEdit, mIvDelete;
        Switch mDoctorSwitch;

        public UserHolder(View itemView) {
            super(itemView);
            mTvName = (TextView) itemView.findViewById(R.id.tvName);
            mTvPhone = (TextView) itemView.findViewById(R.id.tvPhone);
            mIvEdit = (ImageView) itemView.findViewById(R.id.ivEdit);
            mIvDelete = (ImageView) itemView.findViewById(R.id.ivDelete);
            mDoctorSwitch = (Switch) itemView.findViewById(R.id.ivUserSwitch);

            mIvEdit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mDoctorClickListener.onEditClick(getAdapterPosition());
                }
            });
            mIvDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mDoctorClickListener.onDeleteClick(getAdapterPosition());
                }
            });

            mDoctorSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    mDoctorClickListener.onDoctorSwitch(getAdapterPosition(),isChecked);
                }
            });
        }

    }

    public interface DoctorClickListener {
        void onEditClick(int iPosition);

        void onDeleteClick(int iPosition);

        void onDoctorSwitch(int iPosition,boolean isChecked);
    }
}

