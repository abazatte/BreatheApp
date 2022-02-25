package com.example.datenbankefuerprojekt.ui.gallery;

import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import com.example.datenbankefuerprojekt.R;
import com.example.datenbankefuerprojekt.databinding.FragmentGalleryBinding;
import com.example.datenbankefuerprojekt.db.main.database.ControlPause;
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

import java.util.Comparator;
import java.util.List;

public class GalleryFragment extends Fragment {

    private GalleryViewModel galleryViewModel;
    private FragmentGalleryBinding binding;

    private static final int MAX_X_VALUE = 12;
    private static final int MAX_Y_VALUE = 50;
    private static final int MIN_Y_VALUE = 5;
    private static final String SET_LABEL = "App Downloads";
    private static String[] MONTH = new String[MAX_X_VALUE];

    private LiveData<List<ControlPause>> controlPauseList;
    List<ControlPause> controlPauses;
    List<List<ControlPause>> monthControlPauses = new ArrayList<List<ControlPause>>();
    private BarChart chart;
    List<ControlPause> jan = new ArrayList<ControlPause>();
    List<ControlPause> feb = new ArrayList<ControlPause>();
    List<ControlPause> mar = new ArrayList<ControlPause>();
    List<ControlPause> apr = new ArrayList<ControlPause>();
    List<ControlPause> mai = new ArrayList<ControlPause>();
    List<ControlPause> jun = new ArrayList<ControlPause>();
    List<ControlPause> jul = new ArrayList<ControlPause>();
    List<ControlPause> aug = new ArrayList<ControlPause>();
    List<ControlPause> sep = new ArrayList<ControlPause>();
    List<ControlPause> okt = new ArrayList<ControlPause>();
    List<ControlPause> nov = new ArrayList<ControlPause>();
    List<ControlPause> dez = new ArrayList<ControlPause>();

    private final MyHandler myHandler = new MyHandler();
    private MyRunnable myRunnable;


    /*https://stackoverflow.com/questions/1520887/how-to-pause-sleep-thread-or-process-in-android*/
    private static class MyHandler extends Handler {
    }

    private static class MyRunnable implements Runnable {
        private final WeakReference<GalleryFragment> galleryFragmentWeakReference;

        public MyRunnable(GalleryFragment fragment) {
            galleryFragmentWeakReference = new WeakReference<>(fragment);
        }

        @Override
        public void run() {
            GalleryFragment fragment = galleryFragmentWeakReference.get();
            if (fragment != null) {
                //fragment.returnToUebungEditor();
                fragment.startDiagrammCreation();
            }
        }
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        galleryViewModel =
                new ViewModelProvider(requireActivity()).get(GalleryViewModel.class);
        setMonthXValues();
        binding = FragmentGalleryBinding.inflate(inflater, container, false);
        View root = binding.getRoot();


        galleryViewModel.getAllControlPauseByDate().observe(this, allControlPause -> {
            if (allControlPause != null) {
                this.controlPauses = allControlPause;
                startDiagrammCreation();
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

            float y = getMonthControlPausesForBiggerList(i).get(0).getLaenge();
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
        java.util.Date currentDate = Calendar.getInstance().getTime();

        int currentMonth = currentDate.getMonth();

        int dateChange = currentMonth;
        for (int i = 11; i >= 0; i--) {
            MONTH[i] = getMonthString(dateChange);
            dateChange--;
            if (dateChange == -1) {
                dateChange = 11;
            }
        }
        for (int k = 0; k < 12; k++) {
            getMonthControlPausesForBiggerList(k).sort(new Comparator<ControlPause>() {
                @Override
                public int compare(ControlPause controlPause, ControlPause t1) {
                    return Long.compare(controlPause.getLaenge(), t1.getLaenge());
                }
            });
        }
        for (int j = 0; j < 12; j++) {
            monthControlPauses.add(getMonthControlPausesForBiggerList(j));
        }
    }

    private void initMonthControlPauses() {
        for (int i = 0; i < controlPauses.size(); i++) {
            int month = controlPauses.get(i).getDate().getMonth();
            getMonthControlPausesForBiggerList(month).add(controlPauses.get(i));
        }
    }

    private List<ControlPause> getMonthControlPausesForBiggerList(int month) {
        switch (month) {
            case 0:
                return jan;
            case 1:
                return feb;
            case 2:
                return mar;
            case 3:
                return apr;
            case 4:
                return mai;
            case 5:
                return jun;
            case 6:
                return jul;
            case 7:
                return aug;
            case 8:
                return sep;
            case 9:
                return okt;
            case 10:
                return nov;
            case 11:
                return dez;
            default:
                return null;
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
}