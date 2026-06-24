package de.th.nuernberg.bme.gymlog.database.entity;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(
    tableName = "plan_exercises",
    foreignKeys = {
        @ForeignKey(
            entity = TrainingPlan.class,
            parentColumns = "id",
            childColumns = "planId",
            onDelete = ForeignKey.CASCADE
        ),
        @ForeignKey(
            entity = Exercise.class,
            parentColumns = "id",
            childColumns = "exerciseId",
            onDelete = ForeignKey.CASCADE
        )
    },
    indices = {
        @Index("planId"),
        @Index("exerciseId")
    }
)
public class PlanExercise {

    @PrimaryKey(autoGenerate = true)
    private int id;

    private int planId;
    private int dayOfWeek; // 1=Montag … 7=Sonntag
    private int exerciseId;

    public PlanExercise(int planId, int dayOfWeek, int exerciseId) {
        this.planId = planId;
        this.dayOfWeek = dayOfWeek;
        this.exerciseId = exerciseId;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getPlanId() { return planId; }
    public void setPlanId(int planId) { this.planId = planId; }

    public int getDayOfWeek() { return dayOfWeek; }
    public void setDayOfWeek(int dayOfWeek) { this.dayOfWeek = dayOfWeek; }

    public int getExerciseId() { return exerciseId; }
    public void setExerciseId(int exerciseId) { this.exerciseId = exerciseId; }
}