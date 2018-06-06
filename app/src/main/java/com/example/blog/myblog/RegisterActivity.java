package com.example.blog.myblog;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class RegisterActivity extends AppCompatActivity {
private EditText regEmail,regPass,confirmPass;
private Button regbtn,loginreg;
private ProgressBar regProgress;
private FirebaseAuth mAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        regEmail=(EditText)findViewById(R.id.reg_email);
        regPass=(EditText)findViewById(R.id.reg_password);
        confirmPass=(EditText)findViewById(R.id.reg_confirm_pass);
        regbtn=(Button)findViewById(R.id.reg_btn);
        loginreg=(Button)findViewById(R.id.reg_login_btn);
        regProgress=(ProgressBar)findViewById(R.id.reg_progress);
        mAuth=FirebaseAuth.getInstance();
        loginreg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //startActivity(new Intent(getApplicationContext(),LoginActivity.class));
                finish();
            }
        });
        regbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String reg_Email=regEmail.getText().toString();
                String reg_Pass=regPass.getText().toString();
                String confirm_Pass=confirmPass.getText().toString();

                if(!TextUtils.isEmpty(reg_Email) && !TextUtils.isEmpty(reg_Pass) && !TextUtils.isEmpty(confirm_Pass))
                {
                    if(reg_Pass.equals(confirm_Pass)){
                        regProgress.setVisibility(View.VISIBLE);
                        mAuth.createUserWithEmailAndPassword(reg_Email,reg_Pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if(task.isSuccessful())
                                {
                                    startActivity(new Intent(getApplicationContext(),SetupActivity.class));
                                    finish();
                                }else
                                {
                                  String errorMessage=task.getException().getMessage();
                                  Toast.makeText(getApplicationContext(),"Error :"+errorMessage,Toast.LENGTH_LONG).show();
                                }
                                regProgress.setVisibility(View.INVISIBLE);
                            }
                        });

                    }else
                    {
                        Toast.makeText(getApplicationContext(),"Confirm Password ",Toast.LENGTH_LONG).show();
                    }
                }
            }
        });
    }

    @Override
    protected void onStart() {

        super.onStart();
        FirebaseUser currentUser=mAuth.getCurrentUser();
        if(currentUser!=null)
        {
          sendToMain();
        }
    }

    private void sendToMain() {
        startActivity(new Intent(getApplicationContext(),MainActivity.class));
        finish();
    }
}
