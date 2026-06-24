package de.th.nuernberg.bme.gymlog.ui.plan;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;

import java.util.Collections;
import java.util.List;

import de.th.nuernberg.bme.gymlog.database.entity.Exercise;
import de.th.nuernberg.bme.gymlog.database.entity.PlanExercise;
import de.th.nuernberg.bme.gymlog.database.entity.TrainingPlan;
import de.th.nuernberg.bme.gymlog.model.PlanExerciseWithName;
import de.th.nuernberg.bme.gymlog.repository.ExerciseRepository;
import de.th.nuernberg.bme.gymlog.repository.TrainingPlanRepository;

public class PlanViewModel extends AndroidViewModel {

    private final TrainingPlanRepository planRepo;
    public final LiveData<List<TrainingPlan>> allPlans;
    public final LiveData<List<Exercise>> allExercises;

    private final MutableLiveData<Integer> selectedPlanId = new MutableLiveData<>(0);
    public final LiveData<List<PlanExerciseWithName>> planExercises;

    public PlanViewModel(@NonNull Application application) {
        super(application);
        planRepo = new TrainingPlanRepository(application);
        allPlans = planRepo.getAllPlans();
        allExercises = new ExerciseRepository(application).getAllExercises();
        planExercises = Transformations.switchMap(selectedPlanId, id -> {
            if (id == null || id <= 0) return new MutableLiveData<>(Collections.emptyList());
            return planRepo.getExercisesForPlan(id);
        });
    }

    public void selectPlan(int planId) {
        selectedPlanId.setValue(planId);
    }

    public Integer getSelectedPlanId() {
        return selectedPlanId.getValue();
    }

    public void createPlan(String name) {
        if (name != null && !name.trim().isEmpty()) {
            planRepo.insert(new TrainingPlan(name.trim()));
        }
    }

    public void deleteSelectedPlan() {
        Integer id = selectedPlanId.getValue();
        if (id != null && id > 0) {
            planRepo.deleteById(id);
            selectedPlanId.setValue(0);
        }
    }

    public void addExerciseToPlan(int dayOfWeek, int exerciseId) {
        Integer planId = selectedPlanId.getValue();
        if (planId != null && planId > 0) {
            planRepo.insertExercise(new PlanExercise(planId, dayOfWeek, exerciseId));
        }
    }

    public void removePlanExercise(PlanExercise planExercise) {
        planRepo.deletePlanExercise(planExercise);
    }
}
