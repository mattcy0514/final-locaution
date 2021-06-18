package com.matt.locaution.room;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface LocationDAO {
    @Insert
    void addNewLocation(LocationEntity locationEntity);

    @Query("SELECT * FROM location ORDER BY id DESC")
    List<LocationEntity> getAllLocations();

    @Query("DELETE FROM location WHERE id = :id")
    void removeLocationById(long id);
}
