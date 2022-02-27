package com.example.datenbankefuerprojekt.db.main.database.uebung;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.example.datenbankefuerprojekt.R;
/**
 * @author Abdurrahman Azattemür, Maximilian Jaesch
 * <p>Diese Klasse ist dazu da, die Items in der Recyclerview zu lokalisieren und die Werte richtig hinzuzufügen.</p>
 */
public class UebungAdapter extends ListAdapter<Uebung, UebungAdapter.UebungHolder> {
    private OnItemClickListener itemClickListener;
    private OnEditButtonClickListener editButtonClickListener;

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
        private Button editButton;

        public UebungHolder(@NonNull View itemView){
            super(itemView);
            textViewTitel = itemView.findViewById(com.example.datenbankefuerprojekt.R.id.text_view_title);
            textViewBeschreibung = itemView.findViewById(com.example.datenbankefuerprojekt.R.id.text_view_description);
            textViewPrioritaet = itemView.findViewById(com.example.datenbankefuerprojekt.R.id.text_view_priority);
            textViewAnzahl = itemView.findViewById(com.example.datenbankefuerprojekt.R.id.text_view_counter);
            editButton = itemView.findViewById(R.id.button_edit);

            itemView.setOnClickListener(view -> {
                int position = getAdapterPosition();
                if (itemClickListener != null && position != RecyclerView.NO_POSITION){
                    itemClickListener.onItemClick(getItem(position));
                }
            });
            editButton.setOnClickListener(view -> {
                int position = getAdapterPosition();
                if(editButtonClickListener != null && position != RecyclerView.NO_POSITION){
                    editButtonClickListener.onEditButtonClick(getItem(position));
                }
            });
        }
    }

    public interface OnItemClickListener{
        void onItemClick(Uebung uebung);
    }

    public interface OnEditButtonClickListener{
        void onEditButtonClick(Uebung uebung);
    }

    public void setOnItemClickListener(OnItemClickListener listener){
        this.itemClickListener = listener;
    }

    public void setEditButtonClickListener(OnEditButtonClickListener listener){
        this.editButtonClickListener = listener;
    }
}
