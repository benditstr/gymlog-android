package de.th.nuernberg.bme.gymlog.repository;

import android.app.Application;

import androidx.lifecycle.LiveData;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Consumer;

import de.th.nuernberg.bme.gymlog.database.GymLogDatabase;
import de.th.nuernberg.bme.gymlog.database.dao.WorkoutSetDao;
import de.th.nuernberg.bme.gymlog.database.entity.WorkoutSet;
import de.th.nuernberg.bme.gymlog.model.MaxWeightPerDay;
import de.th.nuernberg.bme.gymlog.model.WorkoutSetWithExercise;
import de.th.nuernberg.bme.gymlog.util.DateUtils;
import de.th.nuernberg.bme.gymlog.util.PrDetector;

public class WorkoutSetRepository {

    private final WorkoutSetDao workoutSetDao;
    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    public WorkoutSetRepository(Application application) {
        workoutSetDao = GymLogDatabase.getDatabase(application).workoutSetDao();
    }

    public LiveData<List<WorkoutSetWithExercise>> getSetsByExercise(int exerciseId) {
        return workoutSetDao.getSetsByExercise(exerciseId);
    }

    public LiveData<List<WorkoutSetWithExercise>> getLastTenSets() {
        return workoutSetDao.getLastTenSets();
    }

    public LiveData<Float> getPersonalRecord(int exerciseId) {
        return workoutSetDao.getPersonalRecord(exerciseId);
    }

    public LiveData<List<MaxWeightPerDay>> getMaxWeightPerDay(int exerciseId) {
        return workoutSetDao.getMaxWeightPerDay(exerciseId);
    }

    public LiveData<Integer> getTodaysSetCount() {
        long todayStart = DateUtils.todayMidnight();
        long tomorrowStart = DateUtils.tomorrowMidnight();
        return workoutSetDao.getTodaysSetCount(todayStart, tomorrowStart);
    }

    public LiveData<List<Long>> getDistinctWorkoutDates() {
        return workoutSetDao.getDistinctWorkoutDates();
    }

    /** Zählt Sätze einer Übung asynchron; Callback läuft auf dem Hintergrund-Thread (UF-01). */
    public void countSetsForExercise(int exerciseId, Consumer<Integer> callback) {
        executor.execute(() -> callback.accept(workoutSetDao.countSetsForExercise(exerciseId)));
    }

    public void insert(WorkoutSet workoutSet) {
        executor.execute(() -> workoutSetDao.insert(workoutSet));
    }

    /**
     * Fügt einen Satz ein und prüft davor, ob es ein neuer persönlicher Rekord ist.
     * Der Callback (true = neuer PR) läuft auf dem Hintergrund-Thread.
     */
    public void insertWithPrCheck(WorkoutSet workoutSet, Consumer<Boolean> onPrResult) {
        executor.execute(() -> {
            Float previousMax = workoutSetDao.getMaxWeightSync(workoutSet.getExerciseId());
            boolean isPr = PrDetector.isNewPr(workoutSet.getWeight(), previousMax);
            workoutSetDao.insert(workoutSet);
            if (onPrResult != null) {
                onPrResult.accept(isPr);
            }
        });
    }

    public void delete(WorkoutSet workoutSet) {
        executor.execute(() -> workoutSetDao.delete(workoutSet));
    }
}