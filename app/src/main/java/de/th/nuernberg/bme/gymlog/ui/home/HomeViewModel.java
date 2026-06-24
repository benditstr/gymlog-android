package de.th.nuernberg.bme.gymlog.ui.home;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Transformations;

import java.util.List;

import de.th.nuernberg.bme.gymlog.model.WorkoutSetWithExercise;
import de.th.nuernberg.bme.gymlog.repository.WorkoutSetRepository;
import de.th.nuernberg.bme.gymlog.util.StreakCalculator;

public class HomeViewModel extends AndroidViewModel {

    public final LiveData<List<WorkoutSetWithExercise>> lastTenSets;
    public final LiveData<Integer> todaysSetCount;
    public final LiveData<Integer> streak;

    public HomeViewModel(@NonNull Application application) {
        super(application);
        WorkoutSetRepository repository = new WorkoutSetRepository(application);
        lastTenSets = repository.getLastTenSets();
        todaysSetCount = repository.getTodaysSetCount();
        streak = Transformations.map(
            repository.getDistinctWorkoutDates(),
            dates -> StreakCalculator.calculateStreak(dates, System.currentTimeMillis())
        );
    }
}
