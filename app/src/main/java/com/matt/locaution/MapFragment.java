package com.matt.locaution;

import android.content.Context;
import android.content.SharedPreferences;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;

import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.preference.PreferenceManager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.matt.locaution.room.LocationEntity;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class MapFragment extends Fragment {

    private View view;
    private TextView tv_map_long_val;
    private TextView tv_map_lati_val;
    private TextView tv_map_time;
    private Toolbar tb_map;

    private LocationEntity locationEntity;

    private PrefUtils prefUtils;
    private SharedPreferences mSharedPreferences;
    private final String MY_PREFS_NAME = "LOCAUTION_PREFS";

    public MapFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_map, container, false);
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);



        tv_map_long_val = view.findViewById(R.id.tv_map_long_val);
        tv_map_lati_val = view.findViewById(R.id.tv_map_lati_val);
        tv_map_time = view.findViewById(R.id.tv_map_time);
        tb_map = view.findViewById(R.id.tb_map);

        MapFragmentArgs args = MapFragmentArgs.fromBundle(getArguments());
        locationEntity = args.getLocationEntity();
        double latitude = locationEntity.getLocation_latitude();
        double longitude = locationEntity.getLocation_longitude();
        String time = locationEntity.getLocation_time();

        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        prefUtils = new PrefUtils(mSharedPreferences);
        float markerColor = prefUtils.getMarkerColor();
        String markerDetail = prefUtils.getMarkerDetail(getContext(), latitude, longitude);
        int zoomScale = prefUtils.getZoomScale();

        tv_map_lati_val.setText(getConvertedLocationValue(latitude));
        tv_map_long_val.setText(getConvertedLocationValue(longitude));
        tv_map_time.setText(time);


        final MapView mapView = view.findViewById(R.id.mv_map);
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {



                LatLng coordinates = new LatLng(latitude, longitude);
                googleMap.addMarker(new MarkerOptions().position(coordinates).title(time + " " + markerDetail)
                        .icon(BitmapDescriptorFactory.defaultMarker(markerColor)));
                googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(coordinates, zoomScale));
                // SO IMPORTANT
                mapView.onResume();
            }
        });

        tb_map.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NavController navController = Navigation.findNavController(view);
                navController.popBackStack();
            }
        });
    }

    private String getConvertedLocationValue(double value) {
        return String.format("%.4f...", value);
    }

}