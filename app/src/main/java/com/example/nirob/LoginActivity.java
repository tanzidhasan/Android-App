package com.example.nirob;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Patterns;
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
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {

    private TextView log_in_t_reg, log_in_t_veri, log_in_t_for_pass;
    private EditText log_in_et_email, log_in_et_pass;
    private Button log_in_bt_log_in;
    private ProgressBar log_in_pb;

    private String l_email, l_password;

    private FirebaseAuth l_firebaseAuth;

    private FirebaseUser l_firebaseUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        l_firebaseAuth = FirebaseAuth.getInstance();
        l_firebaseUser = l_firebaseAuth.getCurrentUser();

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle("Login");

        log_in_t_reg = findViewById(R.id.t_log_in_reg);
        log_in_t_veri = findViewById(R.id.t_veri);
        log_in_et_email = findViewById(R.id.et_log_in_email);
        log_in_et_pass = findViewById(R.id.et_log_in_pass);
        log_in_bt_log_in = findViewById(R.id.bt_log_in);
        log_in_t_for_pass = findViewById(R.id.forget_pass);
        log_in_pb = findViewById(R.id.pb_log_in);


        log_in_t_reg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), SignUpActivity.class));
                finish();
            }
        });


        log_in_bt_log_in.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                l_email = log_in_et_email.getText().toString().trim();
                if(l_email.isEmpty()){
                    log_in_et_email.setError("Required!");
                    log_in_et_email.requestFocus();
                    return;
                }
                if(!(Patterns.EMAIL_ADDRESS.matcher(l_email).matches())){
                    log_in_et_email.setError("Invalid Email.");
                    log_in_et_email.requestFocus();
                    return;
                }

                l_password = log_in_et_pass.getText().toString().trim();
                if(l_password.isEmpty()){
                    log_in_et_pass.setError("Required!");
                    log_in_et_pass.requestFocus();
                    return;
                }

                log_in_pb.setVisibility(View.VISIBLE);

                l_firebaseAuth.signInWithEmailAndPassword(l_email, l_password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            l_firebaseAuth = FirebaseAuth.getInstance();
                            l_firebaseUser = l_firebaseAuth.getCurrentUser();
                            if(l_firebaseUser.isEmailVerified()){
                                log_in_pb.setVisibility(View.INVISIBLE);
                                Toast.makeText(LoginActivity.this, "Successfully Logged In.", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                intent.putExtra("key", R.id.menu_home);
                                startActivity(intent);
                                finish();

                            }else {
                                Toast.makeText(LoginActivity.this, "Email not verified.", Toast.LENGTH_LONG).show();
                                Handler handler = new Handler();
                                handler.postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        log_in_pb.setVisibility(View.INVISIBLE);
                                        log_in_t_veri.setVisibility(View.VISIBLE);
                                    }
                                }, 1000);
                            }


                        }else{
                            log_in_pb.setVisibility(View.INVISIBLE);
                            log_in_t_for_pass.setVisibility(View.VISIBLE);
                            Toast.makeText(LoginActivity.this, "Error! "+task.getException().getMessage(), Toast.LENGTH_LONG).show();
                        }
                    }
                });

            }
        });


        log_in_t_for_pass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                log_in_pb.setVisibility(View.VISIBLE);

                l_firebaseAuth.sendPasswordResetEmail(l_email).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        log_in_pb.setVisibility(View.INVISIBLE);
                        Toast.makeText(LoginActivity.this, "Password Reset Email Sent.", Toast.LENGTH_LONG).show();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        log_in_pb.setVisibility(View.INVISIBLE);
                        Toast.makeText(LoginActivity.this, "Error! " + e, Toast.LENGTH_LONG).show();
                    }
                });
            }
        });

    }


    @Override
    public boolean onSupportNavigateUp() {

        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra("key", R.id.menu_home);
        startActivity(intent);
        this.finish();

        return true;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra("key", R.id.menu_home);
        startActivity(intent);
        this.finish();
    }





}
