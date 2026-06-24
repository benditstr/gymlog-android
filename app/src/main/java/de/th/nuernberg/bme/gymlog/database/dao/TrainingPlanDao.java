package de.th.nuernberg.bme.gymlog.database.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Transaction;

import java.util.List;

import de.th.nuernberg.bme.gymlog.database.entity.PlanExercise;
import de.th.nuernberg.bme.gymlog.database.entity.TrainingPlan;
import de.th.nuernberg.bme.gymlog.model.PlanExerciseWithName;

@Dao
public interface TrainingPlanDao {

    @Insert
    long insert(TrainingPlan plan);

    @Query("DELETE FROM training_plans WHERE id = :planId")
    void deleteById(int planId);

    @Query("SELECT * FROM training_plans ORDER BY name ASC")
    LiveData<List<TrainingPlan>> getAllPlans();

    @Insert
    void insertPlanExercise(PlanExercise planExercise);

    @Delete
    void deletePlanExercise(PlanExercise planExercise);

    @Transaction
    @Query("SELECT * FROM plan_exercises WHERE planId = :planId ORDER BY dayOfWeek ASC, id ASC")
    LiveData<List<PlanExerciseWithName>> getExercisesForPlan(int planId);
}
