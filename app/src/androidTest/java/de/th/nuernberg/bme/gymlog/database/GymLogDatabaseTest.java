package de.th.nuernberg.bme.gymlog.database;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import android.content.Context;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.room.Room;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import de.th.nuernberg.bme.gymlog.database.dao.ExerciseDao;
import de.th.nuernberg.bme.gymlog.database.dao.WorkoutSetDao;
import de.th.nuernberg.bme.gymlog.database.entity.Exercise;
import de.th.nuernberg.bme.gymlog.database.entity.WorkoutSet;

@RunWith(AndroidJUnit4.class)
public class GymLogDatabaseTest {

    @Rule
    public InstantTaskExecutorRule instantTaskExecutorRule = new InstantTaskExecutorRule();

    private GymLogDatabase db;
    private ExerciseDao exerciseDao;
    private WorkoutSetDao workoutSetDao;

    @Before
    public void createDb() {
        Context context = ApplicationProvider.getApplicationContext();
        db = Room.inMemoryDatabaseBuilder(context, GymLogDatabase.class)
                .allowMainThreadQueries()
                .build();
        exerciseDao = db.exerciseDao();
        workoutSetDao = db.workoutSetDao();
    }

    @After
    public void closeDb() {
        db.close();
    }

    // ── ExerciseDao ───────────────────────────────────────────

    @Test
    public void insertExercise_appearsInGetAll() throws InterruptedException {
        exerciseDao.insert(new Exercise("Bankdrücken"));

        List<Exercise> exercises = getValue(exerciseDao.getAllExercises());

        assertEquals(1, exercises.size());
        assertEquals("Bankdrücken", exercises.get(0).getName());
    }

    @Test
    public void insertMultipleExercises_returnedAlphabetically() throws InterruptedException {
        exerciseDao.insert(new Exercise("Kniebeugen"));
        exerciseDao.insert(new Exercise("Bankdrücken"));
        exerciseDao.insert(new Exercise("Kreuzheben"));

        List<Exercise> exercises = getValue(exerciseDao.getAllExercises());

        assertEquals(3, exercises.size());
        assertEquals("Bankdrücken", exercises.get(0).getName());
        assertEquals("Kniebeugen", exercises.get(1).getName());
        assertEquals("Kreuzheben", exercises.get(2).getName());
    }

    @Test
    public void deleteExercise_removedFromList() throws InterruptedException {
        exerciseDao.insert(new Exercise("Bankdrücken"));
        Exercise inserted = getValue(exerciseDao.getAllExercises()).get(0);

        exerciseDao.delete(inserted);

        List<Exercise> exercises = getValue(exerciseDao.getAllExercises());
        assertTrue(exercises.isEmpty());
    }

    // ── WorkoutSetDao ─────────────────────────────────────────
    @Test
    public void insertWorkoutSet_appearsInGetSetsByExercise() throws InterruptedException {
        exerciseDao.insert(new Exercise("Bankdrücken"));
        int exerciseId = getValue(exerciseDao.getAllExercises()).get(0).getId();

        workoutSetDao.insert(new WorkoutSet(exerciseId, 80f, 8, System.currentTimeMillis()));

        List<?> sets = getValue(workoutSetDao.getSetsByExercise(exerciseId));
        assertEquals(1, sets.size());
    }

    @Test
    public void getPersonalRecord_returnsHighestWeight() throws InterruptedException {
        exerciseDao.insert(new Exercise("Kniebeugen"));
        int exerciseId = getValue(exerciseDao.getAllExercises()).get(0).getId();

        long today = System.currentTimeMillis();
        workoutSetDao.insert(new WorkoutSet(exerciseId, 100f, 5, today));
        workoutSetDao.insert(new WorkoutSet(exerciseId, 120f, 3, today));
        workoutSetDao.insert(new WorkoutSet(exerciseId, 90f,  8, today));

        Float pr = getValue(workoutSetDao.getPersonalRecord(exerciseId));

        assertNotNull(pr);
        assertEquals(120f, pr, 0.01f);
    }

    @Test
    public void getTodaysSetCount_countsOnlyTodayEntries() throws InterruptedException {
        exerciseDao.insert(new Exercise("Rudern"));
        int exerciseId = getValue(exerciseDao.getAllExercises()).get(0).getId();

        long todayStart    = de.th.nuernberg.bme.gymlog.util.DateUtils.todayMidnight();
        long tomorrowStart = de.th.nuernberg.bme.gymlog.util.DateUtils.tomorrowMidnight();
        long yesterday     = todayStart - 1000;

        workoutSetDao.insert(new WorkoutSet(exerciseId, 60f, 10, todayStart));
        workoutSetDao.insert(new WorkoutSet(exerciseId, 65f, 10, todayStart));
        workoutSetDao.insert(new WorkoutSet(exerciseId, 50f, 12, yesterday));

        Integer count = getValue(workoutSetDao.getTodaysSetCount(todayStart, tomorrowStart));

        assertEquals(2, (int) count);
    }

    @Test
    public void deletingExercise_alsoCascadesWorkoutSets() throws InterruptedException {
        exerciseDao.insert(new Exercise("Schulterdrücken"));
        Exercise exercise = getValue(exerciseDao.getAllExercises()).get(0);

        workoutSetDao.insert(new WorkoutSet(exercise.getId(), 50f, 10, System.currentTimeMillis()));
        exerciseDao.delete(exercise);

        List<?> sets = getValue(workoutSetDao.getSetsByExercise(exercise.getId()));
        assertTrue(sets.isEmpty());
    }

    // ── Helper ────────────────────────────────────────────────

    private static <T> T getValue(LiveData<T> liveData) throws InterruptedException {
        final Object[] result = new Object[1];
        CountDownLatch latch = new CountDownLatch(1);
        Observer<T> observer = value -> {
            result[0] = value;
            latch.countDown();
        };
        liveData.observeForever(observer);
        latch.await(2, TimeUnit.SECONDS);
        liveData.removeObserver(observer);
        //noinspection unchecked
        return (T) result[0];
    }
}