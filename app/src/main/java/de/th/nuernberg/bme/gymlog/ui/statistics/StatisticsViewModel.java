package de.th.nuernberg.bme.gymlog.ui.statistics;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;

import java.util.Collections;
import java.util.List;

import de.th.nuernberg.bme.gymlog.database.entity.Exercise;
import de.th.nuernberg.bme.gymlog.model.MaxWeightPerDay;
import de.th.nuernberg.bme.gymlog.repository.ExerciseRepository;
import de.th.nuernberg.bme.gymlog.repository.WorkoutSetRepository;

public class StatisticsViewModel extends AndroidViewModel {

    private final WorkoutSetRepository workoutSetRepository;
    public final LiveData<List<Exercise>> allExercises;
    private final MutableLiveData<Integer> selectedExerciseId = new MutableLiveData<>();
    public final LiveData<Float> personalRecord;
    public final LiveData<List<MaxWeightPerDay>> chartData;

    public StatisticsViewModel(@NonNull Application application) {
        super(application);
        workoutSetRepository = new WorkoutSetRepository(application);
        ExerciseRepository exerciseRepository = new ExerciseRepository(application);
        allExercises = exerciseRepository.getAllExercises();

        personalRecord = Transformations.switchMap(selectedExerciseId, id -> {
            if (id == null || id <= 0) return new MutableLiveData<>(null);
            return workoutSetRepository.getPersonalRecord(id);
        });

        chartData = Transformations.switchMap(selectedExerciseId, id -> {
            if (id == null || id <= 0) return new MutableLiveData<>(Collections.emptyList());
            return workoutSetRepository.getMaxWeightPerDay(id);
        });
    }

    public void selectExercise(int exerciseId) {
        selectedExerciseId.setValue(exerciseId);
    }
}