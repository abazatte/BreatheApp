package com.example.datenbankefuerprojekt.ui.home;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import com.example.datenbankefuerprojekt.R;
import com.example.datenbankefuerprojekt.databinding.FragmentAddEditUebungBinding;
import com.example.datenbankefuerprojekt.db.main.database.FragmentAdapter;
import com.example.datenbankefuerprojekt.db.main.database.Uebung;

import java.util.List;

public class AddEditUebungFragment extends Fragment {

    public static final String EXTRA_EIN = "com.example.datenbankefuerprojekt.ui.home.EIN";
    public static final String EXTRA_LUFTEIN = "com.example.datenbankefuerprojekt.ui.home.LUFTEIN";
    public static final String EXTRA_AUS = "com.example.datenbankefuerprojekt.ui.home.AUS";
    public static final String EXTRA_LUFTAUS = "com.example.datenbankefuerprojekt.ui.home.LUFTAUS";
    public static final String EXTRA_COUNT = "com.example.datenbankefuerprojekt.ui.home.COUNT";
    public static final String EXTRA_UEBUNG_ID = "com.example.datenbankefuerprojekt.ui.home.UEBUNGID";
    private EditText editTextTitel;
    private EditText editTextDesc;
    private EditText numberPickerPriority;
    private EditText numberPickerCount;
    private FragmentAddEditUebungBinding binding;
    private HomeViewModel homeViewModel;
    private boolean isEdit = false;
    private int id;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        homeViewModel = new ViewModelProvider(requireActivity()).get(HomeViewModel.class);
        setHasOptionsMenu(true);
        binding = FragmentAddEditUebungBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        editTextTitel = binding.editTextTitle;
        editTextDesc = binding.editTextDescription;
        numberPickerPriority = binding.numberPickerPriority;
        numberPickerCount = binding.counter;

        Bundle bundle = getArguments();


        ((AppCompatActivity) getContext()).getSupportActionBar().setTitle(R.string.add_uebung);
        //Toast.makeText(getActivity(), bundle.toString(), Toast.LENGTH_LONG).show();

        if (bundle != null) {
            id = bundle.getInt(HomeFragment.EXTRA_ID, -1);
            ((AppCompatActivity) getContext()).getSupportActionBar().setTitle(R.string.edit_uebung);

            if (id != -1) {
                editTextTitel.setText(bundle.getString(HomeFragment.EXTRA_TITEL));
                editTextDesc.setText(bundle.getString(HomeFragment.EXTRA_DESC));
                numberPickerPriority.setText(Integer.toString(bundle.getInt(HomeFragment.EXTRA_PRIO)));
                numberPickerCount.setText(Integer.toString(bundle.getInt(HomeFragment.EXTRA_COUNT)));
                isEdit = true;

            }
        }

        binding.buttonAddFragment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle bundle1 = new Bundle();
                bundle1.putInt(EXTRA_UEBUNG_ID, id);
                AddEditFragmentFragment addEditFragmentFragment = new AddEditFragmentFragment();
                addEditFragmentFragment.setArguments(bundle1);

                FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
                fragmentTransaction.replace(R.id.nav_host_fragment_content_home, addEditFragmentFragment);
                fragmentTransaction.commit();
            }
        });

        binding.recyclerViewFragment.setLayoutManager(new LinearLayoutManager(getActivity()));
        binding.recyclerViewFragment.setHasFixedSize(true);

        final FragmentAdapter adapter = new FragmentAdapter();
        binding.recyclerViewFragment.setAdapter(adapter);

        if(id!=-1){
            homeViewModel.getAllFragmentsOfUebung(id).observe(this, new Observer<List<com.example.datenbankefuerprojekt.db.main.database.Fragment>>() {
                @Override
                public void onChanged(List<com.example.datenbankefuerprojekt.db.main.database.Fragment> fragments) {
                    adapter.submitList(fragments);
                }
            });
        }

        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0,ItemTouchHelper.LEFT|ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                homeViewModel.deleteFragment(adapter.getFragmentAt(viewHolder.getAdapterPosition()));
                Toast.makeText(getActivity(), "Fragment gelöscht", Toast.LENGTH_SHORT).show();
            }
        }).attachToRecyclerView(binding.recyclerViewFragment);

        adapter.setOnClickListener(new FragmentAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(com.example.datenbankefuerprojekt.db.main.database.Fragment fragment) {
                //TODO: hier was machen
                Bundle bundle1 = new Bundle();
                bundle1.putInt(HomeFragment.EXTRA_ID, fragment.getId());
                bundle1.putString(HomeFragment.EXTRA_TITEL, fragment.getTitelFragment());
                bundle1.putInt(HomeFragment.EXTRA_PRIO, fragment.getPrioritaetFragment());
                bundle1.putInt(AddEditUebungFragment.EXTRA_EIN, fragment.getEinAtmenZeit());
                bundle1.putInt(AddEditUebungFragment.EXTRA_LUFTEIN, fragment.getEinLuftanhaltZeil());
                bundle1.putInt(AddEditUebungFragment.EXTRA_AUS, fragment.getAusAtmenZeit());
                bundle1.putInt(AddEditUebungFragment.EXTRA_LUFTAUS, fragment.getAusLuftanhaltZeit());
                bundle1.putInt(AddEditUebungFragment.EXTRA_COUNT, fragment.getAnzahlWiederholungenFragment());
                bundle1.putInt(AddEditUebungFragment.EXTRA_UEBUNG_ID, fragment.getUebungId());

                AddEditFragmentFragment addEditFragmentFragment = new AddEditFragmentFragment();
                addEditFragmentFragment.setArguments(bundle1);

                FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
                fragmentTransaction.replace(R.id.nav_host_fragment_content_home, addEditFragmentFragment);
                fragmentTransaction.commit();
            }
        });

        return root;
    }


    private void saveUebung() {
        String titel = editTextTitel.getText().toString();
        String desc = editTextDesc.getText().toString();
        int prio = Integer.parseInt(numberPickerPriority.getText().toString());
        int count = Integer.parseInt(numberPickerCount.getText().toString());
        if (titel.trim().isEmpty() || desc.trim().isEmpty()) {
            Toast.makeText(getActivity(), "Bitte Titel und Beschreibung hinzufuegen.", Toast.LENGTH_SHORT).show();
            return;
        }
        Uebung uebung = new Uebung(titel, desc, count, prio);
        homeViewModel.insert(uebung);
        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.nav_host_fragment_content_home, new HomeFragment());
        ((AppCompatActivity) getContext()).getSupportActionBar().setTitle(R.string.uebung_home);
        fragmentTransaction.commit();
    }

    private void updateUebung() {
        String titel = editTextTitel.getText().toString();
        String desc = editTextDesc.getText().toString();
        int prio = Integer.parseInt(numberPickerPriority.getText().toString());
        int count = Integer.parseInt(numberPickerCount.getText().toString());
        if (titel.trim().isEmpty() || desc.trim().isEmpty()) {
            Toast.makeText(getActivity(), "Bitte Titel und Beschreibung hinzufuegen.", Toast.LENGTH_SHORT).show();
            return;
        }
        Uebung uebung = new Uebung(titel, desc, count, prio);
        uebung.setId(id);
        homeViewModel.update(uebung);
        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.nav_host_fragment_content_home, new HomeFragment());
        ((AppCompatActivity) getContext()).getSupportActionBar().setTitle(R.string.uebung_home);
        fragmentTransaction.commit();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.add_edit_uebung_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.save_uebung:
                if (isEdit) {
                    updateUebung();
                } else {
                    saveUebung();
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}