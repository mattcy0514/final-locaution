package com.matt.locaution;

import android.Manifest;
import android.app.Notification;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;

import android.os.VibrationEffect;
import android.os.Vibrator;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.matt.locaution.room.LocationDAO;
import com.matt.locaution.room.LocationDatabase;
import com.matt.locaution.room.LocationEntity;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import static android.content.ContentValues.TAG;

public class ListFragment extends Fragment implements LocationListener, LocationListAdapter.OnItemClickListener, PopupMenu.OnMenuItemClickListener {

    private View view;
    private double currentLongitude;
    private double currentLatitude;
    private RecyclerView rv_location_list;
    private TextView tv_long_val;
    private TextView tv_lati_val;
    private TextView tv_time;
    private Button btn_save;
    private Toolbar tb_list;

    private Timer timer;

    private LocationDatabase mLocationDatabase;
    private LocationDAO mLocationDAO;

    private LocationListAdapter locationListAdapter;
    private LocationListener locationListener;
    private List<LocationEntity> locationDetailList = new ArrayList<>();

    private LocationManager lm;

    private static int PERMITTED = 777;

    final String[] permissions = {
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_list, container, false);
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        tv_lati_val = view.findViewById(R.id.tv_lati_val);
        tv_long_val = view.findViewById(R.id.tv_long_val);
        tv_time = view.findViewById(R.id.tv_list_time);
        btn_save = view.findViewById(R.id.btn_save);
        tb_list = view.findViewById(R.id.tb_list);

        rv_location_list = view.findViewById(R.id.rv_location_list);
        locationListAdapter = new LocationListAdapter(this, locationDetailList);

        rv_location_list.setLayoutManager(new GridLayoutManager(getActivity(), 2));
        rv_location_list.setAdapter(locationListAdapter);

        requestPermissions(permissions, PERMITTED);

        lm = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
        doRequestLocationUpdates(lm);

        timer = new Timer();
        doScheduleTimer(timer);

        mLocationDatabase = Room.databaseBuilder(getActivity(), LocationDatabase.class,
                "location-db").build();
        mLocationDAO = mLocationDatabase.getLocationDAO();
        new GetAsyncTask(mLocationDAO, locationListAdapter).execute();

        btn_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LocationEntity locationDetail = new LocationEntity(tv_time.getText().toString(),
                        currentLongitude, currentLatitude);
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        mLocationDAO.addNewLocation(locationDetail);
                        new GetAsyncTask(mLocationDAO, locationListAdapter).execute();
                        Vibrator v = (Vibrator) getActivity().getSystemService(Context.VIBRATOR_SERVICE);
                        v.vibrate(VibrationEffect.createOneShot(100, VibrationEffect.DEFAULT_AMPLITUDE));
                    }
                }).start();
            }
        });

        tb_list.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu popupMenu = new PopupMenu(getActivity(), v);
                MenuInflater inflater = popupMenu.getMenuInflater();
                inflater.inflate(R.menu.list_options, popupMenu.getMenu());
                popupMenu.setOnMenuItemClickListener(ListFragment.this);
                popupMenu.show();
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        doScheduleTimer(this.timer);
        doRequestLocationUpdates(this.lm);
        ((AppCompatActivity) getActivity()).getSupportActionBar().hide();
    }

    @Override
    public void onResume() {
        super.onResume();
        doScheduleTimer(this.timer);
        doRequestLocationUpdates(this.lm);
        ((AppCompatActivity) getActivity()).getSupportActionBar().hide();
    }

    @Override
    public void onPause() {
        super.onPause();
        doCancelTimer(this.timer);
        doRemoveLocationUpdates(lm);
    }

    @Override
    public void onStop() {
        super.onStop();
        doCancelTimer(this.timer);
        doRemoveLocationUpdates(lm);
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.setting:
                navigateToSetting();
                break;
            case R.id.route_record:
                navigateToRoute();
                break;
            case R.id.logout:
                firebaseLogout();
                getActivity().finish();
                break;
        }
        return false;
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onLocationChanged(@NonNull Location location) {
        if (getActivity() != null) {
            Toast.makeText(getActivity(), "Updated", Toast.LENGTH_SHORT).show();
            currentLongitude = location.getLongitude();
            currentLatitude = location.getLatitude();
            tv_long_val.setText(getConvertedLocationValue(currentLongitude));
            tv_lati_val.setText(getConvertedLocationValue(currentLatitude));
        }
    }

    @Override
    public void onProviderEnabled(@NonNull String provider) {
        if(LocationManager.GPS_PROVIDER.equals(provider)){
            Toast.makeText(getActivity(),"GPS on",Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onProviderDisabled(@NonNull String provider) {
        if(LocationManager.GPS_PROVIDER.equals(provider)){
            Toast.makeText(getActivity(),"GPS off",Toast.LENGTH_SHORT).show();
        }
    }

    private String getConvertedLocationValue(double value) {
        return String.format("%.2f...", value);
    }

    private void firebaseLogout() {
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        mAuth.signOut();
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        GoogleSignIn.getClient(getActivity(), gso).signOut().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(Task<Void> task) {
                Toast.makeText(getActivity(), "Logout", Toast.LENGTH_LONG).show();
            }
        });
    }

    private void navigateToRoute() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                LocationEntity[] locationRecord = (LocationEntity[]) mLocationDAO.getAllLocations().toArray(new LocationEntity[0]);
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        NavController navController = Navigation.findNavController(view);

                        ListFragmentDirections.ActionListFragmentToRouteFragment action =
                                ListFragmentDirections.actionListFragmentToRouteFragment(locationRecord);
                        navController.navigate(action);
                    }
                });
            }
        }).start();

    }

    private void navigateToSetting() {
        NavController navController = Navigation.findNavController(view);
        NavDirections action = ListFragmentDirections.actionListFragmentToSettingsFragment();
        navController.navigate(action);
    }

    private void doScheduleTimer(Timer timer) {
        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                if (getActivity() != null) {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            tv_time.setText(new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(new Date()));
                        }
                    });
                }
            }
        }, 0, 1000);
    }

    private void doCancelTimer(Timer timer) {
        if (timer != null) {
            timer.cancel();
        }
    }

    private void doRequestLocationUpdates(LocationManager locationManager) {
        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(), permissions, PERMITTED);
        }
        else {
            if (this.locationListener == null) {
                Toast.makeText(getActivity(), "Start To Update Locations", Toast.LENGTH_SHORT).show();
                this.locationListener = this;
            }
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 10, this.locationListener);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == PERMITTED) {
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(getActivity(), "Permitted", Toast.LENGTH_SHORT).show();
                doRequestLocationUpdates(lm);
            } else {
                Toast.makeText(getActivity(), "Unpermitted", Toast.LENGTH_SHORT).show();
                getActivity().finish();
            }
        }
    }

    private void doRemoveLocationUpdates(LocationManager locationManager) {
        locationManager.removeUpdates(this);
        this.locationListener = null;
    }

    @Override
    public void onItemClick(int position) {
        LocationEntity locationEntity = locationListAdapter.getmLocationDetailList().get(position);
        NavController navController = Navigation.findNavController(this.view);
        ListFragmentDirections.ActionListFragmentToMapFragment action =
                ListFragmentDirections.actionListFragmentToMapFragment(locationEntity);

        navController.navigate(action);
    }

    @Override
    public void onItemLongClick(int position) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                long id = locationListAdapter.getmLocationDetailList().get(position).getId();
                mLocationDAO.removeLocationById(id);
                new GetAsyncTask(mLocationDAO, locationListAdapter).execute();
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getActivity(), "DELETED", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }).start();
    }

}