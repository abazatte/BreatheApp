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
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.datenbankefuerprojekt.R;
import com.example.datenbankefuerprojekt.databinding.FragmentAddEditUebungBinding;
import com.example.datenbankefuerprojekt.db.main.database.FragmentAdapter;
import com.example.datenbankefuerprojekt.db.main.database.Uebung;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.switchmaterial.SwitchMaterial;

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
    private EditText editTextMinutes;
    private EditText editTextSeconds;
    private SwitchMaterial useTimeSwitch;
    private Spinner animationSpinner;

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
        setBorderOfEditTexts(useTimeSwitch.isChecked());
        setOnChangedListenerForTimeSwitch();
        setClickListenerForAddFragmentButton();
        initRecyclerView();

        return root;
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
        editTextMinutes = binding.editTextMinutes;
        editTextSeconds = binding.editTextSeconds;
        useTimeSwitch = binding.switchUseSeconds;
        animationSpinner = binding.animationSpinner;

        isEdit = false;
    }

    private void setOnChangedListenerForTimeSwitch(){
        useTimeSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                setBorderOfEditTexts(isChecked);
            }
        });
    }

    private void setBorderOfEditTexts(boolean timeSwitchIsChecked){
        if(timeSwitchIsChecked){
            editTextMinutes.setBackgroundResource(R.drawable.green_border);
            editTextSeconds.setBackgroundResource(R.drawable.green_border);
            editTextUebungCount.setBackgroundResource(android.R.drawable.edit_text);
        } else{
            editTextMinutes.setBackgroundResource(android.R.drawable.edit_text);
            editTextSeconds.setBackgroundResource(android.R.drawable.edit_text);
            editTextUebungCount.setBackgroundResource(R.drawable.green_border);
        }
    }

    /**
     * @author Abdurrahman Azattemür, Maximilian Jaesch
     * <p></p>
     * Extrahiert die Werte aus den EditText-Feldern und speichert sie in der Datenbank als neue Uebung-Entität ab
     * */
    private void saveUebung() {
        if(!areAllInputsValid()){
            return;
        }

        String titel = editTextTitel.getText().toString();
        String desc = editTextDesc.getText().toString();
        int prio = Integer.parseInt(editTextUebungPriority.getText().toString());
        int count = 0;
        if(!TextUtils.isEmpty(editTextUebungCount.getText())){
            count = Integer.parseInt(editTextUebungCount.getText().toString());
        }
        boolean useTimed = useTimeSwitch.isChecked();
        int secondsInput=0;
        int minutesInput=0;
        int secondsComplete=0;
        if(!TextUtils.isEmpty(editTextSeconds.getText())){
            secondsInput = Integer.parseInt(editTextSeconds.getText().toString());
        }
        if(!TextUtils.isEmpty(editTextMinutes.getText())){
            minutesInput = Integer.parseInt(editTextMinutes.getText().toString());
        }
        if((secondsInput != 0) ||(minutesInput!=0)){
            if(secondsInput < 0) secondsInput *= -1;
            if(minutesInput < 0) minutesInput *= -1;
            secondsComplete = secondsInput + (minutesInput*60);
        }


        Uebung uebung = new Uebung(titel, desc, count, prio, useTimed, secondsComplete, animationSpinner.getSelectedItemPosition());
        homeViewModel.insert(uebung);
        Navigation.findNavController(binding.getRoot()).navigate(R.id.action_nav_home_add_edit_uebung_to_nav_home);
    }

    /**
     * @author Abdurrahman Azattemür, Maximilian Jaesch
     * <p></p>
     * gleich wie saveUebung, aber es wird ein Record in der Datenbank aktualisiert anstatt das ein neuer Eintrag erstellt wird.
     * */
    private void updateUebung() {
        if(!areAllInputsValid()){
            return;
        }

        String titel = editTextTitel.getText().toString();
        String desc = editTextDesc.getText().toString();
        int prio = Integer.parseInt(editTextUebungPriority.getText().toString());
        int count = Integer.parseInt(editTextUebungCount.getText().toString());
        boolean useTimed = useTimeSwitch.isChecked();
        int secondsInput=0;
        int minutesInput=0;
        int secondsComplete=0;
        if(!TextUtils.isEmpty(editTextSeconds.getText())){
            secondsInput = Integer.parseInt(editTextSeconds.getText().toString());
        }
        if(!TextUtils.isEmpty(editTextMinutes.getText())){
            minutesInput = Integer.parseInt(editTextMinutes.getText().toString());
        }
        if((secondsInput != 0) ||(minutesInput!=0)){
            if(secondsInput < 0) secondsInput *= -1;
            if(minutesInput < 0) minutesInput *= -1;
            secondsComplete = secondsInput + (minutesInput*60);
        }


        Uebung uebung = new Uebung(titel, desc, count, prio,useTimed,secondsComplete,animationSpinner.getSelectedItemPosition());
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
     * <p>minuten und sekunden werden ignorier, und wenn es leer ist stattdessen eine default value von 0 benutzt!</p>
     * */
    private boolean isAFieldEmpty(){
        return TextUtils.isEmpty(editTextTitel.getText()) ||
                TextUtils.isEmpty(editTextDesc.getText()) ||
                TextUtils.isEmpty(editTextUebungPriority.getText()) ;
    }

    private boolean isSpinnerInDefaultPos(){
        return animationSpinner.getSelectedItemPosition() == 0;
    }

    private boolean areAllInputsValid(){
        boolean valid = true;
        if (isAFieldEmpty()) {
            Toast.makeText(getActivity(), "Bitte jedes Feld ausfüllen", Toast.LENGTH_SHORT).show();
            valid = false;
        }
        if(isSpinnerInDefaultPos()){
            Toast.makeText(getActivity(), "Bitte eine Animation auswählen", Toast.LENGTH_SHORT).show();
            valid = false;
        }
        return valid;
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
        bundle1.putInt(UebungEditorFragment.EXTRA_LUFTEIN, fragment.getEinLuftanhaltZeit());
        bundle1.putInt(UebungEditorFragment.EXTRA_AUS, fragment.getAusAtmenZeit());
        bundle1.putInt(UebungEditorFragment.EXTRA_LUFTAUS, fragment.getAusLuftanhaltZeit());
        bundle1.putInt(UebungEditorFragment.EXTRA_FRAGMENT_COUNT, fragment.getAnzahlWiederholungenFragment());
        bundle1.putInt(UebungEditorFragment.EXTRA_UEBUNG_ID, fragment.getUebungId());

        return bundle1;
    }



    /**
     * @author Abdurrahman Azattemür, Maximilian Jaesch
     * <p></p>
     * <p> Empfängt des übergebene Bundle aus HomeActivity und intialiesiert die werte der EditTextFelder </p>
     */
    private void receiveBundleAndInitEditTextFields(){
        Bundle bundle = getArguments();
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getContext(),R.array.animations_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        animationSpinner.setAdapter(adapter);

        if (bundle != null) {
            id = bundle.getInt(HomeFragment.EXTRA_ID, -1);
            if (id != -1) {
                editTextTitel.setText(bundle.getString(HomeFragment.EXTRA_TITEL));
                editTextDesc.setText(bundle.getString(HomeFragment.EXTRA_DESC));
                editTextUebungPriority.setText(Integer.toString(bundle.getInt(HomeFragment.EXTRA_PRIO)));
                editTextUebungCount.setText(Integer.toString(bundle.getInt(HomeFragment.EXTRA_COUNT)));
                useTimeSwitch.setChecked(bundle.getBoolean(HomeFragment.EXTRA_USE_SECONDS));
                int timeInSeconds = bundle.getInt(HomeFragment.EXTRA_SECONDS);
                int seconds = timeInSeconds % 60;
                int minutes = timeInSeconds / 60;
                editTextSeconds.setText(Integer.toString(seconds));
                editTextMinutes.setText(Integer.toString(minutes));
                isEdit = true;

                animationSpinner.setSelection(bundle.getInt(HomeFragment.EXTRA_SPINNER_POSITION));
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
                builder.setTitle(R.string.delete_fragment_dialog_title)
                        .setMessage(R.string.delete_fragment_dialog_content);
                builder.setPositiveButton(R.string.delete_fragment_dialog_positive_button_label, (dialogInterface, i) -> {
                    homeViewModel.deleteFragment(adapter.getFragmentAt(viewHolder.getAdapterPosition()));
                    Toast.makeText(getActivity(), R.string.delete_fragment_success_message, Toast.LENGTH_SHORT).show();
                });
                builder.setNegativeButton(R.string.delete_fragment_dialog_negative_button_label, ((dialogInterface, i) -> {
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