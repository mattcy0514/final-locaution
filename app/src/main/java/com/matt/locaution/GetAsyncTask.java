package com.matt.locaution;

import android.os.AsyncTask;

import com.matt.locaution.LocationListAdapter;
import com.matt.locaution.room.LocationDAO;
import com.matt.locaution.room.LocationEntity;

import java.util.List;

class GetAsyncTask extends AsyncTask<Void, Void, List<LocationEntity>> {
    private LocationDAO locationDAO;
    private LocationListAdapter mAdapter;

    GetAsyncTask(LocationDAO locationDAO, LocationListAdapter mAdapter) {
        this.locationDAO = locationDAO;
        this.mAdapter = mAdapter;
    }

    @Override
    protected List<LocationEntity> doInBackground(Void... voids) {
        return locationDAO.getAllLocations();
    }

    @Override
    protected void onPostExecute(List<LocationEntity> locationEntitiesList) {
        mAdapter.swapList(locationEntitiesList);
    }
}