package com.project.space;


import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;

import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.firebase.geofire.GeoFire;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApi;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;


public class MapFragment extends Fragment implements OnMapReadyCallback, NavigationView.OnNavigationItemSelectedListener,
        GoogleApiClient.OnConnectionFailedListener,GoogleApiClient.ConnectionCallbacks,LocationListener {
    private View view;
   private GoogleMap spaceMap;
   private MapView mapView;
   private GoogleApiClient client;
   private LocationRequest request;
   private LatLng latlng;
   Marker mCurrentLocation;
   CreateUser createUser,currUser;
    DatabaseReference reference;
    FirebaseAuth mAuth;
    String circleMemberId;
    FirebaseUser user;
    Circle circle;

    public static MapFragment newInstance(){
        return new MapFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_map,container,false);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mAuth = FirebaseAuth.getInstance();
        reference = FirebaseDatabase.getInstance().getReference().child("users");
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        mapView = view.findViewById(R.id.mapsView);

        if(mapView!=null){
            mapView.onCreate(null);
            mapView.onResume();
            mapView.getMapAsync(this);
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        spaceMap = googleMap;

        client = new GoogleApiClient.Builder(getContext())
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();
        client.connect();
        /*LatLng India = new LatLng(28.4595,77.0266); --default location set as india
        sapceMap.addMarker(new MarkerOptions().position(India).title("You are Here!"));
        sapceMap.moveCamera(CameraUpdateFactory.newLatLng(India));*/
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        return false;
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {

        request = LocationRequest.create();
            request.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
            request.setInterval(1000);
        Context context = getContext();
        if(context!=null) {
            if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
        }
        LocationServices.FusedLocationApi.requestLocationUpdates(client,request,this);
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onLocationChanged(Location location) {
       if(location == null){
           Toast.makeText(getContext(),"Could not get location",Toast.LENGTH_SHORT).show();
       } else{
           if(mCurrentLocation!=null) mCurrentLocation.remove();
        latlng = new LatLng(location.getLatitude(),location.getLongitude());
        if(user!=null) {
            reference.child(user.getUid()).child("lng").setValue(location.getLongitude());
            reference.child(user.getUid()).child("lat").setValue(location.getLatitude());
            //mCurrentLocation =  spaceMap.addMarker(new MarkerOptions().icon(BitmapDescriptorFactory.fromResource(R.drawable.pin)).position(latlng).title("You"));
            mCurrentLocation = spaceMap.addMarker(new MarkerOptions().position(latlng).title("You"));
        }
       /* MarkerOptions option = new MarkerOptions();
        option.position(latlng);
        option.title("Current Location");
        spaceMap.addMarker(option);*/
       }
        spaceMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(location.getLatitude(), location.getLongitude()), 20.0f));
        //spaceMap.animateCamera( CameraUpdateFactory.zoomTo( 15.0f ) );
        /*circle = spaceMap.addCircle(new CircleOptions()
                .center(new LatLng(location.getLatitude(), location.getLongitude()))
                .radius(1)
                .strokeWidth(10)
                .strokeColor(Color.BLUE)
                .fillColor(Color.argb(0, 0, 0, 195))
                .clickable(true));*/


    }
    @Override
    public void onConnectionSuspended(int i) {
        client.connect();
    }
}
