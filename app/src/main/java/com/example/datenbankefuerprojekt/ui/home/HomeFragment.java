package com.example.datenbankefuerprojekt.ui.home;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.datenbankefuerprojekt.R;
import com.example.datenbankefuerprojekt.databinding.FragmentHomeBinding;
import com.example.datenbankefuerprojekt.db.main.database.uebung.Uebung;
import com.example.datenbankefuerprojekt.db.main.database.uebung.UebungAdapter;

public class HomeFragment extends Fragment {
    public static final String EXTRA_TITEL = "com.example.datenbankefuerprojekt.ui.home.TITEL";
    public static final String EXTRA_ID = "com.example.datenbankefuerprojekt.ui.home.ID";
    public static final String EXTRA_DESC = "com.example.datenbankefuerprojekt.ui.home.DESC";
    public static final String EXTRA_PRIO = "com.example.datenbankefuerprojekt.ui.home.PRIO";
    public static final String EXTRA_COUNT = "com.example.datenbankefuerprojekt.ui.home.COUNT";
    public static final String EXTRA_USE_SECONDS = "com.example.datenbankefuerprojekt.ui.home.USE_SECONDS";
    public static final String EXTRA_SECONDS = "com.example.datenbankefuerprojekt.ui.home.SECONDS";
    public static final String EXTRA_SPINNER_POSITION = "com.example.datenbankefuerprojekt.ui.home.SPINNER_POSITION";
    private HomeViewModel homeViewModel;
    private FragmentHomeBinding binding;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        homeViewModel = new ViewModelProvider(requireActivity()).get(HomeViewModel.class);

        setHasOptionsMenu(true);
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        binding.buttonAddNote.setOnClickListener(view -> {
            Navigation.findNavController(root).navigate(R.id.action_nav_home_to_nav_home_add_edit_uebung);
        });

        binding.recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        binding.recyclerView.setHasFixedSize(true);

        final UebungAdapter adapter = new UebungAdapter();
        binding.recyclerView.setAdapter(adapter);



        homeViewModel.getAllUebung().observe(this, uebungs ->
                adapter.submitList(uebungs));

        /**
         * dieses swipen
         */

        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0,ItemTouchHelper.LEFT|ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setTitle(R.string.delete_uebung_dialog_title)
                        .setMessage(R.string.delete_uebung_dialog_content);
                builder.setPositiveButton(R.string.delete_uebung_dialog_positive_button_label, (dialogInterface, i) -> {
                    homeViewModel.delete(adapter.getUebungAt(viewHolder.getAdapterPosition()));
                    Toast.makeText(getActivity(), R.string.delete_uebung_positive_button_message, Toast.LENGTH_SHORT).show();
                });
                builder.setNegativeButton(R.string.delete_uebung_negative_button_label, ((dialogInterface, i) -> {
                    adapter.notifyItemChanged(viewHolder.getAdapterPosition());
                }));
                builder.setOnCancelListener(dialogInterface -> adapter.notifyItemChanged(viewHolder.getAdapterPosition()));

                AlertDialog dialog = builder.create();
                dialog.show();
            }
        }).attachToRecyclerView(binding.recyclerView);

        adapter.setEditButtonClickListener(uebung -> {
            //Toast.makeText(getActivity(), bundle.toString(), Toast.LENGTH_LONG).show();
            Navigation.findNavController(root).navigate(R.id.action_nav_home_to_nav_home_add_edit_uebung, prepareUebungBundle(uebung));
        });

        adapter.setOnItemClickListener(uebung -> {
            homeViewModel.getAllFragmentsOfUebung(uebung.getId()).observe(requireActivity(), alleFragmentsOfCurrentUebung ->
                    homeViewModel.setAllFragmentsOfCurrentUebung(alleFragmentsOfCurrentUebung));

            if(uebung.getAnimationSpinnerPosition() == 1)
                Navigation.findNavController(root).navigate(R.id.action_nav_home_to_nav_home_progress_bar_fragment, prepareUebungBundle(uebung));
            if(uebung.getAnimationSpinnerPosition() == 2)
                Navigation.findNavController(root).navigate(R.id.action_nav_home_to_nav_home_animation_fragment, prepareUebungBundle(uebung));
            //hier nach animations fragment navigieren
            //if abfrage, wenn progressbar, dann progressbarFragment, sonst AnimationFragment
        });
        return root;
    }
    /**
     * @author Maximilian Jaesch
     */
    public void deleteAllUebungen(){
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage(R.string.delete_all_uebung_dialog_content);
        builder.setPositiveButton(R.string.delete_all_uebung_dialog_positive_button_label, (dialogInterface, i) -> {
            homeViewModel.deleteAllUebung();
            Toast.makeText(getActivity(), R.string.delete_all_uebung_dialog_positive_message, Toast.LENGTH_SHORT).show();
        });
        builder.setNegativeButton(R.string.delete_uebung_negative_button_label, null);

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.home_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.delete_all_uebung:
                deleteAllUebungen();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
    /**
     * @author Maximilian Jaesch, Abdurrahman Azattemür
     */
    private Bundle prepareUebungBundle(Uebung uebung){
        Bundle bundle = new Bundle();
        bundle.putInt(EXTRA_ID, uebung.getId());
        bundle.putString(EXTRA_TITEL, uebung.getTitel());
        bundle.putString(EXTRA_DESC, uebung.getBeschreibung());
        bundle.putInt(EXTRA_PRIO, uebung.getPrioritaet());
        bundle.putInt(EXTRA_COUNT, uebung.getAnzahlDerWiederholungen());
        bundle.putBoolean(EXTRA_USE_SECONDS, uebung.getUseTimed());
        bundle.putInt(EXTRA_SECONDS, uebung.getTimeInSeconds());
        bundle.putInt(EXTRA_SPINNER_POSITION, uebung.getAnimationSpinnerPosition());

        return bundle;
    }
}
