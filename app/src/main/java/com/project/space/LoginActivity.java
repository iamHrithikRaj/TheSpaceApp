package com.project.space;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.common.internal.Objects;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.concurrent.Executor;

public class LoginActivity extends AppCompatActivity {
    private Button mLogin;
    private EditText mEmail, mPassword;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener  firebaseAuthStateListener;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        //getSupportActionBar().setTitle("Log In");
        firebaseAuthStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                if(user!=null && user.isEmailVerified()){
                    Intent intent = new Intent(getApplication(), MainActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                    finish();
                }/*else if(user!=null && !user.isEmailVerified()){
                    Toast.makeText(LoginActivity.this,"Email not verified yet",Toast.LENGTH_LONG).show();
                }*/
            }
        };

        mAuth = FirebaseAuth.getInstance();

        mLogin = findViewById(R.id.login);
        mEmail = findViewById(R.id.email);
        mPassword = findViewById(R.id.password);

        mLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String email = mEmail.getText().toString();
                final String password = mPassword.getText().toString();
                if(TextUtils.isEmpty(email)){
                    Toast.makeText(LoginActivity.this,"You forgot to enter the Email Address",Toast.LENGTH_SHORT).show();
                    return;
                }
                if(TextUtils.isEmpty(password)){
                    Toast.makeText(LoginActivity.this,"You forgot to enter the Password",Toast.LENGTH_SHORT).show();
                    return;
                }
                mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        FirebaseUser user = mAuth.getInstance().getCurrentUser();
                        if(!task.isSuccessful()){
                            Toast.makeText( LoginActivity.this, "Wrong username or password!",Toast.LENGTH_SHORT).show();
                        }else if(!user.isEmailVerified()){
                            Toast.makeText( LoginActivity.this, "Email not Verified yet",Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(firebaseAuthStateListener);
    }

    @Override
    protected void onStop() {
        super.onStop();
        mAuth.removeAuthStateListener(firebaseAuthStateListener);
    }
}
