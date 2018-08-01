package com.kychow.jayjoska;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.kychow.jayjoska.models.Place;

import java.text.DecimalFormat;
import java.util.ArrayList;

/**
 * Created by Karena Chow on 7/20/18.
 */
public class ItineraryAdapter extends RecyclerView.Adapter<ItineraryAdapter.ViewHolder> {

    public static final DecimalFormat df = new DecimalFormat( "#.00" );
    private ArrayList<Place> mItinerary;
    private Context context;

    public ItineraryAdapter(ArrayList<Place> itinerary) {
        mItinerary = itinerary;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View recView = inflater.inflate(R.layout.item_itinerary, parent, false);
        ViewHolder viewHolder = new ViewHolder(recView, new CustomEditTextListener());
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Place place = mItinerary.get(position);
        holder.mName.setText(place.getName());
        holder.mCustomEditTextListener.updatePosition(position);

        String roundedDistance = df.format(place.getDistance());
        holder.mDistance.setText(String.format("%s miles", roundedDistance));
        holder.mTime.setText(String.valueOf(place.getTime()));
        holder.mCost.setText(String.format("$%s", String.valueOf(place.getCost())));

        RequestOptions options = new RequestOptions();
        Glide.with(context)
                .load(place.getImgURL())
                .apply(options.circleCrop())
                .into(holder.mImage);
    }

    @Override
    public int getItemCount() {
        return mItinerary.size();
    }

    public int grabTime() {
        int time = 0;
        for (Place place : mItinerary) {
            time += place.getTime();
        }
        return time;
    }

    public interface OnUpdateTimeListener {
        public void updateTime(int i);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private ImageView mImage;
        private TextView mDistance;
        private TextView mName;
        private TextView mTime;
        private TextView mCost;
        private CustomEditTextListener mCustomEditTextListener;

        public ViewHolder(View itemView, CustomEditTextListener customEditTextListener) {
            super(itemView);

            mImage = itemView.findViewById(R.id.ivItineraryPicture);
            mDistance = itemView.findViewById(R.id.tvItineraryDistance);
            mName = itemView.findViewById(R.id.tvItineraryTitle);
            mTime = itemView.findViewById(R.id.tvItineraryTime);
            mCost = itemView.findViewById(R.id.tvItineraryCost);
            mCustomEditTextListener = customEditTextListener;

            mTime.addTextChangedListener(mCustomEditTextListener);
        }
    }

    // Taken from https://stackoverflow.com/questions/31844373/saving-edittext-content-in-recyclerview
    public class CustomEditTextListener implements TextWatcher {
        private int position;
        private OnUpdateTimeListener mListener;


        public void updatePosition(int position) {
            this.position = position;
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            if (s.length() != 0) {
                mItinerary.get(position).setTime(Integer.parseInt(s.toString()));
            } else {
                mItinerary.get(position).setTime(0);
            }
            try {
                mListener = (OnUpdateTimeListener) context;
            } catch (ClassCastException e) {
                throw new ClassCastException(context.toString()
                        + " must implement OnUpdateTimeListener");
            }
            mListener.updateTime(grabTime());
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    }
}

