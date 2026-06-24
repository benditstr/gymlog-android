package de.th.nuernberg.bme.gymlog.ui.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import de.th.nuernberg.bme.gymlog.R;
import de.th.nuernberg.bme.gymlog.databinding.FragmentHomeBinding;
import de.th.nuernberg.bme.gymlog.model.WorkoutSetWithExercise;
import de.th.nuernberg.bme.gymlog.ui.log.WorkoutSetAdapter;

public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;
    private HomeViewModel viewModel;
    private WorkoutSetAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = new ViewModelProvider(this).get(HomeViewModel.class);

        setupTodayCard();
        setupRecyclerView();
        observeData();
        observeTodayCount();
        observeStreak();
    }

    private void observeStreak() {
        viewModel.streak.observe(getViewLifecycleOwner(), streak -> {
            if (streak == null || streak == 0) {
                binding.streakBadge.setVisibility(View.GONE);
            } else {
                binding.streakBadge.setVisibility(View.VISIBLE);
                binding.tvStreak.setText(String.valueOf(streak));
            }
        });
    }

    private void setupTodayCard() {
        String[] weekdays = {"Sonntag", "Montag", "Dienstag", "Mittwoch",
                             "Donnerstag", "Freitag", "Samstag"};
        int dayIndex = Calendar.getInstance().get(Calendar.DAY_OF_WEEK) - 1;
        binding.tvTodayDay.setText(weekdays[dayIndex]);

        SimpleDateFormat dateFmt = new SimpleDateFormat("d. MMMM yyyy", Locale.GERMANY);
        binding.tvTodayDate.setText(dateFmt.format(Calendar.getInstance().getTime()));
    }

    private void observeTodayCount() {
        viewModel.todaysSetCount.observe(getViewLifecycleOwner(), count -> {
            if (count == null || count == 0) {
                binding.tvTodayCount.setText(R.string.label_no_sets_today);
            } else {
                binding.tvTodayCount.setText(getString(R.string.label_sets_today, count));
            }
        });
    }

    private void setupRecyclerView() {
        adapter = new WorkoutSetAdapter();
        binding.rvLastEntries.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.rvLastEntries.setAdapter(adapter);
    }

    private void observeData() {
        viewModel.lastTenSets.observe(getViewLifecycleOwner(), this::updateList);
    }

    private void updateList(List<WorkoutSetWithExercise> sets) {
        if (sets == null || sets.isEmpty()) {
            binding.tvEmptyHome.setVisibility(View.VISIBLE);
            binding.rvLastEntries.setVisibility(View.GONE);
        } else {
            binding.tvEmptyHome.setVisibility(View.GONE);
            binding.rvLastEntries.setVisibility(View.VISIBLE);
            adapter.setSets(sets);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}