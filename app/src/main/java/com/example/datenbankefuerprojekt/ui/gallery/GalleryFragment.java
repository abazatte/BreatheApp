package com.example.datenbankefuerprojekt.ui.gallery;

import android.app.AlertDialog;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import com.example.datenbankefuerprojekt.R;
import com.example.datenbankefuerprojekt.databinding.FragmentGalleryBinding;
import com.example.datenbankefuerprojekt.db.main.database.controlpause.ControlPause;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.ValueFormatter;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Calendar;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class GalleryFragment extends Fragment {

    public static final String TAG = "GalleryFragment";
    private GalleryViewModel galleryViewModel;
    private FragmentGalleryBinding binding;

    //private static final int ANZAHL_MONTHS = 12;

    private static final int MAX_X_VALUE = 12;
    private static final int MAX_Y_VALUE = 50;
    private static final int MIN_Y_VALUE = 5;
    private static final String SET_LABEL = "Record Seconds";
    private static String[] MONTH = new String[MAX_X_VALUE];

    private LiveData<List<ControlPause>> controlPauseList;
    List<ControlPause> controlPauses;
    List<List<ControlPause>> monthControlPauses = new ArrayList<List<ControlPause>>();
    List<List<ControlPause>> sortedMonthControlPauses = new ArrayList<List<ControlPause>>();
    private BarChart chart;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        galleryViewModel =
                new ViewModelProvider(requireActivity()).get(GalleryViewModel.class);
        setHasOptionsMenu(true);
        setMonthXValues();
        binding = FragmentGalleryBinding.inflate(inflater, container, false);
        View root = binding.getRoot();


        galleryViewModel.getAllControlPauseByDate().observe(this, allControlPause -> {
            if (allControlPause != null && !allControlPause.isEmpty()) {
                this.controlPauses = allControlPause;
                populateMonthControlPauses();
                fillMonthControlPauses();
                reorderMonthControlPauses();
                startDiagrammCreation();
                Log.i(TAG, "onCreateView: dolepe?");
            }
        });


        //galleryViewModel.testMonth();
        //myHandler.postDelayed(myRunnable,2000);

        binding.buttonControlpauseStart.setOnClickListener(view ->
                Navigation.findNavController(root).navigate(R.id.action_nav_gallery_start_gallery_to_nav_fragment, null));

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }



    private void configureChartAppearance() {
        chart.getDescription().setEnabled(false);
        chart.setDrawValueAboveBar(false);

        XAxis xAxis = chart.getXAxis();
        xAxis.setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                return MONTH[(int) value];
            }
        });

        YAxis axisLeft = chart.getAxisLeft();
        axisLeft.setGranularity(10f);
        axisLeft.setAxisMinimum(0);

        YAxis axisRight = chart.getAxisRight();
        axisRight.setGranularity(10f);
        axisRight.setAxisMinimum(0);
    }

    private BarData createChartData() {
        ArrayList<BarEntry> values = new ArrayList<>();
        for (int i = 0; i < MAX_X_VALUE; i++) {
            float x = i;
            float y = 0;
            if(!(sortedMonthControlPauses.get(i).size() == 0)){
                y = sortedMonthControlPauses.get(i).get(0).getLaenge();
            }
            //float y = MIN_Y_VALUE + new Random().nextFloat() * (MAX_Y_VALUE - MIN_Y_VALUE);
            // MIN_Y_VALUE + new Random().nextFloat() * (MAX_Y_VALUE - MIN_Y_VALUE);
            values.add(new BarEntry(x, y));
        }

        BarDataSet set1 = new BarDataSet(values, SET_LABEL);

        return new BarData(set1);
    }

    private void prepareChartData(BarData date) {
        date.setValueTextSize(12f);
        chart.setData(date);
        chart.invalidate();
    }

    private void setMonthXValues() {
        int dateChange = getCurrentMonth();
        for (int i = 11; i >= 0; i--) {
            MONTH[i] = getMonthString(dateChange);
            dateChange--;
            if (dateChange == -1) {
                dateChange = 11;
            }
        }
    }

    private void initMonthControlPauses() {
        for (int i = 0; i < controlPauses.size(); i++) {
            int month = controlPauses.get(i).getDate().getMonth();
            //getMonthControlPausesForBiggerList(month).add(controlPauses.get(i));
        }
    }

    private void populateMonthControlPauses(){
        for(int i = 0; i < MAX_X_VALUE; i++){
            this.monthControlPauses.add(new ArrayList<>());
            this.sortedMonthControlPauses.add(new ArrayList<>());
        }
    }

    private void fillMonthControlPauses(){
        int currentMonth = getCurrentMonth();
        //aus der Liste aller control pause, den month gucken, und dann dahin
        for (ControlPause c :controlPauses) {
            int monthOfControlPause = c.getDate().getMonth();
            monthControlPauses.get(monthOfControlPause).add(c);
            //den höchsten wert von current month
            //monthControlPauses.get(i).add();

        }

        for (List<ControlPause> l : monthControlPauses){
            //System.out.println(l.toString());
            l.sort(new Comparator<ControlPause>() {
                @Override
                public int compare(ControlPause controlPause, ControlPause t1) {
                    return Long.compare(controlPause.getLaenge(), t1.getLaenge());
                }
            });
            //ist falsch rum sortiert, deswegen reverse
            Collections.reverse(l);
            //System.out.println(l.toString());
        }


    }

    private int getCurrentMonth(){
        return Calendar.getInstance().get(Calendar.MONTH);
    }

    private void reorderMonthControlPauses(){
        int dateChange = getCurrentMonth();
        for (int i = 11; i >= 0; i--) {
            sortedMonthControlPauses.set(i, monthControlPauses.get(dateChange));
            dateChange--;
            if (dateChange == -1) {
                dateChange = 11;
            }
        }
    }

    private String getMonthString(int currentMonth) {
        switch (currentMonth) {
            case 0:
                return "Jan";
            case 1:
                return "Feb";
            case 2:
                return "Mar";
            case 3:
                return "Apr";
            case 4:
                return "Mai";
            case 5:
                return "Jun";
            case 6:
                return "Jul";
            case 7:
                return "Aug";
            case 8:
                return "Sep";
            case 9:
                return "Okt";
            case 10:
                return "Nov";
            case 11:
                return "Dez";
            default:
                return null;
        }
    }

    private void startDiagrammCreation() {
        //controlPauses = controlPauseList.getValue();
        chart = binding.barChart;
        BarData data = createChartData();
        configureChartAppearance();
        prepareChartData(data);
    }

    public void deleteAllControlPauses(){
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Alle Control Pauses Löschen?");
        builder.setMessage("Wollen Sie wirklich alle Control Pauses löschen?");
        builder.setPositiveButton("Alles Löschen", (dialogInterface, i) -> {
            galleryViewModel.deleteAllControlPauses();
            Toast.makeText(getActivity(), "Alle Control Pauses gelöscht.", Toast.LENGTH_SHORT).show();
            this.controlPauses = galleryViewModel.getAllControlPauseByDate().getValue();
            populateMonthControlPauses();
            fillMonthControlPauses();
            reorderMonthControlPauses();
            startDiagrammCreation();
        });
        builder.setNegativeButton("Abbrechen", null);

        AlertDialog dialog = builder.create();
        dialog.show();

    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.home_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.delete_all_uebung:
                deleteAllControlPauses();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}

