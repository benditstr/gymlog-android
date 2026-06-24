package de.th.nuernberg.bme.gymlog.ui.log;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import de.th.nuernberg.bme.gymlog.databinding.ItemWorkoutSetBinding;
import de.th.nuernberg.bme.gymlog.model.WorkoutSetWithExercise;

public class WorkoutSetAdapter extends RecyclerView.Adapter<WorkoutSetAdapter.SetViewHolder> {

    private List<WorkoutSetWithExercise> sets = new ArrayList<>();
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy", Locale.GERMANY);

    public void setSets(List<WorkoutSetWithExercise> sets) {
        this.sets = sets != null ? sets : new ArrayList<>();
        notifyDataSetChanged();
    }

    public WorkoutSetWithExercise getSetAt(int position) {
        return sets.get(position);
    }

    @NonNull
    @Override
    public SetViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemWorkoutSetBinding binding = ItemWorkoutSetBinding.inflate(
            LayoutInflater.from(parent.getContext()), parent, false);
        return new SetViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull SetViewHolder holder, int position) {
        WorkoutSetWithExercise item = sets.get(position);
        holder.bind(item);
    }

    @Override
    public int getItemCount() { return sets.size(); }

    class SetViewHolder extends RecyclerView.ViewHolder {
        private final ItemWorkoutSetBinding binding;

        SetViewHolder(ItemWorkoutSetBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        void bind(WorkoutSetWithExercise item) {
            String exerciseName = item.exercise != null ? item.exercise.getName() : "–";
            binding.tvExerciseName.setText(exerciseName);

            float weight = item.workoutSet.getWeight();
            int reps = item.workoutSet.getReps();
            String weightStr = weight == (int) weight
                ? String.valueOf((int) weight)
                : String.valueOf(weight);
            binding.tvWeightReps.setText(weightStr + " kg × " + reps);

            binding.tvDate.setText(dateFormat.format(new Date(item.workoutSet.getDate())));
        }
    }
}