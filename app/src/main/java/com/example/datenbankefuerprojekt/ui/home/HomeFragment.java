package com.example.datenbankefuerprojekt.ui.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.datenbankefuerprojekt.R;
import com.example.datenbankefuerprojekt.databinding.FragmentHomeBinding;
import com.example.datenbankefuerprojekt.db.main.database.Uebung;
import com.example.datenbankefuerprojekt.db.main.database.UebungAdapter;
import com.example.datenbankefuerprojekt.ui.slideshow.SlideshowViewModel;

import java.util.List;

public class HomeFragment extends Fragment {
    public static final String EXTRA_TITEL = "com.example.datenbankefuerprojekt.ui.home.TITEL";
    public static final String EXTRA_ID = "com.example.datenbankefuerprojekt.ui.home.ID";
    public static final String EXTRA_DESC = "com.example.datenbankefuerprojekt.ui.home.DESC";
    public static final String EXTRA_PRIO = "com.example.datenbankefuerprojekt.ui.home.PRIO";
    public static final String EXTRA_COUNT = "com.example.datenbankefuerprojekt.ui.home.COUNT";
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
            /*
            FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.nav_host_fragment_content_home, new AddEditUebungFragment());
            fragmentTransaction.commit();*/
        });

        binding.recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        binding.recyclerView.setHasFixedSize(true);

        final UebungAdapter adapter = new UebungAdapter();
        binding.recyclerView.setAdapter(adapter);


        //homeViewModel = new HomeViewModel(getActivity().getApplication());


        homeViewModel.getAllUebung().observe(this, new Observer<List<Uebung>>() {
            @Override
            public void onChanged(List<Uebung> uebungs) {
                adapter.submitList(uebungs);
            }
        });

        /*
         * dieses swipen
         * */

        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0,ItemTouchHelper.LEFT|ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                homeViewModel.delete(adapter.getUebungAt(viewHolder.getAdapterPosition()));
                Toast.makeText(getActivity(),"Uebung geloescht", Toast.LENGTH_SHORT).show();
            }
        }).attachToRecyclerView(binding.recyclerView);

        adapter.setOnClickListener(new UebungAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Uebung uebung) {
                Bundle bundle = new Bundle();
                bundle.putInt(EXTRA_ID, uebung.getId());
                bundle.putString(EXTRA_TITEL, uebung.getTitel());
                bundle.putString(EXTRA_DESC, uebung.getBeschreibung());
                bundle.putInt(EXTRA_PRIO, uebung.getPrioritaet());
                bundle.putInt(EXTRA_COUNT, uebung.getAnzahlDerWiederholungen());
                AddEditUebungFragment addEditUebungFragment = new AddEditUebungFragment();
                addEditUebungFragment.setArguments(bundle);

                //Toast.makeText(getActivity(), bundle.toString(), Toast.LENGTH_LONG).show();
                Navigation.findNavController(root).navigate(R.id.action_nav_home_to_nav_home_add_edit_uebung, bundle);

                //FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
                //fragmentTransaction.replace(R.id.nav_host_fragment_content_home, addEditUebungFragment);
                //
                //this breaks everything if you save or go into the weird other thing idk man
                //fragmentTransaction.addToBackStack(null);
                //
                //
                //fragmentTransaction.commit();
            }
        });
        return root;
    }

    public void deleteAllUebungen(){
        homeViewModel.deleteAllUebung();
        Toast.makeText(getActivity(), "Alle Uebungen geloescht", Toast.LENGTH_SHORT).show();
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
}
/*
CODE GRAVEYARD:
 @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == ADD_UEBUNG_REQUEST && resultCode == Activity.RESULT_OK){
            String titel = data.getStringExtra(AddEditUebungFragment.EXTRA_TITLE);
            String description = data.getStringExtra(AddEditUebungFragment.EXTRA_DESCRIPTION);
            int priority = data.getIntExtra(AddEditUebungFragment.EXTRA_PRIORITY, 1);
            int anzahl = data.getIntExtra(AddEditUebungFragment.EXTRA_COUNT, 1);
            Uebung uebung = new Uebung(titel, description, anzahl, priority);
            homeViewModel.insert(uebung);
            Toast.makeText(getActivity(), "Uebung hinzugefuegt", Toast.LENGTH_SHORT).show();
        } else if (requestCode == EDIT_NOTE_REQUEST && resultCode == Activity.RESULT_OK){
            int id = data.getIntExtra(AddEditUebungFragment.EXTRA_ID, -1);
            if (id == -1) {
                Toast.makeText(getActivity(), "Fehler aufgetreten", Toast.LENGTH_SHORT).show();
                return;
            }
            String titel = data.getStringExtra(AddEditUebungFragment.EXTRA_TITLE);
            String description = data.getStringExtra(AddEditUebungFragment.EXTRA_DESCRIPTION);
            int priority = data.getIntExtra(AddEditUebungFragment.EXTRA_PRIORITY, 1);
            int anzahl = data.getIntExtra(AddEditUebungFragment.EXTRA_COUNT, 1);
            Uebung uebung = new Uebung(titel, description, anzahl, priority);
            uebung.setId(id);

            homeViewModel.update(uebung);

            Toast.makeText(getActivity(), "Uebung aktuallisiert", Toast.LENGTH_SHORT).show();

        } else {
            Toast.makeText(getActivity(), "Uebung nicht hinzugefuegt", Toast.LENGTH_SHORT).show();
        }
    }


    Intent intent = new Intent(getActivity(), AddEditUebungFragment.class);
                intent.putExtra(AddEditUebungFragment.EXTRA_ID, uebung.getId());
                intent.putExtra(AddEditUebungFragment.EXTRA_TITLE, uebung.getTitel());
                intent.putExtra(AddEditUebungFragment.EXTRA_DESCRIPTION, uebung.getBeschreibung());
                intent.putExtra(AddEditUebungFragment.EXTRA_PRIORITY, uebung.getPrioritaet());
                intent.putExtra(AddEditUebungFragment.EXTRA_COUNT, uebung.getAnzahlDerWiederholungen());
                startActivityForResult(intent, EDIT_NOTE_REQUEST);
 */