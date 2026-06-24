package de.th.nuernberg.bme.gymlog.database.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Transaction;

import java.util.List;

import de.th.nuernberg.bme.gymlog.database.entity.WorkoutSet;
import de.th.nuernberg.bme.gymlog.model.MaxWeightPerDay;
import de.th.nuernberg.bme.gymlog.model.WorkoutSetWithExercise;

@Dao
public interface WorkoutSetDao {

    @Insert
    void insert(WorkoutSet workoutSet);

    @Delete
    void delete(WorkoutSet workoutSet);

    @Transaction
    @Query("SELECT * FROM workout_sets WHERE exerciseId = :exerciseId ORDER BY date DESC")
    LiveData<List<WorkoutSetWithExercise>> getSetsByExercise(int exerciseId);

    @Transaction
    @Query("SELECT * FROM workout_sets ORDER BY date DESC LIMIT 10")
    LiveData<List<WorkoutSetWithExercise>> getLastTenSets();

    @Query("SELECT MAX(weight) FROM workout_sets WHERE exerciseId = :exerciseId")
    LiveData<Float> getPersonalRecord(int exerciseId);

    @Query("SELECT date, MAX(weight) AS maxWeight FROM workout_sets WHERE exerciseId = :exerciseId GROUP BY date ORDER BY date ASC")
    LiveData<List<MaxWeightPerDay>> getMaxWeightPerDay(int exerciseId);

    @Query("SELECT COUNT(*) FROM workout_sets WHERE date >= :todayStart AND date < :tomorrowStart")
    LiveData<Integer> getTodaysSetCount(long todayStart, long tomorrowStart);

    /** Distinkte Trainings-Datumswerte (absteigend) für die Streak-Berechnung. Rein lesend. */
    @Query("SELECT DISTINCT date FROM workout_sets ORDER BY date DESC")
    LiveData<List<Long>> getDistinctWorkoutDates();

    /** Anzahl gespeicherter Sätze einer Übung (für Lösch-Bestätigung UF-01). Synchron — Hintergrund-Thread. */
    @Query("SELECT COUNT(*) FROM workout_sets WHERE exerciseId = :exerciseId")
    int countSetsForExercise(int exerciseId);

    /** Bisheriger Max-Wert einer Übung (für PR-Check). Synchron — Hintergrund-Thread, nullable. */
    @Query("SELECT MAX(weight) FROM workout_sets WHERE exerciseId = :exerciseId")
    Float getMaxWeightSync(int exerciseId);
}