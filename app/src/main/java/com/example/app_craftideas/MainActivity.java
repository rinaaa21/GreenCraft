package com.example.app_craftideas;

import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    // Map untuk memetakan ID menu ke Fragment
    private Map<Integer, Fragment> fragmentMap = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);

        // Inisialisasi fragmentMap dengan instance Fragment yang sesuai
        fragmentMap.put(R.id.nav_home, new HomeFragment());
        fragmentMap.put(R.id.nav_add, new AddFragment());
        fragmentMap.put(R.id.nav_profile, new ProfileFragment());

        // Menangani perubahan item di BottomNavigationView
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Fragment selectedFragment = fragmentMap.get(item.getItemId());
                if (selectedFragment != null) {
                    switchFragment(selectedFragment);
                    return true;
                }
                return false;
            }
        });

        // Set default selection
        bottomNavigationView.setSelectedItemId(R.id.nav_home);
    }

    // Method untuk mengganti fragment yang ditampilkan
    private void switchFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fragment_container, fragment);
        fragmentTransaction.commit();
    }
}
