package com.example.datenbankefuerprojekt.db.main.database;

import static android.os.Build.VERSION_CODES.R;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.example.datenbankefuerprojekt.R;

public class UebungAdapter extends ListAdapter<Uebung, UebungAdapter.UebungHolder> {
    private OnItemClickListener listener;

    public UebungAdapter() {
        super(DIFF_CALLBACK);
    }

    private static final DiffUtil.ItemCallback<Uebung> DIFF_CALLBACK = new DiffUtil.ItemCallback<Uebung>() {
        @Override
        public boolean areItemsTheSame(@NonNull Uebung oldItem, @NonNull Uebung newItem) {
            return oldItem.getId() == newItem.getId();
        }

        @Override
        public boolean areContentsTheSame(@NonNull Uebung oldItem, @NonNull Uebung newItem) {
            return oldItem.getTitel().equals(newItem.getTitel()) &&
                    oldItem.getBeschreibung().equals(newItem.getBeschreibung()) &&
                    oldItem.getPrioritaet() == newItem.getPrioritaet() &&
                    oldItem.getAnzahlDerWiederholungen() == newItem.getAnzahlDerWiederholungen();
        }
    };

    @NonNull
    @Override
    public UebungHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(com.example.datenbankefuerprojekt.R.layout.uebung_item, parent, false);
        return new UebungHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull UebungHolder holder, int position) {
        Uebung currentNote = getItem(position);
        holder.textViewTitel.setText(currentNote.getTitel());
        holder.textViewBeschreibung.setText(currentNote.getBeschreibung());
        holder.textViewPrioritaet.setText(String.valueOf(currentNote.getPrioritaet()));
        holder.textViewAnzahl.setText(String.valueOf(currentNote.getAnzahlDerWiederholungen()));
    }

    public Uebung getUebungAt(int position){
        return getItem(position);
    }

    class UebungHolder extends RecyclerView.ViewHolder{
        private TextView textViewTitel;
        private TextView textViewBeschreibung;
        private TextView textViewPrioritaet;
        private TextView textViewAnzahl;

        public UebungHolder(@NonNull View itemView){
            super(itemView);
            textViewTitel = itemView.findViewById(com.example.datenbankefuerprojekt.R.id.text_view_title);
            textViewBeschreibung = itemView.findViewById(com.example.datenbankefuerprojekt.R.id.text_view_description);
            textViewPrioritaet = itemView.findViewById(com.example.datenbankefuerprojekt.R.id.text_view_priority);
            textViewAnzahl = itemView.findViewById(com.example.datenbankefuerprojekt.R.id.text_view_counter);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int position = getAdapterPosition();
                    if (listener != null && position != RecyclerView.NO_POSITION){
                        listener.onItemClick(getItem(position));
                    }
                }
            });
        }
    }

    public interface OnItemClickListener{
        void onItemClick(Uebung uebung);
    }

    public void setOnClickListener(OnItemClickListener listener){
        this.listener = listener;
    }
}