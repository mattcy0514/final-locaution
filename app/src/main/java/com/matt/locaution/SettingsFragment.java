package com.matt.locaution;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceManager;
import androidx.preference.PreferenceScreen;

import java.util.Map;
import java.util.Set;

public class SettingsFragment extends PreferenceFragmentCompat implements SharedPreferences.OnSharedPreferenceChangeListener {

    private ActionBar actionBar;
    private SharedPreferences mSharedPreferences;
    private final String MY_PREFS_NAME = "LOCAUTION_PREFS";

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.root_preferences, rootKey);

        actionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.show();

        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        mSharedPreferences.registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        Log.d("onSharedPreferenceChanged: ", "IN");
        if (key.equals(getString(R.string.marker_color))) {
            mSharedPreferences.edit().putString(key, sharedPreferences.getString(key, getString(R.string.marker_default_color)));
            mSharedPreferences.edit().commit();
        } else if (key.equals(getString(R.string.marker_detail))) {
            mSharedPreferences.edit().putString(key, sharedPreferences.getString(key, getString(R.string.marker_default_detail)));
            mSharedPreferences.edit().commit();
            Log.d("onSharedPreferenceChanged: ", "DETECTED DETAIL" + getString(R.string.marker_default_detail));
        } else if (key.equals(getString(R.string.zoom_scale))) {
            mSharedPreferences.edit().putInt(key, sharedPreferences.getInt(key, 8));
            mSharedPreferences.edit().commit();
            Log.d("onSharedPreferenceChanged: ", "DETECTED SCALE" + mSharedPreferences.getInt(key, 8));
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mSharedPreferences.unregisterOnSharedPreferenceChangeListener(this);
    }
}