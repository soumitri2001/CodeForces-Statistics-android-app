package com.example.cfstats;

import android.app.Activity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;

public class ContestAdapter extends ArrayAdapter<ContestListView>
{
    private static final String LOG_TAG = ContestAdapter.class.getSimpleName();

    public ContestAdapter(Activity context, ArrayList<ContestListView> list)
    {
        super(context,0,list);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View itemView=convertView;
        if(itemView == null)
        {
            itemView= LayoutInflater.from(getContext()).
                    inflate(R.layout.list_item_layout,parent,false);
        }
        ContestListView currentUser = getItem(position);

        TextView nameContest = (TextView)itemView.findViewById(R.id.nameContest);
        TextView duration = (TextView)itemView.findViewById(R.id.duration);
        TextView startTime = (TextView)itemView.findViewById(R.id.startTime);

        assert currentUser != null;

        nameContest.setText(currentUser.getName());
        duration.setText(currentUser.getDuration());
        startTime.setText(currentUser.getStart());

        Log.d("Adapter set up!", "");

        return itemView;
    }
}
