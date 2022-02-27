package com.example.datenbankefuerprojekt.db.main.database.fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.example.datenbankefuerprojekt.R;
/**
 * @author Abdurrahman Azattemür
 */
public class FragmentAdapter extends ListAdapter<Fragment, FragmentAdapter.FragmentHolder> {
    private OnItemClickListener listener;

    public FragmentAdapter(){
        super(DIFF_CALLBACK);
    }

    private static final DiffUtil.ItemCallback<Fragment> DIFF_CALLBACK = new DiffUtil.ItemCallback<Fragment>() {
        @Override
        public boolean areItemsTheSame(@NonNull Fragment oldItem, @NonNull Fragment newItem) {
            return oldItem.getFragmentId() == newItem.getFragmentId();
        }

        @Override
        public boolean areContentsTheSame(@NonNull Fragment oldItem, @NonNull Fragment newItem) {
            return oldItem.getTitelFragment().equals(newItem.getTitelFragment()) &&
                    oldItem.getAnzahlWiederholungenFragment() == newItem.getAnzahlWiederholungenFragment() &&
                    oldItem.getAusAtmenZeit() == newItem.getAusAtmenZeit() &&
                    oldItem.getAusLuftanhaltZeit() == newItem.getAusLuftanhaltZeit() &&
                    oldItem.getEinAtmenZeit() == newItem.getEinAtmenZeit() &&
                    oldItem.getEinLuftanhaltZeit() == newItem.getEinLuftanhaltZeit();
        }
    };

    @NonNull
    @Override
    public FragmentHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_item, parent, false);
        return new FragmentHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull FragmentHolder holder, int position) {
        Fragment currentNote = getItem(position);
        holder.textViewTitel.setText(currentNote.getTitelFragment());
        holder.textViewBeschreibung.setText("Einatmen: " + currentNote.getEinAtmenZeit() + "\t\tAnhalten: " + currentNote.getEinLuftanhaltZeit()
         + "\nAusatmen: " + currentNote.getAusAtmenZeit() + "\t\tAushalten: " + currentNote.getAusLuftanhaltZeit() + "\t\t\tWdh: " + currentNote.getAnzahlWiederholungenFragment());
        holder.textViewPrioritaet.setText(String.valueOf(currentNote.getPrioritaetFragment()));
    }

    public Fragment getFragmentAt(int position){
        return getItem(position);
    }

    class FragmentHolder extends RecyclerView.ViewHolder{
        private TextView textViewTitel;
        private TextView textViewBeschreibung;
        private TextView textViewPrioritaet;

        public FragmentHolder(@NonNull View itemView){
            super(itemView);
            textViewTitel = itemView.findViewById(R.id.text_view_title_fragment);
            textViewBeschreibung = itemView.findViewById(R.id.text_view_description_fragment);
            textViewPrioritaet = itemView.findViewById(R.id.text_view_priority_fragment);

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
        void onItemClick(Fragment fragment);
    }

    public void setOnClickListener(FragmentAdapter.OnItemClickListener listener){
        this.listener = listener;
    }

}
