package com.example.myproject.CustomClasses;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.myproject.R;

import java.util.ArrayList;

public class MyClaimAdapter extends RecyclerView.Adapter<MyClaimAdapter.ViewHolder>{
    private ArrayList<MyClaimData> claimdata;

    // RecyclerView recyclerView;
    public MyClaimAdapter(ArrayList<MyClaimData> claimdata) {
        this.claimdata = claimdata;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View listItem= layoutInflater.inflate(R.layout.list_item, parent, false);
        ViewHolder viewHolder = new ViewHolder(listItem);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final MyClaimData myClaimData = claimdata.get(position);
        holder.idTextView.setText(myClaimData.getClaimId());
        holder.dateTextView.setText(myClaimData.getClaimDate());
        holder.descriptionTextView.setText(myClaimData.getDescription());
    }


    @Override
    public int getItemCount() {
        return claimdata.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView idTextView;
        public TextView dateTextView;
        public TextView descriptionTextView;
        public ViewHolder(View itemView) {
            super(itemView);
            this.idTextView = (TextView) itemView.findViewById(R.id.idTextView);
            this.dateTextView = (TextView) itemView.findViewById(R.id.dateTextView);
            this.descriptionTextView = (TextView) itemView.findViewById(R.id.descriptionTextView);
        }
    }
}
