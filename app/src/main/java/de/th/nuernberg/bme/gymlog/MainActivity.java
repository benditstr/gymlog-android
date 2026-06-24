package de.th.nuernberg.bme.gymlog;

import android.os.Bundle;
import android.view.ViewOutlineProvider;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.NavigationUI;

import de.th.nuernberg.bme.gymlog.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {

    private static final float NAV_BLUR_RADIUS = 20f;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityMainBinding binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager()
            .findFragmentById(R.id.nav_host_fragment);
        NavController navController = navHostFragment.getNavController();
        NavigationUI.setupWithNavController(binding.bottomNav, navController);

        setupNavBlur(binding);
    }

    /** Echter Backdrop-Blur für die floatende Bottom-Nav (BlurView, Spec 2). */
    private void setupNavBlur(ActivityMainBinding binding) {
        binding.blurView.setupWith(binding.blurTarget).setBlurRadius(NAV_BLUR_RADIUS);
        // Rundung: Outline aus dem Background-Drawable (22dp) clippen
        binding.blurView.setOutlineProvider(ViewOutlineProvider.BACKGROUND);
        binding.blurView.setClipToOutline(true);
    }
}
