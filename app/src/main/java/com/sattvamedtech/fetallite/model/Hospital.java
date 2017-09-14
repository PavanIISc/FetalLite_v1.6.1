package com.sattvamedtech.fetallite.model;

import com.j256.ormlite.field.DatabaseField;

import java.io.Serializable;

public class Hospital implements Serializable {
    @DatabaseField(generatedId = true, allowGeneratedIdInsert = true, columnName = "hospitalId")
    public int hospitalId;

    @DatabaseField
    public String name;

    @DatabaseField
    public String phoneNumber;

    @DatabaseField
    public String email;

    @DatabaseField
    public String address;

    public Hospital() {
    }

    public Hospital(String name, String phoneNumber, String email, String address) {
        this.name = name;
        this.phoneNumber = phoneNumber;
        this.email = email;
        this.address = address;
    }

    @Override
    public String toString() {
        //for login screen's drop down menu
        if(this.address.equals(""))
            return this.name;
        else
            return this.name + " , " +this.address;
    }
}
