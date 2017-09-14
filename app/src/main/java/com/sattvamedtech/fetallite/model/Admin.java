package com.sattvamedtech.fetallite.model;

/**
 * Created by Pavan on 7/11/2017.
 */

public class Admin {

    public String adminid;
    public String name;
    public String lname;
    public String password;
    public String phonenumber;
    public String email;
    public String question;
    public String answer;

    public Admin(String adminid,String name,String lname, String password, String phonenumber, String email, String question, String answer ){
        this.adminid = adminid;
        this.name = name;
        this.lname = lname;
        this.password = password;
        this.phonenumber = phonenumber;
        this.email = email;
        this.question = question;
        this.answer = answer;
    }

    @Override
    public String toString() {
        return this.adminid;
    }
}
