package com.example.yawei_multinote;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class NoteAdapter extends RecyclerView.Adapter<NoteViewHolder>{
    private static final String TAG = "NoteAdapter";
    private List<Note> noteList;
    private MainActivity mainAct;

    public NoteAdapter(List<Note> nList, MainActivity ma){
        this.noteList = nList;
        mainAct = ma;
    }

    @NonNull
    @Override
    public NoteViewHolder onCreateViewHolder(@NonNull final ViewGroup parent, int viewType){
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.note_list_row, parent, false);
        itemView.setOnClickListener(mainAct);
        itemView.setOnLongClickListener(mainAct);
        return new NoteViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull NoteViewHolder holder, int position){
        Note note = noteList.get(position);

        holder.title.setText(note.getTitle());
        holder.text.setText(note.getText());
        holder.date.setText(note.getLastUpdateTime());
    }

    @Override
    public int getItemCount(){
        return noteList.size();
    }
}
