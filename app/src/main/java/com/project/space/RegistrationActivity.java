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

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.SignInMethodQueryResult;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.Executor;

public class RegistrationActivity extends AppCompatActivity {
        private Button mRegistration;
        private EditText mEmail, mPassword, mName,mConfirmPassword;
        private String spaceKey;
        private FirebaseUser user;
        private String date;
        private FirebaseAuth mAuth;
        private FirebaseAuth.AuthStateListener  firebaseAuthStateListener;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);
        firebaseAuthStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                if(user!=null && user.isEmailVerified()){
                    Intent intent = new Intent(getApplication(), MainActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                    finish();
                }
            }
        };

        mAuth = FirebaseAuth.getInstance();
        mName = findViewById(R.id.name);
        mRegistration = findViewById(R.id.registration);
        mEmail = findViewById(R.id.email);
        mPassword = findViewById(R.id.password);
        mConfirmPassword = findViewById(R.id.confirmPassword);

        mRegistration.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String name = mName.getText().toString().trim();
                final String email = mEmail.getText().toString().trim();
                final String password = mPassword.getText().toString().trim();
                final String confirmPassword = mPassword.getText().toString().trim();

                if(TextUtils.isEmpty(name)){
                    Toast.makeText(RegistrationActivity.this,"Enter a unique username!\nProTip: use random name generator",Toast.LENGTH_SHORT).show();
                    return;
                }
                if(TextUtils.isEmpty(email)){
                    Toast.makeText(RegistrationActivity.this,"We need something to verify you!Please enter your Email Address",Toast.LENGTH_SHORT).show();
                    return;
                }
                if(TextUtils.isEmpty(password)){
                    Toast.makeText(RegistrationActivity.this,"Protect your account by entering a Password",Toast.LENGTH_SHORT).show();
                    return;

                }
                if(TextUtils.isEmpty(confirmPassword)){
                    Toast.makeText(RegistrationActivity.this,"Just to be on a safer side, enter the Password again :)",Toast.LENGTH_SHORT).show();
                    return;

                }
                if(password.length() < 6){
                    Toast.makeText(RegistrationActivity.this,"Password TOO short!",Toast.LENGTH_SHORT).show();
                    return;

                }
                if(password.equals(confirmPassword)) {
                    mAuth.fetchSignInMethodsForEmail(email).addOnCompleteListener(new OnCompleteListener<SignInMethodQueryResult>() {
                        @Override
                        public void onComplete(@NonNull Task<SignInMethodQueryResult> task) {
                            if(!task.isSuccessful()){
                                Toast.makeText(getApplication(), "You have already signed up.remember?.", Toast.LENGTH_SHORT).show();
                                return;
                            }
                        }
                    });
                    mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(RegistrationActivity.this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (!task.isSuccessful()) {
                                Toast.makeText(getApplication(), "Something went wrong during Signing in.", Toast.LENGTH_SHORT).show();
                                return;
                            } else {
                                user = mAuth.getInstance().getCurrentUser();
                                DatabaseReference currentUserDb = FirebaseDatabase.getInstance().getReference().child("users");
                                generateSpaceKey();
                                /*Map userInfo = new HashMap<>();
                                userInfo.put("email", email);
                                userInfo.put("name", name);
                                userInfo.put("profileImageUrl", "default");
                                userInfo.put("spaceKey",spaceKey);
                                userInfo.put("date",date);
                                userInfo.put("isSharing","false");*/

                                CreateUser userInfo = new CreateUser(name,email,date,spaceKey,"true",0,0,user.getUid(),0);

                                /*user.sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if(task.isSuccessful()){
                                            Toast.makeText(getApplication(), "Email sent for Verification", Toast.LENGTH_LONG).show();
                                            mAuth.signOut();
                                        }else{
                                            Toast.makeText(getApplication(), "ERROR sending verification Email", Toast.LENGTH_LONG).show();
                                        }
                                    }
                                });*/
                                sendVerification(user);
                                currentUserDb.child(user.getUid()).setValue(userInfo);

                                //gotoInviteCode
                                Intent intent = new Intent(RegistrationActivity.this,InviteCodeActivity.class);
                                intent.putExtra("spaceKey",spaceKey);
                                startActivity(intent);
                                finish();
                            }
                        }
                    });
                }else{
                    Toast.makeText(RegistrationActivity.this,"Oops! your Password didn't matched",Toast.LENGTH_SHORT).show();
                }
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

        public void generateSpaceKey(){
            Date mDate = new Date();
            SimpleDateFormat formatted = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss a", Locale.getDefault());
            date = formatted.format(mDate);
            Random r = new Random();
            int n = 100000 + r.nextInt(900000);
            spaceKey = String.valueOf(n);
        }
        public void sendVerification(FirebaseUser user){
            user.sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if(task.isSuccessful()){
                        Toast.makeText(getApplication(), "Email sent for Verification", Toast.LENGTH_LONG).show();
                    }else{
                        Toast.makeText(getApplication(), "ERROR sending verification Email", Toast.LENGTH_LONG).show();
                    }
                }
            });
        }
}
