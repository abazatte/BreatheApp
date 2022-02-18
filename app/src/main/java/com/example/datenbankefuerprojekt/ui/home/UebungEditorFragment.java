package com.example.datenbankefuerprojekt.ui.home;

import android.app.AlertDialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
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

public class UebungEditorFragment extends Fragment {

    public static final String EXTRA_EIN = "com.example.datenbankefuerprojekt.ui.home.EIN";
    public static final String EXTRA_LUFTEIN = "com.example.datenbankefuerprojekt.ui.home.LUFTEIN";
    public static final String EXTRA_AUS = "com.example.datenbankefuerprojekt.ui.home.AUS";
    public static final String EXTRA_LUFTAUS = "com.example.datenbankefuerprojekt.ui.home.LUFTAUS";
    public static final String EXTRA_FRAGMENT_COUNT = "com.example.datenbankefuerprojekt.ui.home.FRAGMENT_COUNT";
    public static final String EXTRA_UEBUNG_ID = "com.example.datenbankefuerprojekt.ui.home.UEBUNGID";


    private EditText editTextTitel;
    private EditText editTextDesc;
    private EditText editTextUebungPriority;
    private EditText editTextUebungCount;

    private FragmentAddEditUebungBinding binding;
    private HomeViewModel homeViewModel;
    private boolean isEdit;
    private int id;




    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        homeViewModel = new ViewModelProvider(requireActivity()).get(HomeViewModel.class);
        binding = FragmentAddEditUebungBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        //was macht das nochmal?
        setHasOptionsMenu(true);

        initMemberVariables();
        receiveBundleAndInitEditTextFields();
        setClickListenerForAddFragmentButton();
        initRecyclerView();

        return root;
    }

    /**
     * @author Abdurrahman Azattemür, Maximilian Jaesch
     * <p></p>
     * Extrahiert die Werte aus den EditText-Feldern und speichtert sie in der Datenbank als neue Uebung-Entität ab
     * */
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
    }

    /**
     * @author Abdurrahman Azattemür, Maximilian Jaesch
     * <p></p>
     * gleich wie saveUebung, aber es wird ein Record in der Datenbank aktualisiert anstatt das ein neuer Eintrag erstellt wird.
     * */
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
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.add_edit_uebung_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    /**
     * @author Abdurrahman Azattemür
     * <p></p>
     * <p>???</p>
     * */
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


    /**
     * @author Maximilian Jaesch
     * <p></p>
     * <p>Hilfsmethode die überprüft ob mindestens ein EditText-Feld leer ist. In der Datenbank sind keine null Werte erlaubt.
     * </p>
     * */
    private boolean isAFieldEmpty(){
        return TextUtils.isEmpty(editTextTitel.getText()) ||
                TextUtils.isEmpty(editTextDesc.getText()) ||
                TextUtils.isEmpty(editTextUebungCount.getText()) ||
                TextUtils.isEmpty(editTextUebungPriority.getText());
    }

    /**
     * @author Abdurrahman Azattemür, Maximilian Jaesch
     * <p></p>
     * <p>Hilfsmethode die ein Bundle erstellt um das AddEditFragmentFragment zu initialisieren</p>
     * */
    private Bundle createBundleForUebungsFragment(com.example.datenbankefuerprojekt.db.main.database.Fragment fragment){
        Bundle bundle1 = new Bundle();
        bundle1.putInt(HomeFragment.EXTRA_ID, fragment.getFragmentId());
        bundle1.putString(HomeFragment.EXTRA_TITEL, fragment.getTitelFragment());
        bundle1.putInt(HomeFragment.EXTRA_PRIO, fragment.getPrioritaetFragment());
        bundle1.putInt(UebungEditorFragment.EXTRA_EIN, fragment.getEinAtmenZeit());
        bundle1.putInt(UebungEditorFragment.EXTRA_LUFTEIN, fragment.getEinLuftanhaltZeil());
        bundle1.putInt(UebungEditorFragment.EXTRA_AUS, fragment.getAusAtmenZeit());
        bundle1.putInt(UebungEditorFragment.EXTRA_LUFTAUS, fragment.getAusLuftanhaltZeit());
        bundle1.putInt(UebungEditorFragment.EXTRA_FRAGMENT_COUNT, fragment.getAnzahlWiederholungenFragment());
        bundle1.putInt(UebungEditorFragment.EXTRA_UEBUNG_ID, fragment.getUebungId());

        return bundle1;
    }

    /**
     * @author Abdurrahman Azattemür, Maximilian Jaesch
     * <p></p>
     * <p>Member-Variablen werden von dem binding initialisiert</p>
     * */
    private void initMemberVariables(){
        editTextTitel = binding.editTextTitle;
        editTextDesc = binding.editTextDescription;
        editTextUebungPriority = binding.numberPickerPriority;
        editTextUebungCount = binding.counter;
        isEdit = false;
    }

    /**
     * @author Abdurrahman Azattemür, Maximilian Jaesch
     * <p></p>
     * <p> Empfängt des übergebene Bundle aus HomeActivity und intialiesiert die werte der EditTextFelder </p>
     */
    private void receiveBundleAndInitEditTextFields(){
        Bundle bundle = getArguments();


        if (bundle != null) {
            id = bundle.getInt(HomeFragment.EXTRA_ID, -1);
            if (id != -1) {
                editTextTitel.setText(bundle.getString(HomeFragment.EXTRA_TITEL));
                editTextDesc.setText(bundle.getString(HomeFragment.EXTRA_DESC));
                editTextUebungPriority.setText(Integer.toString(bundle.getInt(HomeFragment.EXTRA_PRIO)));
                editTextUebungCount.setText(Integer.toString(bundle.getInt(HomeFragment.EXTRA_COUNT)));
                isEdit = true;
            }
        }
    }

    private void initRecyclerView(){
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
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setTitle("Fragment Löschen")
                        .setMessage("Möchten sie dieses Fragment löschen?");
                builder.setPositiveButton("Loeschen", (dialogInterface, i) -> {
                    homeViewModel.deleteFragment(adapter.getFragmentAt(viewHolder.getAdapterPosition()));
                    Toast.makeText(getActivity(),"Fragment geloescht", Toast.LENGTH_SHORT).show();
                });
                builder.setNegativeButton("Abbrechen", ((dialogInterface, i) -> {
                    adapter.notifyItemChanged(viewHolder.getAdapterPosition());
                }));

                AlertDialog dialog = builder.create();
                dialog.show();
            }
        }).attachToRecyclerView(binding.recyclerViewFragment);

        adapter.setOnClickListener(fragment ->
                Navigation.findNavController(binding.getRoot()).navigate(R.id.action_nav_home_add_edit_uebung_to_nav_home_add_edit_fragment,
                        createBundleForUebungsFragment(fragment))
        );
    }

    /**
     * @author Abdurrahman Azattemür, Maximilian Jaesch
     * <p></p>
     * <p>Setzt den OnclickListener des AddFragment Buttons.</p>
     * <p>wenn die Uebung gerade neu erstellt wurde, und noch nicht abgespeichert wurde, wird eine Snackbar ausgegeben.</p>
     * <p>Um zu einer noch nicht gespeicherten Uebung Fragmente hinzuzufügen, muss die Speicherung der Fragmente verlangsamt werden,
     * um sicherzugehen das die Uebung geladen werden kann, wenn man von dem Fragment editor wieder zum UebungsEditor wechseln möchte</p>
     * <p>Dies wird mit dieser Lösung vermieden.</p>
     * */
    private void setClickListenerForAddFragmentButton(){
        if(isEdit){
            binding.buttonAddFragment.setOnClickListener(view -> {
                Bundle bundle1 = new Bundle();
                bundle1.putInt(EXTRA_UEBUNG_ID, id);

                Navigation.findNavController(binding.getRoot()).navigate(R.id.action_nav_home_add_edit_uebung_to_nav_home_add_edit_fragment, bundle1);
            });
        } else {
            binding.buttonAddFragment.setOnClickListener(view ->
                    Snackbar.make(binding.getRoot(),"Bitte die Übung einmal Abspeichern bevor Sie ein Fragment hinzufügen", Snackbar.LENGTH_LONG).show());

        }
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