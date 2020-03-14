package com.example.stockwatch;

import android.view.View;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

public class StockHolder extends RecyclerView.ViewHolder{
    public TextView name;
    public TextView symbol;
    public TextView price;
    public TextView change;

    public StockHolder(View itemView) {
        super(itemView);
        name = (TextView) itemView.findViewById(R.id.company);
        symbol = (TextView) itemView.findViewById(R.id.symbol);
        change = (TextView) itemView.findViewById(R.id.change);
        price = (TextView) itemView.findViewById(R.id.latestPrice);
    }
}
