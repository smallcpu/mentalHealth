package com.example.gabenator.mentalstealth;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

/**
 * Created by Gabenator on 10/28/2017.
 */


public class MyViewHolder extends RecyclerView.ViewHolder {

    public TextView rating;
    public TextView textContent;
    public TextView textDate;
    public TextView textNumber;

    public MyViewHolder(View view) {
        super(view);
        rating = (TextView) view.findViewById(R.id.textRating);
        textContent = (TextView) view.findViewById(R.id.textContent);
        textDate = (TextView) view.findViewById(R.id.textDate);
        textNumber = (TextView) view.findViewById(R.id.phoneNumber);
    }
}
