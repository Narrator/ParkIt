package com.getparkit.parkit.Classes;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Switch;
import android.widget.TextView;

import com.getparkit.parkit.R;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.List;

import io.swagger.client.model.Spot;

public class SpotAdapter extends RecyclerView.Adapter<SpotAdapter.ViewHolder> {
    private List<Spot> mDataset;

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
    public void onBindViewHolder(ViewHolder holder, int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        Spot spot = mDataset.get(position);
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

    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mDataset.size();
    }
}