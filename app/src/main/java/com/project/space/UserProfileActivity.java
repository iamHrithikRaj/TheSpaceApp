package com.project.space;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class UserProfileActivity extends AppCompatActivity implements LocationListener, GoogleApiClient.OnConnectionFailedListener,GoogleApiClient.ConnectionCallbacks {
    DatabaseReference reference;
    FirebaseAuth mAuth;
    FirebaseUser user;
    LatLng latLng;
    private View view;
    private GoogleMap spaceMap;
    private MapView mapView;
    Button start_stop;
    TextView start_stop_text;
    private GoogleApiClient client;
    private LocationRequest request;

    GeoFire geoFire;
    Marker mMarker;
    Location mLastLocation;
    static int count;
    {
        count = 0;
    }
    Marker mCurrent;
    String spaceKey;
    String sharing;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);
        start_stop = findViewById(R.id.start_stop_btn);
        start_stop_text = findViewById(R.id.start_stop_txt);
        mAuth = FirebaseAuth.getInstance();
        reference = FirebaseDatabase.getInstance().getReference().child("users");
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                String formattedName,formattedEmail,formattedKey;
                spaceKey = dataSnapshot.child(user.getUid()).child("code").getValue(String.class);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        client = new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();
        client.connect();
    }
    public void gotoMainActivity(View v){
        Intent intent = new Intent(UserProfileActivity.this,MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(UserProfileActivity.this,MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
    }


    public void logoutUser(View v){
        mAuth.signOut();
        Intent intent = new Intent(UserProfileActivity.this,ChooseLoginRegistrationActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
    }

    public void gotoAbout(View v){
        Intent intent = new Intent(UserProfileActivity.this,AboutActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
    }
    public void gotoJoinCircle(View v){
        Intent intent = new Intent(UserProfileActivity.this,JoinCircleActivity.class);
        //intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
    }

    public void shareLocationVia(View v){
        Intent i = new Intent(Intent.ACTION_SEND);
        i.setType("text/plain");
        i.putExtra(Intent.EXTRA_TEXT,"My Location: "+"https://www.google.com/maps/@"+"latLng.latitude"+", "+"latLng.longitude"+",17z");
        startActivity(Intent.createChooser(i,"Share using: "));
    }

    public void inviteVia(View v){
        Intent i = new Intent(Intent.ACTION_SEND);
        i.setType("text/plain");
        i.putExtra(Intent.EXTRA_TEXT,"Hi there! Check out the Space app it helps you follow the social distancing <LINK_TO_THE_APP>");
        startActivity(Intent.createChooser(i,"Invite using: "));
    }

    public void shareCodeVia(View v){
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                String formattedName,formattedEmail,formattedKey;
                spaceKey = dataSnapshot.child(user.getUid()).child("code").getValue(String.class);
                Intent i = new Intent(Intent.ACTION_SEND);
                i.setType("text/plain");
                i.putExtra(Intent.EXTRA_TEXT,"Hi there! my Space Key is "+spaceKey);
                startActivity(Intent.createChooser(i,"Send Space Key via: "));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }
    public void tracking(View v){

        if(UserProfileActivity.count%2==0){
            start_stop_text.setText("Start Sharing");
            start_stop.setBackgroundResource(R.drawable.start);
            reference.child(user.getUid()).child("isSharing").setValue("false");
            Toast.makeText(getApplicationContext(),"Tracking stopped",Toast.LENGTH_SHORT).show();
            LocationServices.FusedLocationApi.removeLocationUpdates(client,this);
        }else{
            start_stop_text.setText("Stop Sharing");
            start_stop.setBackgroundResource(R.drawable.stop);
            reference.child(user.getUid()).child("isSharing").setValue("true");
            Toast.makeText(getApplicationContext(),"Tracking started",Toast.LENGTH_SHORT).show();
            LocationServices.FusedLocationApi.requestLocationUpdates(client,request,this);
        }
        ++UserProfileActivity.count;
    }

    @Override
    public void onLocationChanged(Location location) {
        request = LocationRequest.create();
        request.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        request.setInterval(1000);
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
        LocationServices.FusedLocationApi.requestLocationUpdates(client,request,this);
    }
    @Override
    public void onConnected(@Nullable Bundle bundle) {
        request = LocationRequest.create();
        request.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        request.setInterval(1000);
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
        LocationServices.FusedLocationApi.requestLocationUpdates(client,request,this);
    }
    public void gotoFollowing(View v){
       return;
    }
    @Override
    public void onConnectionSuspended(int i) {

    }
    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }
}
