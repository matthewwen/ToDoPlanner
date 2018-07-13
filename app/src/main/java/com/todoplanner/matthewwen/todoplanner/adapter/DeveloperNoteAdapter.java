package com.todoplanner.matthewwen.todoplanner.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.todoplanner.matthewwen.todoplanner.R;
import com.todoplanner.matthewwen.todoplanner.objects.Note;

import java.util.ArrayList;

public class DeveloperNoteAdapter extends RecyclerView.Adapter<DeveloperNoteAdapter.ViewHolder> {

    private LayoutInflater inflater;
    private ArrayList<Note> notes;

    public DeveloperNoteAdapter(Context context, ArrayList<Note> notes){
        inflater = LayoutInflater.from(context);
        this.notes = notes;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.recycle_layout_developer_note_item, parent, false);
        return new ViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Note temp = notes.get(position);

        holder.idtv.setText("ID: " + Integer.toString(temp.getID()));
        holder.headingtv.setText("Heading: " + temp.getHeading());
        holder.bodytv.setText("Body: " + temp.getDetails());
    }

    @Override
    public int getItemCount() {
        return notes.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder{

        TextView idtv;
        TextView headingtv;
        TextView bodytv;

        ViewHolder(View itemView) {
            super(itemView);
            idtv = itemView.findViewById(R.id.developer_note_id_tv);
            headingtv = itemView.findViewById(R.id.developer_note_heading_tv);
            bodytv = itemView.findViewById(R.id.developer_note_body_tv);
        }
    }

    public void clear(){
        this.notes = new ArrayList<>();
        notifyDataSetChanged();
    }

    public void addAllNotes(ArrayList<Note> allThem){
        notes = allThem;
        notifyDataSetChanged();
    }
}
