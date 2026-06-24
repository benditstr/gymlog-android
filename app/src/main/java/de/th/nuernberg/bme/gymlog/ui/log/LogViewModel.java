package de.th.nuernberg.bme.gymlog.ui.log;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;

import java.util.Collections;
import java.util.List;
import java.util.Locale;

import de.th.nuernberg.bme.gymlog.R;
import de.th.nuernberg.bme.gymlog.database.entity.Exercise;
import de.th.nuernberg.bme.gymlog.database.entity.WorkoutSet;
import de.th.nuernberg.bme.gymlog.model.WorkoutSetWithExercise;
import de.th.nuernberg.bme.gymlog.repository.ExerciseRepository;
import de.th.nuernberg.bme.gymlog.repository.WorkoutSetRepository;
import de.th.nuernberg.bme.gymlog.util.DateUtils;

public class LogViewModel extends AndroidViewModel {

    private final WorkoutSetRepository workoutSetRepository;
    public final LiveData<List<Exercise>> allExercises;
    private final MutableLiveData<Integer> selectedExerciseId = new MutableLiveData<>();
    public final LiveData<List<WorkoutSetWithExercise>> setsForExercise;

    private final MutableLiveData<String> _newPrEvent = new MutableLiveData<>();
    /** Einmaliges Event: enthält die Celebration-Nachricht bei neuem PR. */
    public final LiveData<String> newPrEvent = _newPrEvent;

    public LogViewModel(@NonNull Application application) {
        super(application);
        ExerciseRepository exerciseRepository = new ExerciseRepository(application);
        workoutSetRepository = new WorkoutSetRepository(application);
        allExercises = exerciseRepository.getAllExercises();
        setsForExercise = Transformations.switchMap(selectedExerciseId, id -> {
            if (id == null || id <= 0) {
                return new MutableLiveData<>(Collections.emptyList());
            }
            return workoutSetRepository.getSetsByExercise(id);
        });
    }

    public void selectExercise(int exerciseId) {
        selectedExerciseId.setValue(exerciseId);
    }

    public void addSet(int exerciseId, String exerciseName, float weight, int reps, long date) {
        long normalizedDate = DateUtils.normalizeToMidnight(date);
        WorkoutSet set = new WorkoutSet(exerciseId, weight, reps, normalizedDate);
        workoutSetRepository.insertWithPrCheck(set, isPr -> {
            if (Boolean.TRUE.equals(isPr)) {
                _newPrEvent.postValue(buildPrMessage(exerciseName, weight));
            }
        });
    }

    public void deleteSet(WorkoutSet workoutSet) {
        workoutSetRepository.delete(workoutSet);
    }

    public void clearPrEvent() {
        _newPrEvent.setValue(null);
    }

    private String buildPrMessage(String exerciseName, float weight) {
        String weightStr = weight == (int) weight
            ? String.valueOf((int) weight)
            : String.format(Locale.GERMANY, "%.1f", weight);
        return getApplication().getString(R.string.pr_celebration_title)
            + "  "
            + getApplication().getString(R.string.pr_celebration_message, exerciseName, weightStr);
    }
}