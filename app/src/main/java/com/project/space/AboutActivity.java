package com.project.space;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class AboutActivity extends AppCompatActivity {
    DatabaseReference reference;
    FirebaseAuth mAuth;
    FirebaseUser user;
    String username;
    String email;
    String spaceKey;
    TextView name,mail,key;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        reference = FirebaseDatabase.getInstance().getReference().child("users");
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                String formattedName,formattedEmail,formattedKey;
                setContentView(R.layout.activity_about);
                username = dataSnapshot.child(user.getUid()).child("name").getValue(String.class);
                email = dataSnapshot.child(user.getUid()).child("email").getValue(String.class);
                spaceKey = dataSnapshot.child(user.getUid()).child("code").getValue(String.class);
                name = findViewById(R.id.name);
                mail = findViewById(R.id.email);
                key = findViewById(R.id.key);
                formattedName = String.format("Username:       %s",username);
                name.setText(formattedName);
                formattedEmail = String.format("Email:                %s",email);
                mail.setText(formattedEmail);
                formattedKey = String.format("SpaceKey:        %s",spaceKey);
                key.setText(formattedKey);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(AboutActivity.this,UserProfileActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
    }

    //gobacktoMenu
    public void goBack(View v){
        if(user!=null){
            Intent intent = new Intent(AboutActivity.this,UserProfileActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            finish();
        }
    }
}
