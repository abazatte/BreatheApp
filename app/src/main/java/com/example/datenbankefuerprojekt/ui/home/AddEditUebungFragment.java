package com.example.datenbankefuerprojekt.ui.home;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.TextUtils;
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
import com.google.android.material.snackbar.Snackbar;

import java.util.List;

public class AddEditUebungFragment extends Fragment {

    public static final String EXTRA_EIN = "com.example.datenbankefuerprojekt.ui.home.EIN";
    public static final String EXTRA_LUFTEIN = "com.example.datenbankefuerprojekt.ui.home.LUFTEIN";
    public static final String EXTRA_AUS = "com.example.datenbankefuerprojekt.ui.home.AUS";
    public static final String EXTRA_LUFTAUS = "com.example.datenbankefuerprojekt.ui.home.LUFTAUS";

    //macht grade keine probleme aber am besten refactoren!!! ueberschneidet sich mit count von uebung
    public static final String EXTRA_FRAGMENT_COUNT = "com.example.datenbankefuerprojekt.ui.home.FRAGMENT_COUNT";

    public static final String EXTRA_UEBUNG_ID = "com.example.datenbankefuerprojekt.ui.home.UEBUNGID";
    private EditText editTextTitel;
    private EditText editTextDesc;

    private EditText editTextUebungPriority;
    private EditText editTextUebungCount;
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
        editTextUebungPriority = binding.numberPickerPriority;
        editTextUebungCount = binding.counter;

        Bundle bundle = getArguments();

        //binding.buttonAddFragment.setVisibility(View.INVISIBLE);

        if (bundle != null) {
            id = bundle.getInt(HomeFragment.EXTRA_ID, -1);
            if (id != -1) {
                editTextTitel.setText(bundle.getString(HomeFragment.EXTRA_TITEL));
                editTextDesc.setText(bundle.getString(HomeFragment.EXTRA_DESC));
                editTextUebungPriority.setText(Integer.toString(bundle.getInt(HomeFragment.EXTRA_PRIO)));
                editTextUebungCount.setText(Integer.toString(bundle.getInt(HomeFragment.EXTRA_COUNT)));
                isEdit = true;
                //binding.buttonAddFragment.setVisibility(View.VISIBLE);
            }
        }

        if(isEdit){
            binding.buttonAddFragment.setOnClickListener(view -> {
                Bundle bundle1 = new Bundle();
                bundle1.putInt(EXTRA_UEBUNG_ID, id);

                Navigation.findNavController(root).navigate(R.id.action_nav_home_add_edit_uebung_to_nav_home_add_edit_fragment, bundle1);
            });
        } else {
            binding.buttonAddFragment.setOnClickListener(view ->
                    Snackbar.make(binding.getRoot(),"Bitte die Übung einmal Abspeichern bevor Sie ein Fragment hinzufügen", Snackbar.LENGTH_LONG).show());

        }




        binding.recyclerViewFragment.setLayoutManager(new LinearLayoutManager(getActivity()));
        binding.recyclerViewFragment.setHasFixedSize(true);

        final FragmentAdapter adapter = new FragmentAdapter();
        binding.recyclerViewFragment.setAdapter(adapter);

        if (id != -1) {
            homeViewModel.getAllFragmentsOfUebung(id).observe(this, fragments -> adapter.submitList(fragments));
        }

        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
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

        adapter.setOnClickListener(fragment -> {
            //TODO: hier was machen
            Bundle bundle1 = new Bundle();
            bundle1.putInt(HomeFragment.EXTRA_ID, fragment.getFragmentId());
            bundle1.putString(HomeFragment.EXTRA_TITEL, fragment.getTitelFragment());
            bundle1.putInt(HomeFragment.EXTRA_PRIO, fragment.getPrioritaetFragment());
            bundle1.putInt(AddEditUebungFragment.EXTRA_EIN, fragment.getEinAtmenZeit());
            bundle1.putInt(AddEditUebungFragment.EXTRA_LUFTEIN, fragment.getEinLuftanhaltZeil());
            bundle1.putInt(AddEditUebungFragment.EXTRA_AUS, fragment.getAusAtmenZeit());
            bundle1.putInt(AddEditUebungFragment.EXTRA_LUFTAUS, fragment.getAusLuftanhaltZeit());
            bundle1.putInt(AddEditUebungFragment.EXTRA_FRAGMENT_COUNT, fragment.getAnzahlWiederholungenFragment());
            bundle1.putInt(AddEditUebungFragment.EXTRA_UEBUNG_ID, fragment.getUebungId());

            Navigation.findNavController(root).navigate(R.id.action_nav_home_add_edit_uebung_to_nav_home_add_edit_fragment, bundle1);


            /*
            AddEditFragmentFragment addEditFragmentFragment = new AddEditFragmentFragment();
            addEditFragmentFragment.setArguments(bundle1);

            FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.nav_host_fragment_content_home, addEditFragmentFragment);
            fragmentTransaction.commit();*/
        });

        return root;
    }


    private void saveUebung() {
        if (isAFieldEmpty()) {
            Toast.makeText(getActivity(), "Bitte jedes Feld ausfüllen", Toast.LENGTH_SHORT).show();
            return;
        }

        String titel = editTextTitel.getText().toString();
        String desc = editTextDesc.getText().toString();
        int prio = Integer.parseInt(editTextUebungPriority.getText().toString());
        int count = Integer.parseInt(editTextUebungCount.getText().toString());
        if (titel.trim().isEmpty() || desc.trim().isEmpty()) {
            Toast.makeText(getActivity(), "Bitte Titel und Beschreibung hinzufuegen.", Toast.LENGTH_SHORT).show();
            return;
        }
        Uebung uebung = new Uebung(titel, desc, count, prio);
        homeViewModel.insert(uebung);
        Navigation.findNavController(binding.getRoot()).navigate(R.id.action_nav_home_add_edit_uebung_to_nav_home);

        /*
        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.nav_host_fragment_content_home, new HomeFragment());
        ((AppCompatActivity) getContext()).getSupportActionBar().setTitle(R.string.uebung_home);
        fragmentTransaction.commit();*/
    }

    private void updateUebung() {
        if (isAFieldEmpty()) {
            Toast.makeText(getActivity(), "Bitte jedes Feld ausfüllen", Toast.LENGTH_SHORT).show();
            return;
        }

        String titel = editTextTitel.getText().toString();
        String desc = editTextDesc.getText().toString();
        int prio = Integer.parseInt(editTextUebungPriority.getText().toString());
        int count = Integer.parseInt(editTextUebungCount.getText().toString());
        if (titel.trim().isEmpty() || desc.trim().isEmpty()) {
            Toast.makeText(getActivity(), "Bitte Titel und Beschreibung hinzufuegen.", Toast.LENGTH_SHORT).show();
            return;
        }
        Uebung uebung = new Uebung(titel, desc, count, prio);
        uebung.setId(id);
        homeViewModel.update(uebung);
        Navigation.findNavController(binding.getRoot()).navigate(R.id.action_nav_home_add_edit_uebung_to_nav_home);

        /*
        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.nav_host_fragment_content_home, new HomeFragment());
        ((AppCompatActivity) getContext()).getSupportActionBar().setTitle(R.string.uebung_home);
        fragmentTransaction.commit();*/
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

    private boolean isAFieldEmpty(){

        return TextUtils.isEmpty(editTextTitel.getText()) ||
                TextUtils.isEmpty(editTextDesc.getText()) ||
                TextUtils.isEmpty(editTextUebungCount.getText()) ||
                TextUtils.isEmpty(editTextUebungPriority.getText());
    }
}



/*
CODE GRAVEYARD:
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

                    Bundle bundle1 = new Bundle();
                    bundle1.putInt(EXTRA_UEBUNG_ID, uebung.getId());
                    AddEditFragmentFragment addEditFragmentFragment = new AddEditFragmentFragment();
                    addEditFragmentFragment.setArguments(bundle1);
                    FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
                    fragmentTransaction.replace(R.id.nav_host_fragment_content_home, addEditFragmentFragment);
                    fragmentTransaction.commit();
 */