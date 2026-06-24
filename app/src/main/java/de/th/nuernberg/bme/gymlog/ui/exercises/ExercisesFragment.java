package de.th.nuernberg.bme.gymlog.ui.exercises;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import de.th.nuernberg.bme.gymlog.R;
import de.th.nuernberg.bme.gymlog.database.entity.Exercise;
import de.th.nuernberg.bme.gymlog.databinding.FragmentExercisesBinding;

public class ExercisesFragment extends Fragment {

    private FragmentExercisesBinding binding;
    private ExercisesViewModel viewModel;
    private ExerciseAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentExercisesBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = new ViewModelProvider(this).get(ExercisesViewModel.class);

        setupRecyclerView();
        setupAddButton();
        observeData();
    }

    private void setupRecyclerView() {
        adapter = new ExerciseAdapter();
        binding.rvExercises.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.rvExercises.setAdapter(adapter);

        // Klick auf Übung → Statistik öffnen
        adapter.setOnExerciseClickListener(exercise -> {
            Bundle args = new Bundle();
            args.putInt("exerciseId", exercise.getId());
            NavHostFragment.findNavController(this)
                .navigate(R.id.action_exercises_to_statistics, args);
        });

        // Swipe-to-Delete mit Undo-Snackbar
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
                final Exercise exercise = adapter.getExerciseAt(pos);

                // Item immer visuell wiederherstellen — Löschen nur nach Bestätigung (UF-01)
                adapter.notifyItemChanged(pos);

                // Anzahl betroffener Sätze ermitteln, dann Bestätigungsdialog zeigen.
                // Callback läuft auf einem Hintergrund-Thread → Activity null-sicher holen,
                // Re-Check auf dem UI-Thread (Fragment könnte zwischenzeitlich abgelöst sein).
                viewModel.countSetsForExercise(exercise.getId(), count -> {
                    FragmentActivity activity = getActivity();
                    if (activity == null) return;
                    activity.runOnUiThread(() -> {
                        if (isAdded() && binding != null) {
                            showDeleteExerciseDialog(exercise, count);
                        }
                    });
                });
            }
        }).attachToRecyclerView(binding.rvExercises);
    }

    private void showDeleteExerciseDialog(Exercise exercise, int setCount) {
        String message = setCount == 0
            ? getString(R.string.dialog_delete_exercise_no_sets)
            : getResources().getQuantityString(
                R.plurals.dialog_delete_exercise_message, setCount, setCount);

        new MaterialAlertDialogBuilder(requireContext(), R.style.ThemeOverlay_GymLog_Dialog)
            .setTitle(getString(R.string.dialog_delete_exercise_title, exercise.getName()))
            .setMessage(message)
            .setNegativeButton(R.string.btn_cancel, null)
            .setPositiveButton(R.string.btn_delete, (d, w) -> viewModel.deleteExercise(exercise))
            .show();
    }

    private void setupAddButton() {
        binding.btnAddExercise.setOnClickListener(v -> addExercise());
        binding.etExerciseName.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                addExercise();
                return true;
            }
            return false;
        });
        // Fehler beim Tippen zurücksetzen
        binding.etExerciseName.addTextChangedListener(new android.text.TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
                binding.tilExerciseName.setError(null);
            }
            @Override public void afterTextChanged(android.text.Editable s) {}
        });
    }

    private void addExercise() {
        String name = binding.etExerciseName.getText() != null
            ? binding.etExerciseName.getText().toString()
            : "";
        if (!name.trim().isEmpty()) {
            viewModel.addExercise(name);
            binding.etExerciseName.setText("");
        }
    }

    private void observeData() {
        viewModel.allExercises.observe(getViewLifecycleOwner(), exercises -> {
            adapter.setExercises(exercises);
            if (exercises == null || exercises.isEmpty()) {
                binding.tvEmptyExercises.setVisibility(View.VISIBLE);
                binding.rvExercises.setVisibility(View.GONE);
            } else {
                binding.tvEmptyExercises.setVisibility(View.GONE);
                binding.rvExercises.setVisibility(View.VISIBLE);
            }
        });

        viewModel.errorEvent.observe(getViewLifecycleOwner(), error -> {
            if ("duplicate".equals(error)) {
                binding.tilExerciseName.setError(getString(R.string.error_duplicate_exercise));
                viewModel.clearError();
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
