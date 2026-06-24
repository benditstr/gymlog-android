package de.th.nuernberg.bme.gymlog.repository;

import android.app.Application;

import androidx.lifecycle.LiveData;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import de.th.nuernberg.bme.gymlog.database.GymLogDatabase;
import de.th.nuernberg.bme.gymlog.database.dao.TrainingPlanDao;
import de.th.nuernberg.bme.gymlog.database.entity.PlanExercise;
import de.th.nuernberg.bme.gymlog.database.entity.TrainingPlan;
import de.th.nuernberg.bme.gymlog.model.PlanExerciseWithName;

public class TrainingPlanRepository {

    private final TrainingPlanDao dao;
    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    public TrainingPlanRepository(Application application) {
        dao = GymLogDatabase.getDatabase(application).trainingPlanDao();
    }

    public LiveData<List<TrainingPlan>> getAllPlans() {
        return dao.getAllPlans();
    }

    public void insert(TrainingPlan plan) {
        executor.execute(() -> dao.insert(plan));
    }

    public void deleteById(int planId) {
        executor.execute(() -> dao.deleteById(planId));
    }

    public LiveData<List<PlanExerciseWithName>> getExercisesForPlan(int planId) {
        return dao.getExercisesForPlan(planId);
    }

    public void insertExercise(PlanExercise planExercise) {
        executor.execute(() -> dao.insertPlanExercise(planExercise));
    }

    public void deletePlanExercise(PlanExercise planExercise) {
        executor.execute(() -> dao.deletePlanExercise(planExercise));
    }
}
