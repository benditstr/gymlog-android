package de.th.nuernberg.bme.gymlog.ui.log;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.snackbar.Snackbar;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import de.th.nuernberg.bme.gymlog.R;
import de.th.nuernberg.bme.gymlog.database.entity.Exercise;
import de.th.nuernberg.bme.gymlog.databinding.FragmentLogBinding;
import de.th.nuernberg.bme.gymlog.model.WorkoutSetWithExercise;
import de.th.nuernberg.bme.gymlog.util.SetValidator;

public class LogFragment extends Fragment {

    private FragmentLogBinding binding;
    private LogViewModel viewModel;
    private WorkoutSetAdapter adapter;
    private ArrayAdapter<String> dropdownAdapter;

    private List<Exercise> currentExercises = new ArrayList<>();
    private Exercise selectedExercise = null;
    private final Calendar selectedDate = Calendar.getInstance();
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy", Locale.GERMANY);

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentLogBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = new ViewModelProvider(this).get(LogViewModel.class);

        setupDropdown();
        setupRecyclerView();
        setupDateButton();
        setupSaveButton();
        observeData();
    }

    private void setupDropdown() {
        dropdownAdapter = new ArrayAdapter<>(requireContext(),
            R.layout.item_dropdown, new ArrayList<>());
        binding.dropdownExercise.setAdapter(dropdownAdapter);
        binding.dropdownExercise.setOnClickListener(v ->
            binding.dropdownExercise.showDropDown());
        binding.dropdownExercise.setOnItemClickListener((parent, v, position, id) -> {
            if (position < currentExercises.size()) {
                selectedExercise = currentExercises.get(position);
                viewModel.selectExercise(selectedExercise.getId());
            }
        });
    }

    private void setupRecyclerView() {
        adapter = new WorkoutSetAdapter();
        binding.rvSets.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.rvSets.setAdapter(adapter);

        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(
            0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {

            @Override
            public boolean onMove(@NonNull RecyclerView rv,
                                  @NonNull RecyclerView.ViewHolder vh,
                                  @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                final int pos = viewHolder.getAdapterPosition();
                final WorkoutSetWithExercise item = adapter.getSetAt(pos);

                // Visuell wiederherstellen — Löschen erst bei Snackbar-Dismiss
                binding.getRoot().post(() -> adapter.notifyItemChanged(pos));

                Snackbar snackbar = Snackbar.make(binding.getRoot(),
                    getString(R.string.snackbar_set_deleted), Snackbar.LENGTH_LONG);
                snackbar.setAction(R.string.snackbar_undo, v -> { /* item bleibt */ });
                snackbar.addCallback(new Snackbar.Callback() {
                    @Override
                    public void onDismissed(Snackbar sb, int event) {
                        if (event != DISMISS_EVENT_ACTION) {
                            viewModel.deleteSet(item.workoutSet);
                        }
                    }
                });
                snackbar.show();
            }
        }).attachToRecyclerView(binding.rvSets);
    }

    private void setupDateButton() {
        binding.btnDate.setText(dateFormat.format(selectedDate.getTime()));
        binding.btnDate.setOnClickListener(v -> {
            new DatePickerDialog(requireContext(),
                (picker, year, month, day) -> {
                    selectedDate.set(year, month, day);
                    binding.btnDate.setText(dateFormat.format(selectedDate.getTime()));
                },
                selectedDate.get(Calendar.YEAR),
                selectedDate.get(Calendar.MONTH),
                selectedDate.get(Calendar.DAY_OF_MONTH)
            ).show();
        });
    }

    private void setupSaveButton() {
        binding.btnSave.setOnClickListener(v -> saveSet());
    }

    private void saveSet() {
        if (selectedExercise == null) {
            Snackbar.make(binding.getRoot(), R.string.toast_no_exercises, Snackbar.LENGTH_SHORT).show();
            return;
        }

        String weightStr = binding.etWeight.getText() != null
            ? binding.etWeight.getText().toString().trim().replace(',', '.') : "";
        String repsStr = binding.etReps.getText() != null
            ? binding.etReps.getText().toString().trim() : "";

        boolean valid = true;
        if (!SetValidator.isWeightValid(weightStr)) {
            binding.tilWeight.setError(getString(R.string.error_weight));
            valid = false;
        } else {
            binding.tilWeight.setError(null);
        }
        if (!SetValidator.isRepsValid(repsStr)) {
            binding.tilReps.setError(getString(R.string.error_reps));
            valid = false;
        } else {
            binding.tilReps.setError(null);
        }
        if (!valid) return;

        float weight = Float.parseFloat(weightStr);
        int reps = Integer.parseInt(repsStr);
        viewModel.addSet(selectedExercise.getId(), selectedExercise.getName(),
            weight, reps, selectedDate.getTimeInMillis());

        binding.etWeight.setText("");
        binding.etReps.setText("");
        Snackbar.make(binding.getRoot(), R.string.snackbar_set_saved, Snackbar.LENGTH_SHORT).show();
    }

    /** Glass-Snackbar mit Scale/Fade-Animation bei neuem persönlichen Rekord (Spec 5.2). */
    private void showPrCelebration(String message) {
        Snackbar bar = Snackbar.make(binding.getRoot(), message, Snackbar.LENGTH_LONG);
        View sbView = bar.getView();
        sbView.setBackgroundResource(R.drawable.bg_glass_card_accent);
        TextView tv = sbView.findViewById(com.google.android.material.R.id.snackbar_text);
        if (tv != null) {
            tv.setTextColor(ContextCompat.getColor(requireContext(), R.color.text_primary));
            tv.setTextSize(14f);
        }
        bar.show();
        sbView.setScaleX(0.8f);
        sbView.setScaleY(0.8f);
        sbView.setAlpha(0f);
        sbView.animate().scaleX(1f).scaleY(1f).alpha(1f).setDuration(280).start();
    }

    private void observeData() {
        viewModel.allExercises.observe(getViewLifecycleOwner(), exercises -> {
            currentExercises = exercises != null ? exercises : new ArrayList<>();
            List<String> names = new ArrayList<>();
            for (Exercise e : currentExercises) names.add(e.getName());
            dropdownAdapter.clear();
            dropdownAdapter.addAll(names);

            if (!currentExercises.isEmpty() && selectedExercise == null) {
                selectedExercise = currentExercises.get(0);
                binding.dropdownExercise.setText(selectedExercise.getName(), false);
                viewModel.selectExercise(selectedExercise.getId());
            }
        });

        viewModel.setsForExercise.observe(getViewLifecycleOwner(), sets -> {
            adapter.setSets(sets);
        });

        viewModel.newPrEvent.observe(getViewLifecycleOwner(), message -> {
            if (message != null) {
                showPrCelebration(message);
                viewModel.clearPrEvent();
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
