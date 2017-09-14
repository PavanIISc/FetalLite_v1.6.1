package com.sattvamedtech.fetallite.fragment;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.sattvamedtech.fetallite.FLApplication;
import com.sattvamedtech.fetallite.R;
import com.sattvamedtech.fetallite.activity.AdminDashboardActivity;
import com.sattvamedtech.fetallite.activity.HomeActivity;
import com.sattvamedtech.fetallite.activity.LoginActivity;
import com.sattvamedtech.fetallite.activity.TutorialsActivity;
import com.sattvamedtech.fetallite.adapter.DoctorAdapter;
import com.sattvamedtech.fetallite.adapter.UserAdapter;
import com.sattvamedtech.fetallite.dialog.AddDoctorDialog;
import com.sattvamedtech.fetallite.dialog.AddUserDialog;
import com.sattvamedtech.fetallite.helper.ApplicationUtils;
import com.sattvamedtech.fetallite.helper.Constants;
import com.sattvamedtech.fetallite.helper.GsonHelper;
import com.sattvamedtech.fetallite.helper.MessageHelper;
import com.sattvamedtech.fetallite.model.Admin;
import com.sattvamedtech.fetallite.model.Doctor;
import com.sattvamedtech.fetallite.model.Hospital;
import com.sattvamedtech.fetallite.model.User;
import com.sattvamedtech.fetallite.storage.DatabaseHelper;
import com.sattvamedtech.fetallite.storage.FLPreferences;

import java.util.ArrayList;

public class HospitalDetailsFragment extends Fragment implements View.OnClickListener {

    private TextView mTvName, mTvPhone, mTvEmail, mTvAddress;
    //private ImageView mIvClose;
    private ImageButton mIbDeleteHospital, mIbEditHospital;
    private Button mBAddUser, mBAddDoctor;
    private RecyclerView mRvUserList, mRvDoctorList;
    private Hospital mHospital;
    public static boolean TOGGLED_BY_SYSTEM = false;
    private Button mBNext;
    private ArrayList<Hospital> mHospitalList = new ArrayList<>();

    //private Switch mUserSwitch;

    //String name;

    private AddUserDialog mAddUserDialog;
    private AddDoctorDialog mAddDoctorDialog;

    private UserAdapter mUserAdapter;
    private DoctorAdapter mDoctorAdapter;

    private ArrayList<User> mUserList = new ArrayList<>();
    private ArrayList<Doctor> mDoctorList = new ArrayList<>();

    private UserAdapter.UserClickListener mUserClickListener = new UserAdapter.UserClickListener() {
        @Override
        public void onEditClick(int iPosition) {
            if (iPosition > -1 && iPosition < mUserList.size()) {
                initAddUserDialog();
                mAddUserDialog.showEdit(mUserList.get(iPosition));
            }
        }

        @Override
        public void onDeleteClick(int iPosition) {
            if (iPosition > -1 && iPosition < mUserList.size())
                confirmDeleteUser(iPosition);
        }

        @Override
        public void onUserSwitch(int iPosition,boolean isChecked) {
            if (isChecked) {
                if (iPosition > -1 && iPosition < mUserList.size())
                    confirmEnableUser(iPosition);
            } else {
                if (iPosition > -1 && iPosition < mUserList.size())
                    confirmDisableUser(iPosition);
            }
        }

//        @Override
//        public void onUserSwitch(int iPosition) {
//            if (iPosition > -1 && iPosition < mUserList.size())
//                confirmDisableUser(iPosition);
//        }
    };

    private DoctorAdapter.DoctorClickListener mDoctorClickListener = new DoctorAdapter.DoctorClickListener() {
        @Override
        public void onEditClick(int iPosition) {
            if (iPosition > -1 && iPosition < mDoctorList.size()) {
                initAddDoctorDialog();
                mAddDoctorDialog.showEdit(mDoctorList.get(iPosition));
            }
        }

        @Override
        public void onDeleteClick(int iPosition) {
            if (iPosition > -1 && iPosition < mDoctorList.size())
                confirmDeleteDoctor(iPosition);
        }

        @Override
        public void onDoctorSwitch(int iPosition,boolean isChecked) {
            if (isChecked) {
                if (iPosition > -1 && iPosition < mDoctorList.size())
                    confirmEnableDoctor(iPosition);
            } else {
                if (iPosition > -1 && iPosition < mDoctorList.size())
                    confirmDisableDoctor(iPosition);
            }
        }

    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_hospital_details, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getExtraArguments();
        initView(view);
        initListeners();
        setView();

    }

    private void getExtraArguments() {
        if (getArguments() != null && getArguments().getSerializable(Constants.EXTRA_HOSPITAL) != null) {
            mHospital = (Hospital) getArguments().getSerializable(Constants.EXTRA_HOSPITAL);
            Log.e("HospDeFrag", "mHospital = " + mHospital.name);
        }
    }

    public void setHospital() {
        mHospital = DatabaseHelper.getInstance(FLApplication.getInstance()).getHospitalById(mHospital.hospitalId);
        setView();
    }

    private void initView(View iView) {
        mBNext = (Button) iView.findViewById(R.id.bNext);
        mTvName = (TextView) iView.findViewById(R.id.tvTitle);
        mTvPhone = (TextView) iView.findViewById(R.id.tvPhone);
        mTvEmail = (TextView) iView.findViewById(R.id.tvEmail);
        mTvAddress = (TextView) iView.findViewById(R.id.tvAddress);

        //mIvClose = (ImageView) iView.findViewById(R.id.ivOpenClose);
        //mIvClose.setPadding(0, 0, 0, 0);
        //mIvClose.setImageResource(R.drawable.back_btn);

        mIbDeleteHospital = (ImageButton) iView.findViewById(R.id.ibDeleteHospital);
        mIbEditHospital = (ImageButton) iView.findViewById(R.id.ibEditHospital);
        //mUserSwitch = (Switch) iView.findViewById(R.id.ivUserSwitch);

        mBAddUser = (Button) iView.findViewById(R.id.bAddUser);
        mBAddDoctor = (Button) iView.findViewById(R.id.bAddDoctor);

        mRvUserList = (RecyclerView) iView.findViewById(R.id.rvUserList);
        mUserAdapter = new UserAdapter(getActivity(), mUserList, mUserClickListener);
        mRvUserList.setLayoutManager(new LinearLayoutManager(getActivity()));
        mRvUserList.addItemDecoration(new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL));
        mRvUserList.setAdapter(mUserAdapter);

//        mAdapter = new HospitalAdapter(getActivity(), mHospitalList, mHospitalClickListener,true);
//        mRvHospitalList.setAdapter(mAdapter);


        mRvDoctorList = (RecyclerView) iView.findViewById(R.id.rvDoctorList);
        mDoctorAdapter = new DoctorAdapter(getActivity(), mDoctorList, mDoctorClickListener, true);
        mRvDoctorList.setLayoutManager(new LinearLayoutManager(getActivity()));
        mRvDoctorList.addItemDecoration(new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL));
        mRvDoctorList.setAdapter(mDoctorAdapter);

        fetchUsers();
        fetchDoctors();

        ((AdminDashboardActivity)getActivity()).changeToolbar();
//        mUserAdapter.resetSwitchesUser();
//        mDoctorAdapter.resetSwitchesDoctor();

    }

    private void initListeners() {
        //mIvClose.setOnClickListener(this);
        mIbEditHospital.setOnClickListener(this);
        mIbDeleteHospital.setOnClickListener(this);
        mBAddUser.setOnClickListener(this);
        mBAddDoctor.setOnClickListener(this);
        mBNext.setOnClickListener(this);
//        mUserSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
//            @Override
//            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
//                if(!isChecked)
//                    Toast.makeText(getContext(), "User Disabled", Toast.LENGTH_SHORT).show();
//            }
//        });
    }

    private void setView() {
        if (mHospital != null) {


            mTvPhone.setText(mHospital.phoneNumber);
            mTvEmail.setText(mHospital.email);
            mTvAddress.setText(mHospital.address);
        }

        if(ApplicationUtils.mIsAdminAsDoctor == false && ApplicationUtils.mFromMenu == 1){
            disableAdminAsDoctor();
        }
        if(ApplicationUtils.mIsAdminAsUser == false && ApplicationUtils.mFromMenu == 1){
            disableAdminAsUser();
        }
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.ivOpenClose) {
            ((AdminDashboardActivity)getActivity()).initToolbar();
            getActivity().onBackPressed();

        } else if (view.getId() == R.id.ibEditHospital) {
            ((AdminDashboardActivity) getActivity()).showEditHospitalDialog(mHospital);
        } else if (view.getId() == R.id.ibDeleteHospital) {
            confirmDeleteHospital();
        } else if (view.getId() == R.id.bAddUser) {
            showAddUserDialog();
        } else if (view.getId() == R.id.bAddDoctor) {
            showAddDoctorDialog();
        }
        if (view.getId() == R.id.bNext) {
            //if(atleastOneHospital()) {
            if (allHospitalsHaveEnabledUsersAndEnabledDoctors()) {
                if (!FLPreferences.getInstance(FLApplication.getInstance()).getInitialProfile()) {
                    FLPreferences.getInstance(FLApplication.getInstance()).setInitialProfile(true);
                    Intent aIntent;
                    if (!FLPreferences.getInstance(FLApplication.getInstance()).getTutorialSeen()) {
                        aIntent = new Intent(getActivity(), TutorialsActivity.class);
                    } else if (TextUtils.isEmpty(FLPreferences.getInstance(FLApplication.getInstance()).getLoginSessionUserJson())) {
                        //  Log.e("AdminDashBoardFragment","User : "+FLPreferences.getInstance(FLApplication.getInstance()).getLoginSessionUserJson());
                        aIntent = new Intent(getActivity(), LoginActivity.class);
                    } else {
                        //   Log.e("AdminDashBoardFragment","User : "+FLPreferences.getInstance(FLApplication.getInstance()).getLoginSessionUserJson());
                        aIntent = new Intent(getActivity(), HomeActivity.class);
                    }
                    getActivity().finish();
                    startActivity(aIntent);
                } else {
                    Intent aIntent;
                    if (TextUtils.isEmpty(FLPreferences.getInstance(FLApplication.getInstance()).getLoginSessionUserJson())) {

                        // Log.e("AdminDashBoardFragment","User : "+FLPreferences.getInstance(FLApplication.getInstance()).getLoginSessionUserJson());
                        aIntent = new Intent(getActivity(), LoginActivity.class);
                    } else {
                        //    Log.e("AdminDashBoardFragment","User : "+FLPreferences.getInstance(FLApplication.getInstance()).getLoginSessionUserJson());
                        aIntent = new Intent(getActivity(), HomeActivity.class);
                    }
                    getActivity().finish();
                    startActivity(aIntent);
                }
            } else {
                /*************************************5-7-2017**********************/
                new MessageHelper(getActivity()).showTitleAlertOk(getString(R.string.error_invalid_data), getString(R.string.error_no_user_doctor_enabled), getString(R.string.action_ok), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                /********************************************************************/
            }
            //}
//            else {
//                new MessageHelper(getActivity()).showTitleAlertOk(getString(R.string.error_invalid_data), getString(R.string.error_no_hospital), getString(R.string.action_ok), new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        dialog.dismiss();
//                    }
//                });
//            }

        }
    }



    private void confirmDeleteHospital() {
        new MessageHelper(getActivity()).showTitleAlertOkCancel(getString(R.string.label_are_you_sure), getString(R.string.label_delete_hospital_confirm, mHospital.name), "", "", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
                DatabaseHelper.getInstance(FLApplication.getInstance()).deleteAllOfHospital(mHospital);
                DatabaseHelper.getInstance(FLApplication.getInstance()).deleteHospital(mHospital.hospitalId);
                getActivity().onBackPressed();
            }
        }, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
    }

    private void initAddUserDialog() {
        if (mAddUserDialog == null) {
            mAddUserDialog = new AddUserDialog(getActivity(), mHospital);
            mAddUserDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                @Override
                public void onDismiss(DialogInterface dialogInterface) {
                    mAddUserDialog.clearFields();
                    fetchUsers();
                    setHospital();
                }
            });
        }
    }

    private void initAddDoctorDialog() {
        if (mAddDoctorDialog == null) {
            mAddDoctorDialog = new AddDoctorDialog(getActivity(), mHospital);
            mAddDoctorDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                @Override
                public void onDismiss(DialogInterface dialogInterface) {
                    mAddDoctorDialog.clearFields();
                    fetchDoctors();
                    setHospital();
                }
            });
        }
    }

    private void disableAdminAsDoctor(){
        //disable admin as doctor
        Admin aAdmin = (Admin) GsonHelper.getGson(FLPreferences.getInstance(FLApplication.getInstance()).getAdminUser(), Admin.class);
        for(int i=0; i<mUserList.size(); i++){
            if(aAdmin.email.matches(mDoctorList.get(i).email)){
                //mDoctorList.get(i).enable = false;
                disableDoctor(i);
            }
        }
        //disableDoctor();
    }

    private void disableAdminAsUser(){
        //disable admin as user
        Admin aAdmin = (Admin) GsonHelper.getGson(FLPreferences.getInstance(FLApplication.getInstance()).getAdminUser(), Admin.class);
        for(int i=0; i<mUserList.size(); i++){
            if(aAdmin.adminid.matches(mUserList.get(i).userid)){
                //mUserList.get(i).enable = false;
                disableUser(i);
            }
        }

        //disableUser();
    }
    /********************************28-06-17*******************************/
    private void showAddUserDialog() {
        initAddUserDialog();
            mAddUserDialog.show();
    }
    /***************************************************************/

    private void showAddDoctorDialog() {
        initAddDoctorDialog();
        mAddDoctorDialog.show();

    }

    private void fetchUsers() {
        mUserList.clear();
        mUserList.addAll(DatabaseHelper.getInstance(FLApplication.getInstance()).getAllUsers(mHospital));
        mUserAdapter.notifyDataSetChanged();
    }

    private void fetchDoctors() {
        mDoctorList.clear();
        mDoctorList.addAll(DatabaseHelper.getInstance(FLApplication.getInstance()).getAllDoctorsForAdapter(mHospital));
        mDoctorAdapter.notifyDataSetChanged();
    }

    private void confirmDeleteUser(final int iPosition) {
        new MessageHelper(getActivity()).showTitleAlertOkCancel(getString(R.string.label_are_you_sure), getString(R.string.label_delete_user_confirm, mUserList.get(iPosition).userid), "", "", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
                deleteUser(iPosition);
            }
        }, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
    }

    private boolean allHospitalsHaveEnabledUsersAndEnabledDoctors() {
        if (DatabaseHelper.getInstance(FLApplication.getInstance()).getAllEnabledUsersCount(mHospital) < 1)
            return false;
        if (DatabaseHelper.getInstance(FLApplication.getInstance()).getAllEnabledDoctorsCount(mHospital) < 1)
            return false;

        return true;
    }

    public void fetchHospitals() {
        mHospitalList.clear();
        mHospitalList.addAll(DatabaseHelper.getInstance(getActivity().getApplicationContext()).getAllHospital());
        //mAdapter.notifyDataSetChanged();
    }
//    private void confirmDisableUser(final int iPosition) {
//        new MessageHelper(getActivity()).showTitleAlertOkCancel(getString(R.string.label_are_you_sure), getString(R.string.label_delete_user_confirm, mUserList.get(iPosition).username), "", "", new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialogInterface, int i) {
//                dialogInterface.dismiss();
//                disableUser(iPosition);
//            }
//        }, new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialogInterface, int i) {
//                dialogInterface.dismiss();
//            }
//        });
//    }
    private void deleteUser(int iPosition) {
        DatabaseHelper.getInstance(FLApplication.getInstance()).deleteUserDoctor(mUserList.get(iPosition).id);
        mUserList.remove(iPosition);
        mUserAdapter.notifyDataSetChanged();
    }



    /*************************************5-7-2017*******************************/
    private void confirmDisableUser(final int iPosition) {
        if(!TOGGLED_BY_SYSTEM){
            new MessageHelper(getActivity()).showTitleAlertOkCancel(getString(R.string.label_disable_user), getString(R.string.label_disable_user_confirm, mUserList.get(iPosition).userid), "", "", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.dismiss();
                    disableUser(iPosition);
                }
            }, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.dismiss();
                    mUserAdapter.switchUser(iPosition,true);
                }
            });
        }
    }

    private void confirmEnableUser(final int iPosition) {
        if(!TOGGLED_BY_SYSTEM) {
            new MessageHelper(getActivity()).showTitleAlertOkCancel(getString(R.string.label_enable_user), getString(R.string.label_enable_user_confirm, mUserList.get(iPosition).userid), "", "", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.dismiss();
                    enableUser(iPosition);
                }
            }, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.dismiss();
                    mUserAdapter.switchUser(iPosition, false);
                }
            });
        }
    }


    private void confirmDisableDoctor(final int iPosition) {
        if (!TOGGLED_BY_SYSTEM) {
            new MessageHelper(getActivity()).showTitleAlertOkCancel(getString(R.string.label_disable_doctor), getString(R.string.label_disable_doctor_confirm, mDoctorList.get(iPosition).name), "", "", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.dismiss();
                    disableDoctor(iPosition);
                }
            }, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.dismiss();
                    mDoctorAdapter.switchDoctor(iPosition,true);
                }
            });
        }
    }

    private void confirmEnableDoctor(final int iPosition) {
        if (!TOGGLED_BY_SYSTEM){
            new MessageHelper(getActivity()).showTitleAlertOkCancel(getString(R.string.label_enable_doctor), getString(R.string.label_enable_doctor_confirm, mDoctorList.get(iPosition).name), "", "", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.dismiss();
                    enableDoctor(iPosition);
                }
            }, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.dismiss();
                    mDoctorAdapter.switchDoctor(iPosition,false);
                }
            });
        }
    }
    /******************************************************************/



    /*************************************4-7-2017**********************/
    private void disableUser(final int iPosition){
        //int id = mUserList.get(iPosition).id;
        User mUser = mUserList.get(iPosition);
        mUser.enable = false;
        DatabaseHelper.getInstance(getActivity()).addUser(mUser);

        Toast.makeText(getContext(), "User disabled", Toast.LENGTH_SHORT).show();
    }

    private void enableUser(final int iPosition){
        //int id = mUserList.get(iPosition).id;
        User mUser = mUserList.get(iPosition);
        mUser.enable = true;
        DatabaseHelper.getInstance(getActivity()).addUser(mUser);

        Toast.makeText(getContext(), "User enabled", Toast.LENGTH_SHORT).show();
    }

    private void enableDoctor(final int iPosition){
        //int id = mUserList.get(iPosition).id;
        Doctor mDoctor = mDoctorList.get(iPosition);
        mDoctor.enable = true;
        DatabaseHelper.getInstance(getActivity()).addDoctor(mDoctor);

        Toast.makeText(getContext(), "Doctor enabled", Toast.LENGTH_SHORT).show();
    }

    private void disableDoctor(final int iPosition){
        //int id = mUserList.get(iPosition).id;
        Doctor mDoctor = mDoctorList.get(iPosition);
        mDoctor.enable = false;
        DatabaseHelper.getInstance(getActivity()).addDoctor(mDoctor);
        Toast.makeText(getContext(), "Doctor disabled", Toast.LENGTH_SHORT).show();
    }
    /*************************************************************************/

    //    private void disableUser(final int iPosition){
//        //int id = mUserList.get(iPosition).id;
//
//
//        mUserSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
//            @Override
//            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
//                //name = mUserList.get(iPosition).username;
//                Toast.makeText(getContext(), "USer disabled", Toast.LENGTH_SHORT).show();
//            }
//        });
//
//    }
    private void confirmDeleteDoctor(final int iPosition) {
        new MessageHelper(getActivity()).showTitleAlertOkCancel(getString(R.string.label_are_you_sure), getString(R.string.label_delete_user_confirm, mDoctorList.get(iPosition).name), "", "", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
                deleteDoctor(iPosition);
            }
        }, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
    }

    private void deleteDoctor(int iPosition) {
        DatabaseHelper.getInstance(FLApplication.getInstance()).deleteUserDoctor(mDoctorList.get(iPosition).id);
        mDoctorList.remove(iPosition);
        mDoctorAdapter.notifyDataSetChanged();
    }


    private boolean atleastOneHospital() {
        return mHospitalList.size() > 0;
    }

}
