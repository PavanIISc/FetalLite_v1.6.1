package com.sattvamedtech.fetallite.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;

import com.sattvamedtech.fetallite.R;
import com.sattvamedtech.fetallite.model.RiskFactor;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by vijeth on 18-07-2017.
 */

public class RiskFactorAdapter extends ArrayAdapter<RiskFactor> {

    private Context mContext;
    private ArrayList<RiskFactor> listState;
    private RiskFactorAdapter myAdapter;
    private LayoutInflater mInflater;
    private boolean isFromView = false;
    private String mSelected = "";
    private ArrayList<ViewHolder> mHolderList = new ArrayList<>();

    public RiskFactorAdapter(Context iContext, int resource, List<RiskFactor> objects) {
        super(iContext, resource, objects);
        this.mContext = iContext;
        mInflater = LayoutInflater.from(iContext);
        this.listState = (ArrayList<RiskFactor>) objects;
        this.myAdapter = this;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        return getCustomView(position, convertView, parent);
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        return getCustomView(position, convertView, parent);
    }


    public View getCustomView(final int position, View convertView,
                              ViewGroup parent) {

        final ViewHolder holder;
        if (convertView == null) {
            LayoutInflater layoutInflator = LayoutInflater.from(mContext);
            convertView = layoutInflator.inflate(R.layout.item_spinner_checkbox, null);
            holder = new ViewHolder();
            holder.mTextView = (TextView) convertView.findViewById(R.id.text);
            holder.mCheckBox = (CheckBox) convertView.findViewById(R.id.checkbox);
            holder.mTextView.setText(listState.get(position).getTitle());
            mHolderList.add(holder);
            if ((position == 0)) {
                holder.mCheckBox.setVisibility(View.INVISIBLE);
                holder.mCheckBox.setEnabled(false);
            }else {
                holder.mCheckBox.setVisibility(View.VISIBLE);
            }

            convertView.setTag(holder);

        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        isFromView = true;
        holder.mCheckBox.setChecked(listState.get(position).isSelected());
        isFromView = false;

        holder.mCheckBox.setTag(position);


        holder.mCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                int getPosition = (Integer) buttonView.getTag();
                if(getPosition != 0 && !isFromView){
                    listState.get(getPosition).setSelected(isChecked);
                    Log.e("Riskfactor",listState.get(getPosition).getTitle()+":"+listState.get(getPosition).isSelected());
                }
            }
        });

        return convertView;
    }

    public String getSelectedItems(){
        for (int i = 0;i < listState.size() ;i++){
            if(listState.get(i).isSelected()){
                if(!mSelected.equals(""))
                    mSelected = mSelected +","+ listState.get(i).getTitle();
                else
                    mSelected =  listState.get(i).getTitle();
            }
        }
        return mSelected;
    }

    public void setSelectedItems(String iList){
        String[] aList = iList.split(",");
        for(int i = 0;i < listState.size(); i++){
            for(int j = 0;j < aList.length; j++){
                if(listState.get(i).getTitle().equals(aList[j])){
                    listState.get(i).setSelected(true);
                   // mHolderList.get(i-1).mCheckBox.setChecked(true);
                    Log.e("NewTestDialog",""+listState.get(i).getTitle());
                }
            }
        }
    }

    private class ViewHolder {
        private TextView mTextView;
        private CheckBox mCheckBox;
    }
}
