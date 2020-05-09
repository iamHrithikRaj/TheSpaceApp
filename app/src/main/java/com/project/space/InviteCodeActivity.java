package com.project.space;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class InviteCodeActivity extends AppCompatActivity {
    private TextView spaceKey;
    FirebaseAuth mAuth;
    //Button ex = findViewById(R.id.continueButton);
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_invite_code);
        spaceKey = findViewById(R.id.code);
        Intent intent = getIntent();
        if(intent!=null){
            spaceKey.setText(intent.getStringExtra("spaceKey"));
        }
    }

    public void gotoStatusFragment(View v){
        mAuth = FirebaseAuth.getInstance();
        Intent intent = new Intent(InviteCodeActivity.this,ChooseLoginRegistrationActivity.class);
        mAuth.signOut();
        startActivity(intent);
        finish();
    }
}
