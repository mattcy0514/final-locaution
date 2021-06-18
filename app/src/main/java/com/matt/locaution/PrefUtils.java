package com.matt.locaution;

import android.content.Context;
import android.content.SharedPreferences;
import android.location.Address;
import android.location.Geocoder;
import android.util.Log;

import androidx.fragment.app.Fragment;
import androidx.preference.PreferenceManager;

import com.google.android.gms.maps.model.BitmapDescriptorFactory;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class PrefUtils {
    private String markerColor;
    private String markerDetail;
    private int zoomScale;
    private SharedPreferences mSharedPreferences;
    private final String MARKER_COLOR = "marker_color";
    private final String MARKER_DETAIL = "marker_detail";
    private final String ZOOM_SCALE = "zoom_scale";
    private final String DEFAULT_COLOR = "HUE_RED";
    private final String DEFAULT_DETAIL = "Address";
    private final int DEFAULT_SCALE = 8;

    public PrefUtils(SharedPreferences sharedPreferences) {
        if (sharedPreferences == null) {
            return;
        }
        this.mSharedPreferences = sharedPreferences;
        this.markerColor = mSharedPreferences.getString(MARKER_COLOR, DEFAULT_COLOR);
        this.markerDetail = mSharedPreferences.getString(MARKER_DETAIL, DEFAULT_DETAIL);
        this.zoomScale = mSharedPreferences.getInt(ZOOM_SCALE, DEFAULT_SCALE);
    }

    public float getMarkerColor() {
        switch (markerColor) {
            case "HUE_RED" :
                return BitmapDescriptorFactory.HUE_RED;
            case "HUE_GREEN" :
                return BitmapDescriptorFactory.HUE_GREEN;
            case "HUE_BLUE" :
                return BitmapDescriptorFactory.HUE_BLUE;
            case "HUE_YELLOW" :
                return BitmapDescriptorFactory.HUE_YELLOW;
            default:
                return BitmapDescriptorFactory.HUE_RED;
        }
    }

    public String getMarkerDetail(Context context, double latitude, double longitude) {
        Geocoder geocoder = new Geocoder(context, Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocation(latitude,longitude, 1);
            switch (markerDetail) {
                case "Address" :
                    return addresses.get(0).getAddressLine(0);
                case "City" :
                    return addresses.get(0).getLocality();
                case "State" :
                    return addresses.get(0).getAdminArea();
                case "Country" :
                    return addresses.get(0).getCountryName();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "NULL";
    }

    public int getZoomScale() {
        return zoomScale;
    }

}
