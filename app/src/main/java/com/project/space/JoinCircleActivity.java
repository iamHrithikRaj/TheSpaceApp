package com.project.space;

import android.content.Intent;
import android.net.CaptivePortal;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.goodiebag.pinview.Pinview;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class JoinCircleActivity extends AppCompatActivity {
    Pinview pinView;
    DatabaseReference reference,currReference;
    FirebaseUser user;
    FirebaseAuth mAuth;
    String curr_user_id,join_user_id;
    DatabaseReference circle_reference;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_join_circle);
        pinView = findViewById(R.id.pin_view);
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        reference = FirebaseDatabase.getInstance().getReference().child("users");
        currReference = FirebaseDatabase.getInstance().getReference().child("users").child(user.getUid());
        curr_user_id = user.getUid();
    }
    public void joinButtonClick(View v){
        Query query = reference.orderByChild("code").equalTo(pinView.getValue());
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    CreateUser createUser = null;
                    for(DataSnapshot childDss : dataSnapshot.getChildren()){
                        createUser = childDss.getValue(CreateUser.class);
                        join_user_id = createUser.user_id;

                        circle_reference = FirebaseDatabase.getInstance().getReference().child("users").child(join_user_id).child("CircleMembers");

                        CircleJoin circleJoin = new CircleJoin(curr_user_id,0,-1);
                        //CircleJoin circleJoin1 = new CircleJoin(join_user_id);

                        circle_reference.child(user.getUid()).setValue(circleJoin).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if(task.isSuccessful()){
                                    Toast.makeText(getApplicationContext(),"Space joined successfully!!",Toast.LENGTH_LONG).show();
                                }
                            }
                        });
                        circle_reference = FirebaseDatabase.getInstance().getReference().child("users").child(curr_user_id).child("JoinedCircleMembers");

                         //CircleJoin circleJoin = new CircleJoin(curr_user_id);
                        CircleJoin circleJoin1 = new CircleJoin(join_user_id,99999999,-1);

                        circle_reference.child(join_user_id).setValue(circleJoin1).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if(task.isSuccessful()){
                                    //Toast.makeText(getApplicationContext(),"Space joined successfully!!",Toast.LENGTH_LONG).show();
                                }
                            }
                        });
                    }
                }else{
                    Toast.makeText(getApplicationContext(),"Invalid SpaceKey",Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(JoinCircleActivity.this,UserProfileActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
    }
    public void gotoBack(View v){
        Intent intent = new Intent(JoinCircleActivity.this,UserProfileActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
    }
}