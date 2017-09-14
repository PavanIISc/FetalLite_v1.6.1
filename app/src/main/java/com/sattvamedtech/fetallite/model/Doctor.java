package com.sattvamedtech.fetallite.model;

import com.j256.ormlite.field.DatabaseField;

import java.io.Serializable;

/**
 * Created by Pavan on 7/11/2017.
 */

public class Doctor implements Serializable {

    @DatabaseField(generatedId = true, allowGeneratedIdInsert = true)
    public int id;

    @DatabaseField
    public String name;

    @DatabaseField
    public String phoneNumber;

    @DatabaseField
    public String email;

    @DatabaseField
    public boolean enable;

    @DatabaseField(foreign = true, foreignColumnName = "hospitalId")
    public Hospital hospital;

    public Doctor(){

    }

    public Doctor(String name, String email, String phoneNumber, boolean enable, Hospital hospital ) {
        this.name = name;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.enable = enable;
        this.hospital = hospital;
    }


    public Doctor(String name, String email, String phoneNumber, boolean enable) {
        this.name = name;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.enable = enable;
        this.hospital = null;
    }

    @Override
    public String toString() {
        return this.name;
    }
}
