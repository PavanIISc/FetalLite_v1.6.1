package com.sattvamedtech.fetallite.model;

import com.j256.ormlite.field.DatabaseField;

import java.io.Serializable;

public class User implements Serializable {
    /*****************************DB old database fields version < 1.4.3.1 **************************/
//    public static final int TYPE_USER = 0;
//    public static final int TYPE_DOCTOR = 1;
//    public static final int TYPE_ADMIN = 2;
    /******************************************************************************************/

    @DatabaseField(generatedId = true, allowGeneratedIdInsert = true)
    public int id;

    @DatabaseField
    public String userid;

    @DatabaseField
    public String fname;

    @DatabaseField
    public String lname;

    @DatabaseField
    public String password;

    @DatabaseField
    public String phoneNumber;

    @DatabaseField
    public String email;
/*****************************DB old database fields version < 1.4.3.1 **************************/
//    @DatabaseField
//    public String question;
//
//    @DatabaseField
//    public String answer;
//
//    @DatabaseField
//    public int type;
        /******************************************************************************************/

    @DatabaseField
    public boolean enable;

    @DatabaseField(foreign = true, foreignColumnName = "hospitalId")
    public Hospital hospital;

//    public User(String username, String password, String phoneNumber, String email, int type, boolean enable) {
//        this.username = username;
//        this.password = password;
//        this.phoneNumber = phoneNumber;
//        this.email = email;
//        this.type = type;
//        this.enable = enable;
//        this.hospital = null;
//    }
//
//    public User(String username, String password, String phoneNumber, String email, int type, boolean enable, Hospital hospital) {
//        this.username = username;
//        this.password = password;
//        this.phoneNumber = phoneNumber;
//        this.email = email;
//        this.type = type;
//        this.enable = enable;
//        this.hospital = hospital;
//    }
//
//    public User(String username, String password, String phoneNumber, String email, String question, String answer, int type, boolean enable) {
//        this.username = username;
//        this.password = password;
//        this.phoneNumber = phoneNumber;
//        this.email = email;
//        this.question = question;
//        this.answer = answer;
//        this.type = type;
//        this.enable = false;
//        this.hospital = null;
//    }

    public User(){

    }


    public User(String userid, String fname, String lname, String password, String email, String phoneNumber, boolean enable, Hospital hospital ) {
        this.userid = userid;
        this.fname = fname;
        this.lname = lname;
        this.password = password;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.enable = enable;
        this.hospital = hospital;
    }

    public User(String userid, String fname, String lname, String password, String email, String phoneNumber, boolean enable) {
        this.userid = userid;
        this.fname = fname;
        this.lname = lname;
        this.password = password;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.enable = enable;
        this.hospital = null;
    }


    @Override
    public String toString() {
        return this.userid;
    }
}
