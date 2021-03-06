package com.project.space;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.viewpager.widget.ViewPager;
import androidx.viewpager.widget.ViewPager.OnPageChangeListener;

import android.os.Build;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.maps.android.SphericalUtil;
import com.karan.churi.PermissionManager.PermissionManager;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;


public class MainActivity extends AppCompatActivity{
    Timer t = new Timer();
    LatLng latLngFrom, latLngTo;
    static ArrayList<Location>locList;
    ImageView status;
    RelativeLayout main_layout;
    TextView noofPeople;
    CreateUser createUser;
    ArrayList<CreateUser> nList;
    DatabaseReference reference, usersReference, currUserReference;
    String circleMemberId;
    PermissionManager manager;
    CreateUser currUser;
    FirebaseAuth mAuth;
    DataSnapshot userProfile;
    FirebaseUser user;
    int tempDis;
    int count;
    FragmentPagerAdapter adapterViewPager;
    private final int REQUEST_LOCATION_PERMISSION = 1;
    {
        count=0;
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        main_layout = findViewById(R.id.main_layout);
        noofPeople = findViewById(R.id.status_txt);
        main_layout.setBackgroundColor(getResources().getColor(R.color.Safe));
        status = findViewById(R.id.status);
        status.setBackgroundResource(R.drawable.safe);
        if(user == null){
            manager = new PermissionManager() {};
            manager.checkAndRequestPermissions(this);
        }

        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getInstance().getCurrentUser();
        //nameList = new ArrayList<>();


        ViewPager viewPager = findViewById(R.id.viewPager);

        TabsView tabsView = (TabsView) findViewById(R.id.tabs);
        tabsView.setUpWithViewPager(viewPager);

        adapterViewPager = new MyPagerAdapter(getSupportFragmentManager());
        viewPager.setAdapter(adapterViewPager);
        viewPager.setCurrentItem(1);


        viewPager.addOnPageChangeListener(new OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }
            @Override
            public void onPageSelected(int position) {

            }
            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        checkSurroundings();
        t.scheduleAtFixedRate(
                new TimerTask()
                {
                    public void run()
                    {
                        checkSurroundings();
                    }
                },
                0,      // run first occurrence immediatetly
                100000); // run every two seconds
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        manager.checkResult(requestCode,permissions,grantResults);
        ArrayList<String> denied_permissions = manager.getStatus().get(0).denied;
        if(denied_permissions.isEmpty()){
            Toast.makeText(getApplicationContext(),"Permission Enabled",Toast.LENGTH_SHORT).show();
        }

    }

    public static class MyPagerAdapter extends FragmentPagerAdapter {

        MyPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @NonNull
        @Override
        public Fragment getItem(int position) {
            switch (position){
                case 0:
                    // return fragment chat
                    return ChatFragment.newInstance();
                case 1:
                    // return fragment status //cam
                    //new MainActivity().onCreate();
                    //new MainActivity().onLocationChanged();
                    return StatusFragment.newInstance();
                case 2:
                    // return fragment map //stry
                    return MapFragment.newInstance();
            }
           return null;
        }

        @Override
        public int getCount() {
            return 3;
        }
    }
    public void gotoUserProfile(View v) {
                Intent intent = new Intent(MainActivity.this,UserProfileActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                finish();
    }
    public void checkSurroundings(){

        usersReference = FirebaseDatabase.getInstance().getReference().child("users");
        currUserReference = FirebaseDatabase.getInstance().getReference().child("users").child(user.getUid());
        reference = FirebaseDatabase.getInstance().getReference().child("users").child(user.getUid()).child("JoinedCircleMembers");
        //distanceReference = FirebaseDatabase.getInstance().getReference().child("users").child(user.getUid()).child("JoinedCircleMembers").child("distance");
        currUserReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                currUser = dataSnapshot.getValue(CreateUser.class);
                userProfile = dataSnapshot;
                //Toast.makeText(getApplicationContext(),"Success",Toast.LENGTH_SHORT).show();
                count = currUser.count;
                latLngFrom = new LatLng(currUser.lat,currUser.lng);
                /*final Location currUserLoc = new Location("");
                currUserLoc.setLatitude(currUser.lat);
                currUserLoc.setLongitude(currUser.lng);*/
                reference.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if(dataSnapshot.exists()){
                            for(DataSnapshot dss:dataSnapshot.getChildren()){
                                circleMemberId = dss.child("circleMemberId").getValue(String.class);
                                if(circleMemberId!=null) {
                                    usersReference.child(circleMemberId).addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                            createUser = dataSnapshot.getValue(CreateUser.class);
                                            latLngTo = new LatLng(createUser.lat,createUser.lng);
                                            /*Location userLoc = new Location("");
                                            userLoc.setLatitude(createUser.lat);
                                            userLoc.setLongitude(createUser.lng);*/
                                            Integer distance1 = userProfile.child("JoinedCircleMembers").child(createUser.user_id).child("currDistance").getValue(Integer.class);
                                            Integer distance2 = userProfile.child("JoinedCircleMembers").child(createUser.user_id).child("prevDistance").getValue(Integer.class);
                                            tempDis = (int)(SphericalUtil.computeDistanceBetween(latLngFrom, latLngTo));
                                            //tempDis = Math.floor(tempDis * 100) / 100;
                                            if (distance1 != null && distance2 != null) {
                                                int d1 = distance1;
                                                int d2 = distance2;
                                                /*if(Double.compare(distance1,0)==0 && Double.compare(tempDis, distance1)!=0){
                                                    //tempDis = SphericalUtil.computeDistanceBetween(latLngFrom, latLngTo);
                                                    if(count>=0) {
                                                        ++count;
                                                        reference.child(createUser.user_id).child("currDistance").setValue(tempDis);
                                                        currUserReference.child("count").setValue(count);
                                                    }
                                                }else {*/
                                                    //tempDis = currUserLoc.distanceTo(userLoc);
                                                    //tempDis = SphericalUtil.computeDistanceBetween(latLngFrom, latLngTo);
                                                    //tempDis = CalculationByDistance(latLngFrom, latLngTo);
                                                    //tempDis = Math.floor(tempDis * 100) / 100;
                                                    /*if(Double.compare(tempDis, 1.50)<0){

                                                    }
                                                    if(Double.compare(tempDis, 1.50)<0 && Double.compare(tempDis, distance1)>0){

                                                    }*/
                                                    if(tempDis > 1 && tempDis > distance1){
                                                        if(count>0) {
                                                            --count;
                                                            currUserReference.child("count").setValue(count);
                                                            reference.child(createUser.user_id).child("currDistance").setValue(tempDis);
                                                        }
                                                    }
                                                    if(tempDis < 1 && tempDis < distance1){
                                                        if(count>=0) {
                                                            count++;
                                                            currUserReference.child("count").setValue(count);
                                                            reference.child(createUser.user_id).child("currDistance").setValue(tempDis);
                                                        }
                                                    }
                                                    /*if (Double.compare(tempDis, 1.50) < 0 && Double.compare(distance1, distance2) != 0 && Double.compare(tempDis, distance2) < 0) {
                                                        //++count;
                                                        if (count >= 0) {
                                                            int temp = count;
                                                            currUserReference.child("count").setValue(++temp);
                                                            temp = 0;
                                                        }
                                                        //usersReference.child(createUser.user_id).child("JoinedCircleMembers").child(user.getUid()).child("prevDistance").setValue(distance1);
                                                        //usersReference.child(createUser.user_id).child("JoinedCircleMembers").child(user.getUid()).child("currDistance").setValue(tempDis);
                                                    } else if (Double.compare(tempDis, 1.5) > 0 && Double.compare(distance1, distance2) != 0 && Double.compare(distance1, distance2) > 0) {
                                                        //--count;
                                                        //int temp = --count;
                                                        //if (temp > 0) {
                                                        if (count > 0) {
                                                            int temp = count;
                                                            currUserReference.child("count").setValue(--temp);
                                                            temp = 0;
                                                        }
                                                        //usersReference.child(createUser.user_id).child("JoinedCircleMembers").child(user.getUid()).child("prevDistance").setValue(distance1);
                                                        //usersReference.child(createUser.user_id).child("JoinedCircleMembers").child(user.getUid()).child("currDistance").setValue(tempDis);
                                                    }

                                                    reference.child(createUser.user_id).child("prevDistance").setValue(distance1);

                                                    //currUser.child("JoinedCircleMembers").child(createUser.user_id).child("currDistance");
                                                    //userProfile.child("JoinedCircleMembers").child(createUser.user_id).child("prevDistance").setValue(distance1);
                                                    //usersReference.child(user.getUid()).child("JoinedCircleMembers").child(createUser.user_id).child("currDistance").setValue(tempDis);
                                                    //usersReference.child(user.getUid()).child("JoinedCircleMembers").child(createUser.user_id).child("prevDistance").setValue(distance1);*/
                                                //}
                                            }
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError databaseError) {
                                            Toast.makeText(getApplicationContext(), databaseError.getMessage(), Toast.LENGTH_LONG).show();
                                        }
                                    });
                                }
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Toast.makeText(getApplicationContext(),databaseError.getMessage(),Toast.LENGTH_LONG).show();
                    }
                });

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        showStatus();
    }
    public void showStatus(){
        currUserReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                currUser = dataSnapshot.getValue(CreateUser.class);
                if(currUser!=null) {
                    if (currUser.count > 10) {
                        main_layout.setBackgroundColor(getResources().getColor(R.color.Danger));
                        status.setBackgroundResource(R.drawable.danger);
                        noofPeople.setText("No. of people within 1m radius: " + currUser.count);
                        Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                        // Vibrate for 500 milliseconds
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                            v.vibrate(VibrationEffect.createOneShot(20000, VibrationEffect.DEFAULT_AMPLITUDE));
                        } else {
                            //deprecated in API 26
                            v.vibrate(20000);
                        }
                    } else if (currUser.count > 5) {
                        main_layout.setBackgroundColor(getResources().getColor(R.color.Warning));
                        status.setBackgroundResource(R.drawable.warning);
                        noofPeople.setText("No. of people within 1m radius: " + currUser.count);
                        Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                        // Vibrate for 500 milliseconds
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                            v.vibrate(VibrationEffect.createOneShot(5000, VibrationEffect.DEFAULT_AMPLITUDE));
                        } else {
                            //deprecated in API 26
                            v.vibrate(5000);
                        }
                    } else {
                        main_layout.setBackgroundColor(getResources().getColor(R.color.Safe));
                        status.setBackgroundResource(R.drawable.safe);
                        noofPeople.setText("No. of people within 1m radius: " + currUser.count);
                    }
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void refresh(View v){
        Toast.makeText(getApplicationContext(),"Scanning your Space...",Toast.LENGTH_LONG).show();
        checkSurroundings();
    }
    /*public double CalculationByDistance(LatLng StartP, LatLng EndP) {
        int Radius = 6371;// radius of earth in Km
        double lat1 = StartP.latitude;
        double lat2 = EndP.latitude;
        double lon1 = StartP.longitude;
        double lon2 = EndP.longitude;
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                + Math.cos(Math.toRadians(lat1))
                * Math.cos(Math.toRadians(lat2)) * Math.sin(dLon / 2)
                * Math.sin(dLon / 2);
        double c = 2 * Math.asin(Math.sqrt(a));
        double valueResult = Radius * c;
        double km = valueResult / 1;
        DecimalFormat newFormat = new DecimalFormat("####");
        int kmInDec = Integer.valueOf(newFormat.format(km));
        double meter = valueResult % 1000;
        int meterInDec = Integer.valueOf(newFormat.format(meter));
        Log.i("Radius Value", "" + valueResult + "   KM  " + kmInDec
                + " Meter   " + meterInDec);

        return Radius * c;
    }*/
}
