package de.th.nuernberg.bme.gymlog.ui.exercises;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import java.util.List;
import java.util.function.Consumer;

import de.th.nuernberg.bme.gymlog.database.entity.Exercise;
import de.th.nuernberg.bme.gymlog.repository.ExerciseRepository;
import de.th.nuernberg.bme.gymlog.repository.WorkoutSetRepository;

public class ExercisesViewModel extends AndroidViewModel {

    private final ExerciseRepository repository;
    private final WorkoutSetRepository workoutSetRepository;
    public final LiveData<List<Exercise>> allExercises;

    private final MutableLiveData<String> _errorEvent = new MutableLiveData<>();
    public final LiveData<String> errorEvent = _errorEvent;

    public ExercisesViewModel(@NonNull Application application) {
        super(application);
        repository = new ExerciseRepository(application);
        workoutSetRepository = new WorkoutSetRepository(application);
        allExercises = repository.getAllExercises();
    }

    /** Anzahl betroffener Sätze für die Lösch-Bestätigung (UF-01). Callback auf Hintergrund-Thread. */
    public void countSetsForExercise(int exerciseId, Consumer<Integer> callback) {
        workoutSetRepository.countSetsForExercise(exerciseId, callback);
    }

    public void addExercise(String name) {
        String trimmed = name.trim();
        if (!trimmed.isEmpty()) {
            repository.insert(new Exercise(trimmed), _errorEvent::postValue);
        }
    }

    public void deleteExercise(Exercise exercise) {
        repository.delete(exercise);
    }

    public void clearError() {
        _errorEvent.setValue(null);
    }
}
