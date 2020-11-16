package com.example.nirob;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.concurrent.TimeUnit;

public class MobileNoVerificationActivity extends AppCompatActivity {

    private TextView mv_t_counttime;
    private Button mv_bt_recode, mv_bt_veri;
    private EditText mv_et_veri_code;
    private ProgressBar mv_pb;
    private String mobile_no, verification_id;
    private FirebaseAuth v_firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mobile_no_verification);

        ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setTitle("Verify Mobile No.");
        actionBar.setDisplayHomeAsUpEnabled(true);
        v_firebaseAuth = FirebaseAuth.getInstance();

        mv_t_counttime = findViewById(R.id.t_mv_counttime);
        mv_bt_recode = findViewById(R.id.bt_mv_re_code);
        mv_et_veri_code = findViewById(R.id.et_mv_veri_code);
        mv_bt_veri = findViewById(R.id.bt_mv_veri);
        mv_pb = findViewById(R.id.pb_mv);

        Bundle extras = getIntent().getExtras();
        if(extras != null) {
            mobile_no = extras.getString("mob_no");
        }


        sendverification();
        counttiimer();

        mv_bt_recode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendverification();
                counttiimer();
            }
        });




        mv_bt_veri.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String code = mv_et_veri_code.getText().toString().trim();

                if(code.isEmpty()){
                    mv_et_veri_code.setError("Required!");
                    mv_et_veri_code.requestFocus();
                    return;
                }

                if(code.length() != 6){
                    mv_et_veri_code.setError("Error!");
                    mv_et_veri_code.requestFocus();
                    return;
                }

                mv_t_counttime.setVisibility(View.GONE);
                mv_bt_recode.setVisibility(View.GONE);
                mv_pb.setVisibility(View.VISIBLE);

                verifycode(code);
            }
        });


    }

    public void sendverification(){


        PhoneAuthProvider.OnVerificationStateChangedCallbacks phoneAuthProvider = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            @Override
            public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {

                String code = phoneAuthCredential.getSmsCode();

                if(code != null){
                    mv_et_veri_code.setText(code);
                    mv_t_counttime.setVisibility(View.GONE);
                    mv_bt_recode.setVisibility(View.GONE);
                    mv_pb.setVisibility(View.VISIBLE);
                    verifycode(code);
                }

            }

            @Override
            public void onVerificationFailed(@NonNull FirebaseException e) {


                Toast.makeText(MobileNoVerificationActivity.this, "Error! " + e.getMessage(), Toast.LENGTH_LONG).show();

            }

            @Override
            public void onCodeSent(@NonNull String s, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                super.onCodeSent(s, forceResendingToken);

                verification_id = s;
            }
        };

        PhoneAuthProvider.getInstance().verifyPhoneNumber("+88" + mobile_no, 120, TimeUnit.SECONDS, this, phoneAuthProvider);


    }



    public void verifycode(String code){

        PhoneAuthCredential phoneAuthCredential = PhoneAuthProvider.getCredential(verification_id, code);
        v_firebaseAuth.signInWithCredential(phoneAuthCredential).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {

                if(task.isSuccessful()){

                    mv_pb.setVisibility(View.GONE);
                    mv_t_counttime.setVisibility(View.GONE);
                    mv_bt_recode.setVisibility(View.GONE);
                    Toast.makeText(MobileNoVerificationActivity.this, "Authentication Successful.", Toast.LENGTH_LONG).show();
                    v_firebaseAuth.getCurrentUser().delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){
                                Intent intent = new Intent(MobileNoVerificationActivity.this, LoginActivity.class);
                                startActivity(intent);
                                finish();
                            }
                        }
                    });

                }else {
                    mv_pb.setVisibility(View.GONE);
                    mv_t_counttime.setVisibility(View.GONE);
                    mv_bt_recode.setVisibility(View.VISIBLE);
                    Toast.makeText(MobileNoVerificationActivity.this, "Error! " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                }

            }
        });
    }



    public void counttiimer(){

        mv_pb.setVisibility(View.GONE);
        mv_bt_recode.setVisibility(View.GONE);
        mv_t_counttime.setVisibility(View.VISIBLE);
        new CountDownTimer(120000, 1000) {

            @SuppressLint({"SetTextI18n", "DefaultLocale"})
            public void onTick(long millisUntilFinished) {
                mv_t_counttime.setText("Resend Code in : " + String.format("%02d", millisUntilFinished / (1000*60)) + ":" + String.format("%02d", (millisUntilFinished / 1000) % 60 ));
            }

            @SuppressLint("SetTextI18n")
            public void onFinish() {
                mv_t_counttime.setVisibility(View.GONE);
                mv_pb.setVisibility(View.GONE);
                mv_bt_recode.setVisibility(View.VISIBLE);
            }
        }.start();
    }


}
