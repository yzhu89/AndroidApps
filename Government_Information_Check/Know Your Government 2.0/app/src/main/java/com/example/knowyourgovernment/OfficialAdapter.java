package com.example.knowyourgovernment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class OfficialAdapter extends RecyclerView.Adapter<OfficialViewHolder>{
    private static final String TAG = "OfficialAdapter";
    private List<Official> officialList;
    private MainActivity mainActivity;

    public OfficialAdapter(List<Official> oList, MainActivity ma){
        this.officialList = oList;
        mainActivity = ma;
    }

    @NonNull
    @Override
    public OfficialViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.official_list_row, parent, false);
        itemView.setOnClickListener((View.OnClickListener) mainActivity);

        return new OfficialViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull OfficialViewHolder holder, int position) {
        Official official = officialList.get(position);

        if (official.getParty() == null){
            holder.name.setText(official.getName());
        } else {
            holder.name.setText(official.getName()+'('+official.getParty()+')');
        }
        holder.official.setText(official.getOfficialName());
    }

    @Override
    public int getItemCount() {
        return officialList.size();
    }
}
