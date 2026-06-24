package de.th.nuernberg.bme.gymlog.ui.statistics;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;

import java.util.ArrayList;
import java.util.List;

import de.th.nuernberg.bme.gymlog.R;
import de.th.nuernberg.bme.gymlog.database.entity.Exercise;
import de.th.nuernberg.bme.gymlog.databinding.FragmentStatisticsBinding;
import de.th.nuernberg.bme.gymlog.model.MaxWeightPerDay;

import android.widget.ArrayAdapter;

public class StatisticsFragment extends Fragment {

    private FragmentStatisticsBinding binding;
    private StatisticsViewModel viewModel;
    private ArrayAdapter<String> dropdownAdapter;
    private List<Exercise> currentExercises = new ArrayList<>();
    private boolean initialSelectionDone = false;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentStatisticsBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = new ViewModelProvider(this).get(StatisticsViewModel.class);

        setupChart();
        setupDropdown();
        observeData();
    }

    private void setupChart() {
        int axisColor = ContextCompat.getColor(requireContext(), R.color.text_secondary);
        int gridColor = ContextCompat.getColor(requireContext(), R.color.glass_border);

        binding.lineChart.getDescription().setEnabled(false);
        binding.lineChart.getLegend().setEnabled(false);
        binding.lineChart.setNoDataText("Keine Daten vorhanden");
        binding.lineChart.setNoDataTextColor(axisColor);

        XAxis xAxis = binding.lineChart.getXAxis();
        xAxis.setDrawGridLines(false);
        xAxis.setTextColor(axisColor);
        xAxis.setAxisLineColor(gridColor);

        YAxis leftAxis = binding.lineChart.getAxisLeft();
        leftAxis.setTextColor(axisColor);
        leftAxis.setGridColor(gridColor);
        leftAxis.setAxisLineColor(gridColor);

        binding.lineChart.getAxisRight().setEnabled(false);
    }

    private void setupDropdown() {
        dropdownAdapter = new ArrayAdapter<>(requireContext(),
            R.layout.item_dropdown, new ArrayList<>());
        binding.dropdownExerciseStats.setAdapter(dropdownAdapter);
        binding.dropdownExerciseStats.setOnClickListener(v ->
            binding.dropdownExerciseStats.showDropDown());
        binding.dropdownExerciseStats.setOnItemClickListener((parent, v, position, id) -> {
            if (position < currentExercises.size()) {
                viewModel.selectExercise(currentExercises.get(position).getId());
            }
        });
    }

    private void observeData() {
        int requestedId = getArguments() != null ? getArguments().getInt("exerciseId", -1) : -1;

        viewModel.allExercises.observe(getViewLifecycleOwner(), exercises -> {
            currentExercises = exercises != null ? exercises : new ArrayList<>();
            List<String> names = new ArrayList<>();
            for (Exercise e : currentExercises) names.add(e.getName());
            dropdownAdapter.clear();
            dropdownAdapter.addAll(names);

            if (currentExercises.isEmpty()) {
                // KI-03: kein Exercise vorhanden
                binding.tvEmptyStats.setText(R.string.empty_stats_no_exercises);
                binding.tvEmptyStats.setVisibility(View.VISIBLE);
                binding.lineChart.setVisibility(View.GONE);
                binding.dropdownExerciseStats.setText("", false);
                return;
            }

            if (!initialSelectionDone) {
                initialSelectionDone = true;
                // Von ExercisesFragment weitergeleitet → direkt auswählen
                if (requestedId > 0) {
                    for (Exercise e : currentExercises) {
                        if (e.getId() == requestedId) {
                            binding.dropdownExerciseStats.setText(e.getName(), false);
                            viewModel.selectExercise(e.getId());
                            return;
                        }
                    }
                }
                // Fallback: erste Übung
                binding.dropdownExerciseStats.setText(currentExercises.get(0).getName(), false);
                viewModel.selectExercise(currentExercises.get(0).getId());
            }
        });

        viewModel.personalRecord.observe(getViewLifecycleOwner(), pr -> {
            if (pr != null && pr > 0) {
                float rounded = Math.round(pr * 10) / 10f;
                String prStr = rounded == (int) rounded
                    ? (int) rounded + " kg"
                    : rounded + " kg";
                binding.tvPr.setText(prStr);
            } else {
                binding.tvPr.setText("–");
            }
        });

        viewModel.chartData.observe(getViewLifecycleOwner(), this::updateChart);
    }

    private void updateChart(List<MaxWeightPerDay> data) {
        if (data == null || data.isEmpty()) {
            binding.tvEmptyStats.setText(R.string.empty_stats);
            binding.tvEmptyStats.setVisibility(View.VISIBLE);
            binding.lineChart.setVisibility(View.GONE);
            return;
        }

        binding.tvEmptyStats.setVisibility(View.GONE);
        binding.lineChart.setVisibility(View.VISIBLE);

        List<Entry> entries = new ArrayList<>();
        for (int i = 0; i < data.size(); i++) {
            entries.add(new Entry(i, data.get(i).maxWeight));
        }

        int accent = ContextCompat.getColor(requireContext(), R.color.accent);
        int hole = ContextCompat.getColor(requireContext(), R.color.bg_base);

        LineDataSet dataSet = new LineDataSet(entries, "Max. Gewicht");
        dataSet.setColor(accent);
        dataSet.setCircleColor(accent);
        dataSet.setCircleHoleColor(hole);
        dataSet.setLineWidth(2.5f);
        dataSet.setCircleRadius(4f);
        dataSet.setDrawValues(false);
        dataSet.setDrawFilled(true);
        dataSet.setFillColor(accent);
        dataSet.setFillAlpha(60);

        // KI-04: DateAxisFormatter hat bereits Bounds-Check, setze expliziten LabelCount
        binding.lineChart.getXAxis().setValueFormatter(new DateAxisFormatter(data));
        binding.lineChart.getXAxis().setGranularity(1f);
        binding.lineChart.getXAxis().setLabelRotationAngle(-45f);
        binding.lineChart.getXAxis().setLabelCount(Math.min(data.size(), 6), false);

        binding.lineChart.setData(new LineData(dataSet));
        binding.lineChart.invalidate();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
