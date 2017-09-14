package com.sattvamedtech.fetallite.adapter;

import android.content.Context;
import android.provider.Settings;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.RadioButton;
import android.widget.SeekBar;
import android.widget.TextView;

import com.sattvamedtech.fetallite.R;
import com.sattvamedtech.fetallite.activity.HomeActivity;
import com.sattvamedtech.fetallite.activity.LoginActivity;
import com.sattvamedtech.fetallite.fragment.TestFragment;
import com.sattvamedtech.fetallite.model.NavigationMenuItem;

import java.util.ArrayList;

public class DrawerListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context mContext;
    private LayoutInflater mInflater;
    private ArrayList<NavigationMenuItem> mNavigationMenuItems;
    private NavigationMenuItemClick mNavigationMenuItemClick;

    public DrawerListAdapter(Context iContext, ArrayList<NavigationMenuItem> iNavigationMenuItems, NavigationMenuItemClick iNavigationMenuItemClick) {
        mContext = iContext;
        mInflater = LayoutInflater.from(iContext);
        mNavigationMenuItems = iNavigationMenuItems;
        mNavigationMenuItemClick = iNavigationMenuItemClick;
    }

    @Override
    public int getItemViewType(int position) {
        return mNavigationMenuItems.get(position).viewType;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == NavigationMenuItem.VIEW_BRIGHTNESS)
            return new NavigationBrightnessItemHolder(mInflater.inflate(R.layout.item_navigation_menu_brightness, parent, false));
        else if (viewType == NavigationMenuItem.VIEW_THEME)
            return new NavigationThemeItemHolder(mInflater.inflate(R.layout.item_navigation_menu_theme, parent, false));
        else
            return new NavigationItemHolder(mInflater.inflate(R.layout.item_navigation_menu_default, parent, false));
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (getItemViewType(position) == NavigationMenuItem.VIEW_BRIGHTNESS) {
            ((NavigationBrightnessItemHolder) holder).mTvTitle.setText(mNavigationMenuItems.get(position).title);
            try {
                if (((LoginActivity) mContext).hasBrightnessPermission())
                    ((NavigationBrightnessItemHolder) holder).mSbBrightness.setProgress(Settings.System.getInt(mContext.getApplicationContext().getContentResolver(), Settings.System.SCREEN_BRIGHTNESS));
            } catch (Settings.SettingNotFoundException e) {
                e.printStackTrace();
            }
        } else if (getItemViewType(position) == NavigationMenuItem.VIEW_THEME) {
            ((NavigationThemeItemHolder) holder).mTvTitle.setText(mNavigationMenuItems.get(position).title);
            ((NavigationThemeItemHolder) holder).mTvTitle.setEnabled(((LoginActivity) mContext).getCurrentFragment() instanceof TestFragment);
            ((NavigationThemeItemHolder) holder).mRbDark.setEnabled(((LoginActivity) mContext).getCurrentFragment() instanceof TestFragment);
            ((NavigationThemeItemHolder) holder).mRbLight.setEnabled(((LoginActivity) mContext).getCurrentFragment() instanceof TestFragment);
        } else {
            ((NavigationItemHolder) holder).mTvTitle.setText(mNavigationMenuItems.get(position).title);
            // ((NavigationItemHolder) holder).mTvTitle.setEnabled(mNavigationMenuItems.get(position).enable || !((LoginActivity) mContext).isTestInProgress());
        }
    }

    @Override
    public int getItemCount() {
        return mNavigationMenuItems.size();
    }

    public class NavigationItemHolder extends RecyclerView.ViewHolder {

        TextView mTvTitle;

        public NavigationItemHolder(View itemView) {
            super(itemView);
            mTvTitle = (TextView) itemView.findViewById(R.id.tvTitle);
            mTvTitle.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mNavigationMenuItemClick.onNavigationMenuItemClick(getAdapterPosition());
                }
            });
        }
    }

    public class NavigationThemeItemHolder extends RecyclerView.ViewHolder {

        TextView mTvTitle;
        RadioButton mRbDark, mRbLight;

        public NavigationThemeItemHolder(View itemView) {
            super(itemView);
            mTvTitle = (TextView) itemView.findViewById(R.id.tvTitle);
            mRbDark = (RadioButton) itemView.findViewById(R.id.rbDark);
            mRbLight = (RadioButton) itemView.findViewById(R.id.rbLight);
            mRbDark.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (((HomeActivity) mContext).getCurrentFragment() instanceof TestFragment) {
                        if (isChecked)
                            ((TestFragment) ((HomeActivity) mContext).getCurrentFragment()).setTheme(true);
                    }
                    ((LoginActivity) mContext).closeDrawer();
                }
            });
            mRbLight.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (((HomeActivity) mContext).getCurrentFragment() instanceof TestFragment) {
                        if (isChecked)
                            ((TestFragment) ((HomeActivity) mContext).getCurrentFragment()).setTheme(false);
                    }
                    ((LoginActivity) mContext).closeDrawer();
                }
            });
        }
    }

    public class NavigationBrightnessItemHolder extends RecyclerView.ViewHolder {

        TextView mTvTitle;
        SeekBar mSbBrightness;

        public NavigationBrightnessItemHolder(View itemView) {
            super(itemView);
            mTvTitle = (TextView) itemView.findViewById(R.id.tvTitle);
            mSbBrightness = (SeekBar) itemView.findViewById(R.id.sbBrightness);
            mSbBrightness.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    int action = event.getAction();
                    switch (action) {
                        case MotionEvent.ACTION_DOWN:
                            // Disallow Drawer to intercept touch events.
                            v.getParent().requestDisallowInterceptTouchEvent(true);
                            break;

                        case MotionEvent.ACTION_UP:
                            // Allow Drawer to intercept touch events.
                            v.getParent().requestDisallowInterceptTouchEvent(false);
                            break;
                    }

                    // Handle seekbar touch events.
                    v.onTouchEvent(event);
                    return true;
                }
            });
            mSbBrightness.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                    if (((LoginActivity) mContext).hasBrightnessPermission())
                        Settings.System.putInt(mContext.getApplicationContext().getContentResolver(), Settings.System.SCREEN_BRIGHTNESS, progress);
                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {

                }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {

                }
            });
        }
    }

    public interface NavigationMenuItemClick {
        void onNavigationMenuItemClick(int iPosition);
    }
}