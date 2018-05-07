package com.getparkit.parkit.Classes;

import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;

import com.getparkit.parkit.Activities.AsyncDrawerActivities.OwnerHomeScreenActivity;
import com.getparkit.parkit.Activities.HelperActivities.ErrorActivity;
import com.getparkit.parkit.R;
import com.getparkit.parkit.SQLite.Helper;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.List;

import io.swagger.client.api.ParkingSpaceApi;
import io.swagger.client.model.Spot;
import retrofit2.Call;
import retrofit2.Callback;

public class SpotAdapter extends RecyclerView.Adapter<SpotAdapter.ViewHolder> {
    private List<Spot> mDataset;
    private UserAccess ua;

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public static class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public View spotView;
        public ViewHolder(View v) {
            super(v);
            spotView = v;
        }
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public SpotAdapter(List<Spot> myDataset) {
        mDataset = myDataset;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public SpotAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                   int viewType) {
        // create a new view
        View spotView = LayoutInflater.from(parent.getContext()).inflate(
                R.layout.spot_card, parent,false);
        ViewHolder vh = new ViewHolder(spotView);
        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        final Spot spot = mDataset.get(position);
        TextView spotNo = holder.spotView.findViewById(R.id.spot_title);
        TextView spotDescription = holder.spotView.findViewById(R.id.spot_description);
        Switch spotStatus = holder.spotView.findViewById(R.id.spot_status);

        NumberFormat nf = DecimalFormat.getInstance();
        nf.setMaximumFractionDigits(0);
        String spotStr = nf.format(spot.getZone());
        spotNo.setText("Spot " + spotStr);
        spotDescription.setText(spot.getDescription());

        if (spot.getStatus() > 0 ) {
            spotStatus.setChecked(true);
            spotStatus.setText("Online");
        } else  {
            spotStatus.setText("Offline");
            spotStatus.setTextColor(holder.spotView.getResources().getColor(R.color.colorRed));
        }

        spotStatus.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(final CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    buttonView.setText("Online");
                    buttonView.setTextColor(holder.spotView.getResources().getColor(R.color.colorPrimary));

                    Helper helper = new Helper(holder.spotView.getContext());
                    ua = helper.searchUserAccess();

                    // start async loading icon..
                    AuthenticatedApiClient client = new AuthenticatedApiClient(ua.getAccessToken());
                    // Get the parking spots of the parking space, other show text asking user to add.
                    ParkingSpaceApi parkingSpaceApi = client.createService(ParkingSpaceApi.class);

                    spot.setStatus(0.0);
                    Call<Spot> updateSpotCall = parkingSpaceApi.parkingSpacePrototypeUpdateByIdSpots(spot.getParkingSpaceId().toString(), spot.getId().toString(), spot);

                    updateSpotCall.enqueue(new Callback<Spot>() {
                        @Override
                        public void onResponse(Call<Spot> call, retrofit2.Response<Spot> response) {
                            // Handle async error
//                            if (client.handleError(response, OwnerHomeScreenActivity.this, OwnerHomeScreenActivity.class, ua)) {
//                                return;
//                            }
                        }
                        @Override
                        public void onFailure(Call<Spot> call, Throwable t) {
                            Log.d("SERVER_ERROR", "Error dealing with the response: " + t.getMessage());
                            buttonView.setText("Offline");
                            buttonView.setTextColor(holder.spotView.getResources().getColor(R.color.colorRed));
                        }
                    });
                } else {
                    buttonView.setText("Offline");
                    buttonView.setTextColor(holder.spotView.getResources().getColor(R.color.colorRed));
                }
            }
        });

    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mDataset.size();
    }
}