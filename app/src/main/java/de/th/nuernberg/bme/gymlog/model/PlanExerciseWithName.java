package de.th.nuernberg.bme.gymlog.model;

import androidx.room.Embedded;
import androidx.room.Relation;

import de.th.nuernberg.bme.gymlog.database.entity.Exercise;
import de.th.nuernberg.bme.gymlog.database.entity.PlanExercise;

public class PlanExerciseWithName {

    @Embedded
    public PlanExercise planExercise;

    @Relation(parentColumn = "exerciseId", entityColumn = "id")
    public Exercise exercise;
}