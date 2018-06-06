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

public class LoginActivity extends AppCompatActivity {

    private EditText login_emailText,login_passwordText;
    private Button loginbtn,loginreg;
    private FirebaseAuth mAuth;
    private ProgressBar loginProgress;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        mAuth=FirebaseAuth.getInstance();
        login_emailText=(EditText)findViewById(R.id.reg_email);
        login_passwordText=(EditText)findViewById(R.id.reg_password);
        loginbtn=(Button)findViewById(R.id.reg_btn);
        loginreg=(Button)findViewById(R.id.login_reg_btn);
        loginProgress=(ProgressBar)findViewById(R.id.reg_progress);
        loginreg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(),RegisterActivity.class));
            }
        });
        loginbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String loginEmail=login_emailText.getText().toString();
                String loginPassword=login_passwordText.getText().toString();
                if(!TextUtils.isEmpty(loginEmail) && !TextUtils.isEmpty(loginPassword))
                {
                    loginProgress.setVisibility(View.VISIBLE);
                    mAuth.signInWithEmailAndPassword(loginEmail,loginPassword).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(task.isSuccessful())
                            {
                                sendToMain();
                            }else
                            {
                                String errorMessage=task.getException().getMessage();
                                Toast.makeText(getApplicationContext(),errorMessage,Toast.LENGTH_LONG).show();
                            }
                            loginProgress.setVisibility(View.INVISIBLE);
                        }
                    });
                }
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser currentUser= mAuth.getCurrentUser();
        if(currentUser != null)
        {
           sendToMain();
        }

    }

    private void sendToMain() {
        Intent mainIntent=new Intent(this,MainActivity.class);
        startActivity(mainIntent);
        finish();
    }
}
