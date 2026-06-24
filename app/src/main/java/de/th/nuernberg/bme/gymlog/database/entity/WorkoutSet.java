package de.th.nuernberg.bme.gymlog.database.entity;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(
    tableName = "workout_sets",
    foreignKeys = @ForeignKey(
        entity = Exercise.class,
        parentColumns = "id",
        childColumns = "exerciseId",
        onDelete = ForeignKey.CASCADE
    ),
    indices = @Index("exerciseId")
)
public class WorkoutSet {

    @PrimaryKey(autoGenerate = true)
    private int id;

    private int exerciseId;
    private float weight;
    private int reps;
    private long date; // Unix-Timestamp in Millisekunden

    public WorkoutSet(int exerciseId, float weight, int reps, long date) {
        this.exerciseId = exerciseId;
        this.weight = weight;
        this.reps = reps;
        this.date = date;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getExerciseId() { return exerciseId; }
    public void setExerciseId(int exerciseId) { this.exerciseId = exerciseId; }

    public float getWeight() { return weight; }
    public void setWeight(float weight) { this.weight = weight; }

    public int getReps() { return reps; }
    public void setReps(int reps) { this.reps = reps; }

    public long getDate() { return date; }
    public void setDate(long date) { this.date = date; }
}