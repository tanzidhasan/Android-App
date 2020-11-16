package com.example.nirob.Object;

public class User {

    public String u01_user_id, u02_full_name, u03_gender, u04_roll_no, u05_email,
            u06_blood_group, u07_last_blood_donation_date, u09_mobile_no, u10_district, u14_last_seen;
    public boolean u11_donated_blood, u12_wanna_donate, u13_status;

    public long u08_last_blood_donation_milisec;


    public User(){

    }

    public User(String u01_user_id, String u02_full_name, String u03_gender, String u04_roll_no, String u05_email, String u06_blood_group, String u07_last_blood_donation_date, long u08_last_blood_donation_milisec, String u09_mobile_no, String u10_district, boolean u11_donated_blood, boolean u12_wanna_donate, boolean u13_status, String u14_last_seen) {
        this.u01_user_id = u01_user_id;
        this.u02_full_name = u02_full_name;
        this.u03_gender = u03_gender;
        this.u04_roll_no = u04_roll_no;
        this.u05_email = u05_email;
        this.u06_blood_group = u06_blood_group;
        this.u07_last_blood_donation_date = u07_last_blood_donation_date;
        this.u08_last_blood_donation_milisec = u08_last_blood_donation_milisec;
        this.u09_mobile_no = u09_mobile_no;
        this.u10_district = u10_district;
        this.u11_donated_blood = u11_donated_blood;
        this.u12_wanna_donate = u12_wanna_donate;
        this.u13_status = u13_status;
        this.u14_last_seen = u14_last_seen;
    }
}
