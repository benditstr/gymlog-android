package de.th.nuernberg.bme.gymlog.ui.plan;

import android.content.Context;
import android.os.Bundle;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.snackbar.Snackbar;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;

import java.util.ArrayList;
import java.util.List;

import de.th.nuernberg.bme.gymlog.R;
import de.th.nuernberg.bme.gymlog.database.entity.Exercise;
import de.th.nuernberg.bme.gymlog.database.entity.TrainingPlan;
import de.th.nuernberg.bme.gymlog.databinding.FragmentPlanBinding;
import de.th.nuernberg.bme.gymlog.databinding.ItemPlanExerciseBinding;
import de.th.nuernberg.bme.gymlog.model.PlanExerciseWithName;

public class PlanFragment extends Fragment {

    private static final String[] DAY_NAMES = {
        "Montag", "Dienstag", "Mittwoch", "Donnerstag", "Freitag", "Samstag", "Sonntag"
    };

    private FragmentPlanBinding binding;
    private PlanViewModel viewModel;
    private ArrayAdapter<String> plansDropdownAdapter;

    private List<TrainingPlan> currentPlans = new ArrayList<>();
    private List<Exercise> currentExercises = new ArrayList<>();
    private final SparseArray<LinearLayout> dayContainers = new SparseArray<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentPlanBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = new ViewModelProvider(this).get(PlanViewModel.class);

        setupPlanDropdown();
        buildDaySections();
        setupButtons();
        observeData();
    }

    private void setupPlanDropdown() {
        plansDropdownAdapter = new ArrayAdapter<>(requireContext(),
            R.layout.item_dropdown, new ArrayList<>());
        binding.dropdownPlan.setAdapter(plansDropdownAdapter);
        binding.dropdownPlan.setOnClickListener(v -> binding.dropdownPlan.showDropDown());
        binding.dropdownPlan.setOnItemClickListener((parent, v, position, id) -> {
            if (position < currentPlans.size()) {
                viewModel.selectPlan(currentPlans.get(position).getId());
            }
        });
    }

    private void buildDaySections() {
        LayoutInflater inflater = LayoutInflater.from(requireContext());
        int dp8 = (int) (8 * getResources().getDisplayMetrics().density);
        int dp16 = dp8 * 2;

        for (int i = 0; i < 7; i++) {
            int dayOfWeek = i + 1;

            // Äußere Card
            MaterialCardView card = new MaterialCardView(requireContext());
            LinearLayout.LayoutParams cardParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
            cardParams.setMargins(0, 0, 0, dp8 + dp8);
            card.setLayoutParams(cardParams);
            card.setCardBackgroundColor(requireContext().getColor(R.color.glass_fill));
            card.setRadius(dp16);
            card.setCardElevation(0);
            card.setStrokeColor(requireContext().getColor(R.color.glass_border));
            card.setStrokeWidth(dp8 / 4);

            // Inneres Layout
            LinearLayout inner = new LinearLayout(requireContext());
            inner.setOrientation(LinearLayout.VERTICAL);
            inner.setPadding(dp16, dp16, dp16, dp16);

            // Tages-Header
            android.widget.TextView header = new android.widget.TextView(requireContext());
            header.setText(DAY_NAMES[i]);
            header.setTextColor(requireContext().getColor(R.color.accent));
            header.setTextSize(13f);
            header.setTypeface(null, android.graphics.Typeface.BOLD);
            header.setLetterSpacing(0.08f);
            inner.addView(header);

            // Separator
            View sep = new View(requireContext());
            LinearLayout.LayoutParams sepParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, 1);
            sepParams.setMargins(0, dp8, 0, dp8);
            sep.setLayoutParams(sepParams);
            sep.setBackgroundColor(requireContext().getColor(R.color.glass_border));
            inner.addView(sep);

            // Übungen-Container
            LinearLayout exerciseContainer = new LinearLayout(requireContext());
            exerciseContainer.setOrientation(LinearLayout.VERTICAL);
            inner.addView(exerciseContainer);
            dayContainers.put(dayOfWeek, exerciseContainer);

            // "+ Übung hinzufügen" Button
            MaterialButton addBtn = new MaterialButton(requireContext(),
                null, com.google.android.material.R.attr.materialButtonOutlinedStyle);
            LinearLayout.LayoutParams btnParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
            btnParams.setMargins(0, dp8, 0, 0);
            addBtn.setLayoutParams(btnParams);
            addBtn.setText(R.string.btn_add_exercise_to_day);
            addBtn.setTextSize(12f);
            addBtn.setOnClickListener(v -> showAddExerciseDialog(dayOfWeek));
            inner.addView(addBtn);

            card.addView(inner);
            binding.llDaysContainer.addView(card);
        }
    }

    private void setupButtons() {
        binding.btnNewPlan.setOnClickListener(v -> showCreatePlanDialog());
        binding.btnDeletePlan.setOnClickListener(v -> showDeletePlanDialog());
    }

    private void observeData() {
        viewModel.allPlans.observe(getViewLifecycleOwner(), plans -> {
            currentPlans = plans != null ? plans : new ArrayList<>();
            List<String> names = new ArrayList<>();
            for (TrainingPlan p : currentPlans) names.add(p.getName());
            plansDropdownAdapter.clear();
            plansDropdownAdapter.addAll(names);

            if (currentPlans.isEmpty()) {
                binding.tvEmptyPlan.setVisibility(View.VISIBLE);
                binding.llDaysContainer.setVisibility(View.GONE);
                binding.dropdownPlan.setText("", false);
            } else {
                binding.tvEmptyPlan.setVisibility(View.GONE);
                binding.llDaysContainer.setVisibility(View.VISIBLE);

                Integer selectedId = viewModel.getSelectedPlanId();
                boolean found = false;
                if (selectedId != null && selectedId > 0) {
                    for (TrainingPlan p : currentPlans) {
                        if (p.getId() == selectedId) {
                            binding.dropdownPlan.setText(p.getName(), false);
                            found = true;
                            break;
                        }
                    }
                }
                if (!found) {
                    binding.dropdownPlan.setText(currentPlans.get(0).getName(), false);
                    viewModel.selectPlan(currentPlans.get(0).getId());
                }
            }
        });

        viewModel.allExercises.observe(getViewLifecycleOwner(), exercises -> {
            currentExercises = exercises != null ? exercises : new ArrayList<>();
        });

        viewModel.planExercises.observe(getViewLifecycleOwner(), this::updateDaySections);
    }

    private void updateDaySections(List<PlanExerciseWithName> exercises) {
        // Alle Container leeren
        for (int day = 1; day <= 7; day++) {
            LinearLayout container = dayContainers.get(day);
            if (container != null) container.removeAllViews();
        }

        if (exercises == null) return;

        for (PlanExerciseWithName pe : exercises) {
            LinearLayout container = dayContainers.get(pe.planExercise.getDayOfWeek());
            if (container != null) addExerciseRow(container, pe);
        }
    }

    private void addExerciseRow(LinearLayout container, PlanExerciseWithName pe) {
        ItemPlanExerciseBinding row = ItemPlanExerciseBinding.inflate(
            LayoutInflater.from(requireContext()), container, false);

        String name = pe.exercise != null ? pe.exercise.getName() : "–";
        row.tvPlanExerciseName.setText(name);
        row.btnRemovePlanExercise.setOnClickListener(v ->
            viewModel.removePlanExercise(pe.planExercise));

        container.addView(row.getRoot());
    }

    private void showCreatePlanDialog() {
        EditText input = new EditText(requireContext());
        input.setHint(getString(R.string.dialog_plan_name_hint));
        input.setSingleLine(true);
        input.setInputType(android.text.InputType.TYPE_CLASS_TEXT
            | android.text.InputType.TYPE_TEXT_FLAG_CAP_WORDS);

        int px = (int) (20 * getResources().getDisplayMetrics().density);
        android.widget.FrameLayout container = new android.widget.FrameLayout(requireContext());
        android.widget.FrameLayout.LayoutParams lp = new android.widget.FrameLayout.LayoutParams(
            android.widget.FrameLayout.LayoutParams.MATCH_PARENT,
            android.widget.FrameLayout.LayoutParams.WRAP_CONTENT);
        lp.setMargins(px, px / 2, px, 0);
        input.setLayoutParams(lp);
        container.addView(input);

        // null-Listener → Dialog schließt NICHT automatisch bei OK
        AlertDialog dialog = new AlertDialog.Builder(requireContext())
            .setTitle(R.string.dialog_create_plan_title)
            .setView(container)
            .setPositiveButton(android.R.string.ok, null)
            .setNegativeButton(android.R.string.cancel, null)
            .create();

        dialog.show();

        // Tastatur sofort öffnen
        input.requestFocus();
        InputMethodManager imm = (InputMethodManager)
            requireContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.showSoftInput(input, InputMethodManager.SHOW_IMPLICIT);
        }

        // OK-Button übernehmen: bei leerem Feld Fehler zeigen statt schließen
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(v -> {
            String name = input.getText().toString().trim();
            if (name.isEmpty()) {
                input.setError(getString(R.string.dialog_plan_name_hint));
                return;
            }
            viewModel.createPlan(name);
            dialog.dismiss();
            if (binding != null) {
                Snackbar.make(binding.getRoot(),
                    "Plan \"" + name + "\" erstellt", Snackbar.LENGTH_SHORT).show();
            }
        });
    }

    private void showDeletePlanDialog() {
        if (viewModel.getSelectedPlanId() == null || viewModel.getSelectedPlanId() <= 0) return;

        new AlertDialog.Builder(requireContext())
            .setTitle(R.string.dialog_delete_plan_title)
            .setMessage(R.string.dialog_delete_plan_message)
            .setPositiveButton(android.R.string.ok, (dialog, which) -> {
                viewModel.deleteSelectedPlan();
                binding.dropdownPlan.setText("", false);
            })
            .setNegativeButton(android.R.string.cancel, null)
            .show();
    }

    private void showAddExerciseDialog(int dayOfWeek) {
        if (currentExercises.isEmpty()) {
            new AlertDialog.Builder(requireContext())
                .setMessage(getString(R.string.toast_no_exercises))
                .setPositiveButton(android.R.string.ok, null)
                .show();
            return;
        }

        String[] names = new String[currentExercises.size()];
        for (int i = 0; i < currentExercises.size(); i++) {
            names[i] = currentExercises.get(i).getName();
        }

        new AlertDialog.Builder(requireContext())
            .setTitle(R.string.dialog_add_exercise_title)
            .setItems(names, (dialog, which) -> {
                int exerciseId = currentExercises.get(which).getId();
                viewModel.addExerciseToPlan(dayOfWeek, exerciseId);
            })
            .setNegativeButton(android.R.string.cancel, null)
            .show();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
