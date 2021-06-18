package com.matt.locaution;

import android.content.SharedPreferences;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;

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
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.matt.locaution.room.LocationEntity;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class RouteFragment extends Fragment {

    private View view;
    private TextView tv_route_start_val;
    private TextView tv_route_end_val;
    private androidx.appcompat.widget.Toolbar tb_route;
    private PrefUtils prefUtils;
    private SharedPreferences mSharedPreferences;

    public RouteFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_route, container, false);
        return view;
    }


    private String getConvertedLocationTime(String time) {
        String[] time_array = time.split("\\s");
        return time_array[0];
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        tv_route_start_val = view.findViewById(R.id.tv_route_start_val);
        tv_route_end_val = view.findViewById(R.id.tv_route_end_val);
        tb_route = view.findViewById(R.id.tb_route);

        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        prefUtils = new PrefUtils(mSharedPreferences);
        float markerColor = prefUtils.getMarkerColor();

        int zoomScale = prefUtils.getZoomScale();

        // Initialize view with Safe Argumentes
        RouteFragmentArgs args = RouteFragmentArgs.fromBundle(getArguments());
        LocationEntity[] locationRecord = args.getLocationRecords();

        if (locationRecord.length == 0 || locationRecord.equals(null)) {
            Log.d("onViewCreated: ", String.valueOf(locationRecord.length));
        }
        else {
            String routeStartTime = getConvertedLocationTime(locationRecord[0].getLocation_time());
            String routeEndTime = getConvertedLocationTime(locationRecord[locationRecord.length - 1].getLocation_time());
            tv_route_start_val.setText(routeStartTime);
            tv_route_end_val.setText(routeEndTime);

            final MapView mapView = view.findViewById(R.id.mv_route_map);
            mapView.onCreate(savedInstanceState);

            mapView.getMapAsync(new OnMapReadyCallback() {
                @Override
                public void onMapReady(GoogleMap googleMap) {
                    LatLngBounds.Builder bounds = new LatLngBounds.Builder();
                    for (LocationEntity locationEntity : locationRecord) {
                        double latitude = locationEntity.getLocation_latitude();
                        double longitude = locationEntity.getLocation_longitude();
                        String time = locationEntity.getLocation_time();
                        String markerDetail = prefUtils.getMarkerDetail(getContext(), latitude, longitude);

                        LatLng coordinates = new LatLng(latitude, longitude);
                        bounds.include(coordinates);
                        googleMap.addMarker(new MarkerOptions().position(coordinates).title(time + " " + markerDetail)
                        .icon(BitmapDescriptorFactory.defaultMarker(markerColor)));
                    }
                    LatLngBounds latLngBounds = bounds.build();

                    googleMap.animateCamera(CameraUpdateFactory.newLatLngBounds(latLngBounds, 50));

                    // SO IMPORTANT
                    mapView.onResume();
                }
            });
        }
        tb_route.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navigateBack();
            }
        });
    }
    private void navigateBack() {
        NavController navController = Navigation.findNavController(view);
        navController.popBackStack();
    }

}