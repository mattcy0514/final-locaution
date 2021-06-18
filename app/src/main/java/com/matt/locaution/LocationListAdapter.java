package com.matt.locaution;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.matt.locaution.room.LocationEntity;

import java.util.List;

public class LocationListAdapter extends RecyclerView.Adapter<LocationListAdapter.ViewHolder> {

    interface OnItemClickListener {
        void onItemClick(int position);
        void onItemLongClick(int position);
    }

    final private OnItemClickListener mOnItemClickListener;
    private List<LocationEntity> mLocationDetailList;
    
    public LocationListAdapter(OnItemClickListener mOnItemClickListener, List<LocationEntity> locationDetailList) {
        this.mOnItemClickListener = mOnItemClickListener;
        this.mLocationDetailList = locationDetailList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.location_layout, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        LocationEntity locationEntity = mLocationDetailList.get(position);

        holder.itemView.setOnClickListener(holder);
        holder.itemView.setOnLongClickListener(holder);

        holder.getTv_ent_lati_val().setText(getConvertedLocationValue(locationEntity.getLocation_latitude()));
        holder.getTv_ent_long_val().setText(getConvertedLocationValue(locationEntity.getLocation_longitude()));
        holder.getTv_ent_time().setText(locationEntity.getLocation_time());
    }

    @Override
    public int getItemCount() {
        return mLocationDetailList.size();         
    }

    public List<LocationEntity> getmLocationDetailList() {
        return mLocationDetailList;
    }

    public void swapList(List<LocationEntity> newLocationEntitiesList) {
        this.mLocationDetailList = newLocationEntitiesList;
        this.notifyDataSetChanged();
    }

    private String getConvertedLocationValue(double value) {
        return String.format("%.2f...", value);
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {
        private TextView tv_ent_lati_val;
        private TextView tv_ent_long_val;
        private TextView tv_ent_time;
        private CardView cv_location;

        public ViewHolder(View itemView) {
            super(itemView);
            tv_ent_lati_val = itemView.findViewById(R.id.tv_ent_lati_val);
            tv_ent_long_val = itemView.findViewById(R.id.tv_ent_long_val);
            tv_ent_time = itemView.findViewById(R.id.tv_ent_time);
            cv_location = itemView.findViewById(R.id.cv_location);
        }

        public TextView getTv_ent_long_val() {
            return tv_ent_long_val;
        }

        public TextView getTv_ent_lati_val() {
            return tv_ent_lati_val;
        }

        public TextView getTv_ent_time() {
            return tv_ent_time;
        }

        @Override
        public void onClick(View v) {
            int wrapperPosition = getAdapterPosition();
            mOnItemClickListener.onItemClick(wrapperPosition);
        }

        @Override
        public boolean onLongClick(View v) {
            int wrapperPosition = getAdapterPosition();
            mOnItemClickListener.onItemLongClick(wrapperPosition);
            return true;
        }
    }

}
