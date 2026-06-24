package de.th.nuernberg.bme.gymlog.repository;

import android.app.Application;

import androidx.lifecycle.LiveData;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Consumer;

import de.th.nuernberg.bme.gymlog.database.GymLogDatabase;
import de.th.nuernberg.bme.gymlog.database.dao.ExerciseDao;
import de.th.nuernberg.bme.gymlog.database.entity.Exercise;

public class ExerciseRepository {

    private final ExerciseDao exerciseDao;
    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    public ExerciseRepository(Application application) {
        exerciseDao = GymLogDatabase.getDatabase(application).exerciseDao();
    }

    public LiveData<List<Exercise>> getAllExercises() {
        return exerciseDao.getAllExercises();
    }

    public void insert(Exercise exercise, Consumer<String> onError) {
        executor.execute(() -> {
            try {
                exerciseDao.insert(exercise);
            } catch (android.database.sqlite.SQLiteConstraintException e) {
                if (onError != null) onError.accept("duplicate");
            }
        });
    }

    public void delete(Exercise exercise) {
        executor.execute(() -> exerciseDao.delete(exercise));
    }
}
