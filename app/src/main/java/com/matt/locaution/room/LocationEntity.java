package com.matt.locaution.room;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;


@Entity(tableName = "location")
public class LocationEntity implements Parcelable {

    @PrimaryKey(autoGenerate = true)
    @NonNull
    @ColumnInfo(name = "id")
    public long id;

    @ColumnInfo(name = "location_time")
    public String location_time;

    @ColumnInfo(name = "location_longitude")
    public double location_longitude;

    @ColumnInfo(name = "location_latitude")
    public double location_latitude;

    public LocationEntity(String location_time, double location_longitude, double location_latitude) {
        this.location_time = location_time;
        this.location_longitude = location_longitude;
        this.location_latitude = location_latitude;
    }

    protected LocationEntity(Parcel in) {
        id = in.readLong();
        location_time = in.readString();
        location_longitude = in.readDouble();
        location_latitude = in.readDouble();
    }

    public static final Creator<LocationEntity> CREATOR = new Creator<LocationEntity>() {
        @Override
        public LocationEntity createFromParcel(Parcel in) {
            return new LocationEntity(in);
        }

        @Override
        public LocationEntity[] newArray(int size) {
            return new LocationEntity[size];
        }
    };

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getLocation_time() {
        return location_time;
    }

    public void setLocation_time(String location_time) {
        this.location_time = location_time;
    }

    public double getLocation_longitude() {
        return location_longitude;
    }

    public void setLocation_longitude(double location_longitude) {
        this.location_longitude = location_longitude;
    }

    public double getLocation_latitude() {
        return location_latitude;
    }

    public void setLocation_latitude(double location_latitude) {
        this.location_latitude = location_latitude;
    }

    @Override
    public String toString() {
        return "LocationEntity{" +
                "id=" + id +
                ", location_time='" + location_time + '\'' +
                ", location_longitude=" + location_longitude +
                ", location_latitude=" + location_latitude +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(id);
        dest.writeString(location_time);
        dest.writeDouble(location_longitude);
        dest.writeDouble(location_latitude);
    }
}
