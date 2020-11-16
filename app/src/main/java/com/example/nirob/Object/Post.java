package com.example.nirob.Object;

public class Post {

    public String p01_post_id, p02_user_id, p03_full_name, p04_blood_group, p05_blood_amount, p06_about_disease, p07_hospital_name, p08_district, p09_deadline, p11_phone_number, p12_post_time;
    public boolean p15_managed;
    public long p10_deadline_mili, p13_post_mili_time, p14_post_mili_time_dec;

    public Post(){

    }

    public Post(String p01_post_id, String p02_user_id, String p03_full_name, String p04_blood_group, String p05_blood_amount, String p06_about_disease, String p07_hospital_name, String p08_district, String p09_deadline, long p10_deadline_mili, String p11_phone_number, String p12_post_time, long p13_post_mili_time, long p14_post_mili_time_dec, boolean p15_managed) {
        this.p01_post_id = p01_post_id;
        this.p02_user_id = p02_user_id;
        this.p03_full_name = p03_full_name;
        this.p04_blood_group = p04_blood_group;
        this.p05_blood_amount = p05_blood_amount;
        this.p06_about_disease = p06_about_disease;
        this.p07_hospital_name = p07_hospital_name;
        this.p08_district = p08_district;
        this.p09_deadline = p09_deadline;
        this.p10_deadline_mili = p10_deadline_mili;
        this.p11_phone_number = p11_phone_number;
        this.p12_post_time = p12_post_time;
        this.p13_post_mili_time = p13_post_mili_time;
        this.p14_post_mili_time_dec = p14_post_mili_time_dec;
        this.p15_managed = p15_managed;

    }
}
