package com.project.space;

import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class MyCircleActivity  extends AppCompatActivity {
    RecyclerView recyclerView;
    RecyclerView.Adapter adapter;
    RecyclerView.LayoutManager layoutManager;

    FirebaseAuth mAuth;
    FirebaseUser user;
    CreateUser createUser;
    ArrayList<CreateUser> nameList;
    DatabaseReference reference, usersReference;
    String circleMemberId;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView()
        recyclerView = findViewById(R.id.recyclerview);
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        nameList = new ArrayList<>();

        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);

        usersReference = FirebaseDatabase.getInstance().getReference().child("users");

        reference = FirebaseDatabase.getInstance().getReference().child("users").child(user.getUid()).child("CircleMembers");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                nameList.clear();
                if(dataSnapshot.exists()){
                    for(DataSnapshot dss:dataSnapshot.getChildren()){
                        circleMemberId = dss.child("circleMemberId").getValue(String.class);
                        usersReference.child(circleMemberId).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                createUser = dataSnapshot.getValue(CreateUser.class);
                                nameList.add(createUser);
                                adapter.notifyDataSetChanged();
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {
                                Toast.makeText(getApplicationContext(),databaseError.getMessage(),Toast.LENGTH_LONG).show();
                            }
                        });
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getApplicationContext(),databaseError.getMessage(),Toast.LENGTH_LONG).show();
            }
        });

        adapter = new MembersAdapter(nameList,getApplicationContext());
        recyclerView.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }
}
