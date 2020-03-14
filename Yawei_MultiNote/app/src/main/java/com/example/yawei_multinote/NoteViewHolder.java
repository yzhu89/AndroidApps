package com.example.yawei_multinote;

import android.view.View;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

class NoteViewHolder extends RecyclerView.ViewHolder{
    TextView title;
    TextView text;
    TextView date;

    NoteViewHolder(View view) {
        super(view);
        title = view.findViewById(R.id.title);
        text = view.findViewById(R.id.text);
        date = view.findViewById(R.id.date);
    }
}
