package de.th.nuernberg.bme.gymlog.database;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteDatabase;

import de.th.nuernberg.bme.gymlog.database.dao.ExerciseDao;
import de.th.nuernberg.bme.gymlog.database.dao.TrainingPlanDao;
import de.th.nuernberg.bme.gymlog.database.dao.WorkoutSetDao;
import de.th.nuernberg.bme.gymlog.database.entity.Exercise;
import de.th.nuernberg.bme.gymlog.database.entity.PlanExercise;
import de.th.nuernberg.bme.gymlog.database.entity.TrainingPlan;
import de.th.nuernberg.bme.gymlog.database.entity.WorkoutSet;

@Database(
    entities = {Exercise.class, WorkoutSet.class, TrainingPlan.class, PlanExercise.class},
    version = 2,
    exportSchema = false
)
public abstract class GymLogDatabase extends RoomDatabase {

    private static volatile GymLogDatabase INSTANCE;

    public abstract ExerciseDao exerciseDao();
    public abstract WorkoutSetDao workoutSetDao();
    public abstract TrainingPlanDao trainingPlanDao();

    static final Migration MIGRATION_1_2 = new Migration(1, 2) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase db) {
            // KI-01: UNIQUE constraint auf exercises.name
            db.execSQL("CREATE TABLE exercises_new (id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, name TEXT NOT NULL)");
            db.execSQL("INSERT OR IGNORE INTO exercises_new SELECT id, name FROM exercises");
            db.execSQL("DROP TABLE exercises");
            db.execSQL("ALTER TABLE exercises_new RENAME TO exercises");
            db.execSQL("CREATE UNIQUE INDEX index_exercises_name ON exercises (name)");

            // Tab 4: training_plans Tabelle
            db.execSQL("CREATE TABLE training_plans (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
                "name TEXT NOT NULL)");

            // Tab 4: plan_exercises Tabelle
            db.execSQL("CREATE TABLE plan_exercises (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
                "planId INTEGER NOT NULL, " +
                "dayOfWeek INTEGER NOT NULL, " +
                "exerciseId INTEGER NOT NULL, " +
                "FOREIGN KEY (planId) REFERENCES training_plans(id) ON DELETE CASCADE, " +
                "FOREIGN KEY (exerciseId) REFERENCES exercises(id) ON DELETE CASCADE)");
            db.execSQL("CREATE INDEX index_plan_exercises_planId ON plan_exercises (planId)");
            db.execSQL("CREATE INDEX index_plan_exercises_exerciseId ON plan_exercises (exerciseId)");
        }
    };

    public static GymLogDatabase getDatabase(Context context) {
        if (INSTANCE == null) {
            synchronized (GymLogDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(
                        context.getApplicationContext(),
                        GymLogDatabase.class,
                        "gymlog_database"
                    )
                    .addMigrations(MIGRATION_1_2)
                    .build();
                }
            }
        }
        return INSTANCE;
    }
}
