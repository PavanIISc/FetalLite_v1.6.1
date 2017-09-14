package com.sattvamedtech.fetallite.model;

import com.j256.ormlite.field.DatabaseField;

import java.io.Serializable;

public class Patient implements Serializable {

    @DatabaseField(id = true)
    public String id;

    @DatabaseField
    public String pid;

    @DatabaseField
    public String firstName;

    @DatabaseField
    public String lastName;

    @DatabaseField
    public int age;

    @DatabaseField
    public String riskFactor;

    @DatabaseField
    public int gravidity;

    @DatabaseField
    public int parity;

    @DatabaseField
    public int gestationalWeeks;

    @DatabaseField
    public int gestationalDays;

    @DatabaseField(foreign = true, foreignColumnName = "id")
    public User user;

    @DatabaseField(foreign = true, foreignColumnName = "id")
    public Doctor doctor;



    public Patient(String id, String firstName, String lastName, int age, String riskFactor, int gravidity, int parity, int gestationalWeeks, int gestationalDays, User user, Doctor doctor) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.age = age;
        this.riskFactor = riskFactor;
        this.gravidity = gravidity;
        this.parity = parity;
        this.gestationalWeeks = gestationalWeeks;
        this.gestationalDays = gestationalDays;
        this.user = user;
        this.doctor = doctor;

    }

    public Patient() {
    }

    @Override
    public String toString(){
        return  this.id;
    }
}