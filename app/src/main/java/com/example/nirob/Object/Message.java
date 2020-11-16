package com.example.nirob.Object;

public class Message {

    public String m01_message_id, m02_sender_receiver_id, m03_sender_id, m04_receiver_id, m05_message, m07_message_time;
    public boolean m06_is_seen;
    public long m08_message_time_milisec, m09_message_time_milisec_dec;

    public Message(){

    }

    public Message(String m01_message_id, String m02_sender_receiver_id, String m03_sender_id, String m04_receiver_id, String m05_message, boolean m06_is_seen, String m07_message_time, long m08_message_time_milisec, long m09_message_time_milisec_dec) {
        this.m01_message_id = m01_message_id;
        this.m02_sender_receiver_id = m02_sender_receiver_id;
        this.m03_sender_id = m03_sender_id;
        this.m04_receiver_id = m04_receiver_id;
        this.m05_message = m05_message;
        this.m06_is_seen = m06_is_seen;
        this.m07_message_time = m07_message_time;
        this.m08_message_time_milisec = m08_message_time_milisec;
        this.m09_message_time_milisec_dec = m09_message_time_milisec_dec;
    }
}
