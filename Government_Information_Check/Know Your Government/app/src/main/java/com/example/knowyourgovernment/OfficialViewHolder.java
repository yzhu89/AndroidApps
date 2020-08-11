package com.example.knowyourgovernment;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class OfficialViewHolder extends RecyclerView.ViewHolder{
    TextView official;
    TextView name;

    public OfficialViewHolder(@NonNull View itemView) {
        super(itemView);
        official = itemView.findViewById(R.id.office_row);
        name = itemView.findViewById(R.id.name_row);
    }
}
