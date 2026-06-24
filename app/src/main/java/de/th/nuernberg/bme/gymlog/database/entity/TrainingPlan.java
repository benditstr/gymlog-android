package de.th.nuernberg.bme.gymlog.database.entity;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "training_plans")
public class TrainingPlan {

    @PrimaryKey(autoGenerate = true)
    private int id;

    @NonNull
    private String name;

    public TrainingPlan(@NonNull String name) {
        this.name = name;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    @NonNull
    public String getName() { return name; }
    public void setName(@NonNull String name) { this.name = name; }

    @NonNull
    @Override
    public String toString() { return name; }
}