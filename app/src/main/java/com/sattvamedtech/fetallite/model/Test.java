package com.sattvamedtech.fetallite.model;

import com.j256.ormlite.field.DatabaseField;

import java.io.Serializable;

public class Test implements Serializable {
//    @DatabaseField(id = true)
//    public String id;
//
//    @DatabaseField
//    public long timeStamp;
//
//    @DatabaseField
//    public int testDurationInMinutes;
//
//    @DatabaseField
//    public String inputFileName;
//
//    @DatabaseField(foreign = true, foreignColumnName = "id")
//    public Patient patient;
//
//    @DatabaseField(foreign = true, foreignColumnName = "id")
//    public User user;
//
//    @DatabaseField(foreign = true, foreignColumnName = "hospitalId")
//    public Hospital hospital;

    @DatabaseField(id = true)
    public String id;

    @DatabaseField
    public String testDate;

    @DatabaseField
    public long testTime;

    @DatabaseField
    public int testDurationInMinutes;

    @DatabaseField
    public String inputFilePath;

    @DatabaseField(foreign = true, foreignColumnName = "id")
    public Patient patient;

    @DatabaseField(foreign = true, foreignColumnName = "id")
    public User user;

    @DatabaseField(foreign = true, foreignColumnName = "id")
    public Doctor doctor;

    @DatabaseField(foreign = true, foreignColumnName = "hospitalId")
    public Hospital hospital;

}
