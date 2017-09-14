package com.sattvamedtech.fetallite.storage;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;
import android.util.Log;

import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.DeleteBuilder;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.stmt.UpdateBuilder;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;
import com.sattvamedtech.fetallite.helper.Logger;
import com.sattvamedtech.fetallite.model.Doctor;
import com.sattvamedtech.fetallite.model.Hospital;
import com.sattvamedtech.fetallite.model.Patient;
import com.sattvamedtech.fetallite.model.Test;
import com.sattvamedtech.fetallite.model.User;

import java.util.ArrayList;

public class DatabaseHelper extends OrmLiteSqliteOpenHelper {
    private static final String DATABASE_NAME = "fetalLite.db";
    private static final int DATABASE_VERSION = 10;
    private static DatabaseHelper mDataBaseHelper;
    private static Dao<Hospital, Integer> mHospital = null;
    private static Dao<User, Integer> mUser = null;
    //11-7-17
    private static Dao<Doctor, Integer> mDoctor = null;
    //
    private static Dao<Patient, Integer> mPatient = null;
    private static Dao<Test, Integer> mTest = null;

    public DatabaseHelper(Context context, String databaseName) {
        super(context, databaseName, null, DATABASE_VERSION);
    }

    public static DatabaseHelper getInstance(Context context) {
        if (null == mDataBaseHelper) {
            mDataBaseHelper = new DatabaseHelper(context, DATABASE_NAME);
            mDataBaseHelper.getWritableDatabase();
            try {
                mHospital = mDataBaseHelper.getHospitalDao();
            } catch (Exception e) {
                e.printStackTrace();
            }
            try {
                mUser = mDataBaseHelper.getUserDao();
            } catch (Exception e) {
                e.printStackTrace();
            }
            //11-7-17
            try {
                mDoctor = mDataBaseHelper.getDoctorDao();
            } catch (Exception e) {
                e.printStackTrace();
            }
            //
            try {
                mPatient = mDataBaseHelper.getPatientDao();
            } catch (Exception e) {
                e.printStackTrace();
            }
            try {
                mTest = mDataBaseHelper.getTestDao();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return mDataBaseHelper;
    }

    /**
     * This is called when the database is first created. Usually you should
     * call createTable statements here to create the tables that will store
     * your data.
     */
    @Override
    public void onCreate(SQLiteDatabase database, ConnectionSource connectionSource) {
        Logger.logInfo(DatabaseHelper.class.getName(), "onCreate");
        try {
            TableUtils.createTable(connectionSource, Hospital.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            TableUtils.createTable(connectionSource, User.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
        //11-7-17
        try {
            TableUtils.createTable(connectionSource, Doctor.class);
        } catch (Exception e) {
            e.printStackTrace();
        }

        //
        try {
            TableUtils.createTable(connectionSource, Patient.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            TableUtils.createTable(connectionSource, Test.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * This is called when your application is upgraded and it has a higher
     * version number. This allows you to adjust the various data to match the
     * new version number.
     */
    @Override
    public void onUpgrade(SQLiteDatabase database, ConnectionSource connectionSource, int oldVersion, int newVersion) {
        Logger.logInfo(DatabaseHelper.class.getName(), "onUpgrade");
        if (oldVersion == 9 && newVersion == 10) {
            try {
                TableUtils.dropTable(connectionSource, Test.class, true);
            } catch (Exception e) {
                e.printStackTrace();
            }
            try {
                TableUtils.createTable(connectionSource, Test.class);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            try {
                TableUtils.dropTable(connectionSource, Hospital.class, true);
            } catch (Exception e) {
                e.printStackTrace();
            }
            try {
                TableUtils.dropTable(connectionSource, User.class, true);
            } catch (Exception e) {
                e.printStackTrace();
            }
            //11-7-17
            try {
                TableUtils.dropTable(connectionSource, Doctor.class, true);
            } catch (Exception e) {
                e.printStackTrace();
            }
            //
            try {
                TableUtils.dropTable(connectionSource, Patient.class, true);
            } catch (Exception e) {
                e.printStackTrace();
            }
            try {
                TableUtils.dropTable(connectionSource, Test.class, true);
            } catch (Exception e) {
                e.printStackTrace();
            }
            onCreate(database, connectionSource);
        }
    }

    /**
     * Returns the Database Access Object (DAO) for our Hospital class. It
     * will create it or just give the cached value.
     */
    private Dao<Hospital, Integer> getHospitalDao() throws Exception {
        if (mHospital == null) {
            mHospital = getDao(Hospital.class);
        }
        return mHospital;
    }

    /**
     * Returns the Database Access Object (DAO) for our User class. It
     * will create it or just give the cached value.
     */
    private Dao<User, Integer> getUserDao() throws Exception {
        if (mUser == null) {
            mUser = getDao(User.class);
        }
        return mUser;
    }

    // 11-7-17
    private Dao<Doctor, Integer> getDoctorDao() throws Exception {
        if (mDoctor == null) {
            mDoctor = getDao(Doctor.class);
        }
        return mDoctor;
    }
    //

    /**
     * Returns the Database Access Object (DAO) for our Patient class. It
     * will create it or just give the cached value.
     */
    private Dao<Patient, Integer> getPatientDao() throws Exception {
        if (mPatient == null) {
            mPatient = getDao(Patient.class);
        }
        return mPatient;
    }

    /**
     * Returns the Database Access Object (DAO) for our Test class. It
     * will create it or just give the cached value.
     */
    private Dao<Test, Integer> getTestDao() throws Exception {
        if (mTest == null) {
            mTest = getDao(Test.class);
        }
        return mTest;
    }

    /**
     * Close the database connections and clear any cached DAOs.
     */
    @Override
    public void close() {
        super.close();
        mDataBaseHelper = null;
        mHospital = null;
        mUser = null;
        mPatient = null;
        mDoctor = null;
    }

    //************************ Hospital Data ************************

    public ArrayList<Hospital> getAllHospital() {
        try {
            QueryBuilder<Hospital, Integer> qb = mHospital.queryBuilder();
            return (ArrayList<Hospital>) qb.query();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new ArrayList<>();
    }

    public ArrayList<String> getAllHospitalNames() {
        try {

             ArrayList<Hospital> aHospitalList = getAllHospital();
             ArrayList<String> aHospitalNameList = new ArrayList<>();
            for(int i = 0;i < aHospitalList.size();i++){
                aHospitalNameList.add(aHospitalList.get(i).name);
            }
            return  aHospitalNameList;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new ArrayList<>();
    }

    public void addHospital(Hospital iUserData) {
        try {
            mHospital.createOrUpdate(iUserData);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Hospital getHospitalById(int iId) {
        try {
            return mHospital.queryBuilder().where().eq("hospitalId", iId).queryForFirst();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public void deleteHospital(int iId) {
        DeleteBuilder<Hospital, Integer> deleteBuilder = mHospital.deleteBuilder();
        try {
            deleteBuilder.where().eq("hospitalId", iId);
            deleteBuilder.delete();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //************************ User Data ************************

    /************************29-06-17*****************************/
    public User checkAdminOldPassword(String adminID, String enteredPassword){
        try {
            QueryBuilder<User, Integer> qb = mUser.queryBuilder();
            return qb.where().eq("username", adminID).and().eq("password", enteredPassword).queryForFirst();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /************************************************************/

    public ArrayList<User> getAllUsers() {
        try {
            QueryBuilder<User, Integer> qb = mUser.queryBuilder();
            return (ArrayList<User>) qb.where().eq("enable",true).query();
           //return (ArrayList<User>) qb.where().eq("type", User.TYPE_USER).query();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new ArrayList<>();
    }

    public int getAllUsersCount() {
        return getAllUsers().size();
    }

    public ArrayList<User> getAllUsers(Hospital iHospital) {
        try {
            QueryBuilder<User, Integer> qb = mUser.queryBuilder();
            return (ArrayList<User>) qb.where().eq("hospital_hospitalId", iHospital.hospitalId).query();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new ArrayList<>();
    }

    /*************************************5-7-2017**********************/
    public ArrayList<User> getAllEnabledUsers(Hospital iHospital) {
        try {
            QueryBuilder<User, Integer> qb = mUser.queryBuilder();
            return (ArrayList<User>) qb.where().eq("hospital_hospitalId", iHospital.hospitalId).and().eq("enable",true).query();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new ArrayList<>();
    }
    /*******************************************************************/

    public int getAllUsersCount(Hospital iHospital) {
        return getAllUsers(iHospital).size();
    }

    /*************************************5-7-2017**********************/
    public int getAllEnabledUsersCount(Hospital iHospital) {
        return getAllEnabledUsers(iHospital).size();
    }
    /*******************************************************************/

    public ArrayList<Doctor> getAllDoctors() {
        try {
            QueryBuilder<Doctor, Integer> qb = mDoctor.queryBuilder();
            return (ArrayList<Doctor>) qb.where().eq("enable",true).query();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new ArrayList<>();
    }

    public int getAllDoctorsCount() {

        return getAllDoctors().size();
    }

    public ArrayList<Doctor> getAllDoctors(Hospital iHospital) {
        try {
            QueryBuilder<Doctor, Integer> qb = mDoctor.queryBuilder();
            return (ArrayList<Doctor>) qb.where().eq("hospital_hospitalId", iHospital.hospitalId).and().eq("enable",true).query();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new ArrayList<>();
    }

    /*************************************5-7-2017**********************/
    public ArrayList<Doctor> getAllEnabledDoctors(Hospital iHospital) {
        try {
            QueryBuilder<Doctor, Integer> qb = mDoctor.queryBuilder();
            return (ArrayList<Doctor>) qb.where().eq("hospital_hospitalId", iHospital.hospitalId).and().eq("enable",true).query();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new ArrayList<>();
    }
    /*******************************************************************/

    /*************************************5-7-2017**********************/
    public ArrayList<Doctor> getAllDoctorsForAdapter(Hospital iHospital) {
        try {
            QueryBuilder<Doctor, Integer> qb = mDoctor.queryBuilder();
            return (ArrayList<Doctor>) qb.where().eq("hospital_hospitalId", iHospital.hospitalId).query();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new ArrayList<>();
    }
    /******************************************************************/


    public int getAllDoctorsCount(Hospital iHospital) {
        return getAllDoctors(iHospital).size();
    }
    /*************************************5-7-2017**********************/
    public int getAllEnabledDoctorsCount(Hospital iHospital) {
        return getAllEnabledDoctors(iHospital).size();
    }
    /*******************************************************************/

//    public void addUserDoctor(User iUserData) {
//        try {
//            mUser.createOrUpdate(iUserData);
////            Log.e("Phone:", " " + iUserData.phoneNumber);
////            Log.e("user_or_doctor_ID:", " " + iUserData.id);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
    //11-7-17
    public void addUser(User iUserData) {
        try {
            Log.e("DatabaseHelper : ","Database updating User");
            mUser.createOrUpdate(iUserData);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    //
    //11-7-17
    public void addDoctor(Doctor iDoctorData) {
        try {
            Log.e("DatabaseHelper : ","Database updating Doctor");
            mDoctor.createOrUpdate(iDoctorData);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    //


//    public void updateAdmin(User iUserData){
//        int id = iUserData.id;
//        try {
//             UpdateBuilder<User,Integer> ub = getUserDao().updateBuilder();
//            QueryBuilder<User,Integer> qb = getUserDao().queryBuilder();
//            ub.where().eq("username",iUserData.username);
//            long count_row = qb.where().eq("username",iUserData.username).countOf();
//            Log.e("count_rows:", " " + count_row);
//             ub.updateColumnValue("password",iUserData.password);
//             ub.updateColumnValue("phoneNumber",iUserData.phoneNumber);
//             ub.updateColumnValue("email",iUserData.email);
//             int update_rows =  ub.update();
//            Log.e("update_rows:", " " + update_rows);
//            Log.e("ID:", " " + id);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//
//
//
//    }



    public String getDoctorById(Doctor iDoctor) {
        try {
            return mDoctor.queryBuilder().where().eq("id", iDoctor.id).queryForFirst().name;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public String getUserNameById(User iUser) {
        try {
            return mUser.queryBuilder().where().eq("id", iUser.id).queryForFirst().userid;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public void deleteUserDoctor(int iId) {
        DeleteBuilder<User, Integer> deleteBuilder = mUser.deleteBuilder();
        try {
            deleteBuilder.where().eq("id", iId);
            deleteBuilder.delete();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void deleteAllOfHospital(Hospital iHospital) {
        DeleteBuilder<User, Integer> deleteBuilder = mUser.deleteBuilder();
        try {
            deleteBuilder.where().eq("hospital_hospitalId", iHospital.hospitalId);
            deleteBuilder.delete();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //have to include hospital as param
    public boolean usernameExists(String iUsername) {
        try {
            QueryBuilder<User, Integer> qb = mUser.queryBuilder();
            return (qb.where().eq("userid", iUsername).countOf() > 0);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    //11-7-17
    public boolean usernameExists(String iUsername,Hospital iHospital) {
        try {
            QueryBuilder<User, Integer> qb = mUser.queryBuilder();
            return (qb.where().eq("userid", iUsername).and().eq("hospital_hospitalId",iHospital.hospitalId).countOf() > 0);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
    //


    //have to include hospital as param
    public boolean DoctorNameExists(String iDoctorName) {
        try {
            QueryBuilder<Doctor, Integer> qb = mDoctor.queryBuilder();
            return (qb.where().eq("username", iDoctorName).countOf() > 0);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    //11-7-17
    public boolean DoctorNameExists(String iDoctorName,Hospital iHospital) {
        try {
            QueryBuilder<Doctor, Integer> qb = mDoctor.queryBuilder();
            return (qb.where().eq("name", iDoctorName).and().eq("hospital_hospitalId",iHospital.hospitalId).countOf() > 0);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
    //

    public boolean HospitalNameExists(String iHospitalName, String iAddress) {
        try {
            QueryBuilder<Hospital, Integer> qb = mHospital.queryBuilder();
            return (qb.where().eq("name", iHospitalName).and().eq("address",iAddress).countOf() > 0);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }


    public User validUserCredentials(String iUsername, String iPassword,Hospital iHospital) {
        try {
            QueryBuilder<User, Integer> qb = mUser.queryBuilder();
            return qb.where().eq("userid", iUsername).and().eq("password", iPassword).and().eq("enable",true).and().eq("hospital_hospitalId",iHospital.hospitalId).queryForFirst();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    //************************ Patient Data ************************

    public ArrayList<Patient> getAllPatient() {
        try {
            QueryBuilder<Patient, Integer> qb = mPatient.queryBuilder();
            return (ArrayList<Patient>) qb.query();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new ArrayList<>();
    }

    public void addPatient(Patient iPatientData) {
        try {
            mPatient.createOrUpdate(iPatientData);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Patient getPatientById(String pid) {
        try {
            return mPatient.queryBuilder().where().eq("id", pid).queryForFirst();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

//    public Patient getPatientByIdNameDob(String iId, long iDob, String iFirstName, String iLastName) {
//        try {
//            QueryBuilder<Patient, Integer> qb = mPatient.queryBuilder();
//            return qb.where().eq("id", iId).and().eq("dob", iDob).and().like("firstName", iFirstName).and().like("lastName", iLastName).queryForFirst();
////            if (!TextUtils.isEmpty(iId))
////                return qb.where().eq("id", iId).queryForFirst();
////            else if (iDob > 0)
////                return qb.where().eq("dob", iDob).queryForFirst();
////            else
////                return qb.where().like("firstName", iFirstName).and().like("lastName", iLastName).queryForFirst();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        return null;
//    }


    public Patient getPatientByIdNameAge(String iId, int iAge, String iFirstName, String iLastName) {
        try {
            QueryBuilder<Patient, Integer> qb = mPatient.queryBuilder();
            return qb.where().eq("id", iId).and().eq("age", iAge).and().like("firstName", iFirstName).and().like("lastName", iLastName).queryForFirst();

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }




    public void deletePatient(int iId) {
        DeleteBuilder<Patient, Integer> deleteBuilder = mPatient.deleteBuilder();
        try {
            deleteBuilder.where().eq("id", iId);
            deleteBuilder.delete();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //************************ Test Data ************************

    public ArrayList<Test> getAllTest() {
        try {
            QueryBuilder<Test, Integer> qb = mTest.queryBuilder();
            return (ArrayList<Test>) qb.orderBy("testTime", false).query();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new ArrayList<>();
    }

    public ArrayList<Test> getAllTestByHospital(Hospital iHospital) {
        try {
            QueryBuilder<Test, Integer> qb = mTest.queryBuilder();
            if (iHospital == null)
                return (ArrayList<Test>) qb.orderBy("testTime", false).query();
            else
                return (ArrayList<Test>) qb.orderBy("testTime", false).where().eq("hospital_hospitalId", iHospital.hospitalId).query();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new ArrayList<>();
    }

    public ArrayList<Test> getAllTestByTestDate(Hospital iHospital, String itestDate) {
        try {
            QueryBuilder<Test, Integer> qb = mTest.queryBuilder();
            if (iHospital == null) {
                return (ArrayList<Test>) qb.orderBy("testTime", false).query();
            }else {
                Log.e("DataBase_Helper","in else");
                return (ArrayList<Test>) qb.orderBy("testTime", false).where().eq("hospital_hospitalId", iHospital.hospitalId).and().eq("testDate", itestDate).query();

            }
            } catch (Exception e) {
            e.printStackTrace();
        }
        return new ArrayList<>();
    }

    public ArrayList<Test> getAllTestByTestDateAndPatient(Hospital iHospital,String itestDate,String iPatient) {
        try {
            QueryBuilder<Test, Integer> qb = mTest.queryBuilder();
            if (iHospital == null) {
                return (ArrayList<Test>) qb.orderBy("testTime", false).query();
            }else {
                QueryBuilder<Patient, Integer> qbPatient = mPatient.queryBuilder();
                Patient aTemp =  qbPatient.where().eq("id", iPatient).or().eq("firstName", iPatient).or().eq("lastName", iPatient).queryForFirst();
                Log.e("patient_pid",""+aTemp.id);
                return (ArrayList<Test>) qb.orderBy("testTime", false).where().eq("hospital_hospitalId", iHospital.hospitalId).and().eq("testDate", itestDate).and().eq("patient_id",aTemp.id).query();

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new ArrayList<>();
    }

    public ArrayList<Test> getAllTestByPatient(Patient iPatient, Hospital iHospital) {
        try {
            QueryBuilder<Test, Integer> qb = mTest.queryBuilder();
            return (ArrayList<Test>) qb.where().eq("patient_id", iPatient.id).and().eq("hospital_hospitalId", iHospital.hospitalId).query();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new ArrayList<>();
    }

    public ArrayList<Test> getAllTestByPatient(String iPatientDetail, long iTestTimeStamp, Hospital iHospital) {
        try {
            QueryBuilder<Test, Integer> qbTest = mTest.queryBuilder();
            if (iTestTimeStamp > 0)
                qbTest.where().eq("testTime", iTestTimeStamp).and().eq("hospital_hospitalId", iHospital.hospitalId);
            else
                qbTest.where().eq("hospital_hospitalId", iHospital.hospitalId);

            if (!TextUtils.isEmpty(iPatientDetail)) {
                QueryBuilder<Patient, Integer> qbPatient = mPatient.queryBuilder();
                qbPatient.where().eq("id", iPatientDetail).or().eq("firstName", iPatientDetail).or().eq("lastName", iPatientDetail);
                qbTest.join(qbPatient);
            }

            return (ArrayList<Test>) qbTest.orderBy("testTime", false).query();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new ArrayList<>();
    }

    public int getAllTestCount() {
        return getAllTest().size();
    }

    public void addTest(Test iTestData) {
        try {
            mTest.createOrUpdate(iTestData);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Test getTestById(String iId) {
        try {
            return mTest.queryBuilder().where().eq("id", iId).queryForFirst();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public void deleteTest(int iId) {
        DeleteBuilder<Test, Integer> deleteBuilder = mTest.deleteBuilder();
        try {
            deleteBuilder.where().eq("id", iId);
            deleteBuilder.delete();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
