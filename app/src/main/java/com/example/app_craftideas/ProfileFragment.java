package com.example.app_craftideas;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProfileFragment extends Fragment {

    private TextView tvName, tvEmail;
    private ApiService apiService;
    private int userId;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        tvName = view.findViewById(R.id.tvName);
        tvEmail = view.findViewById(R.id.tvEmail);
        Button logoutButton = view.findViewById(R.id.logout_button); // assuming you have defined the logout button in your XML

        // Initialize ApiService
        apiService = ApiClient.getApiService(getContext());

        // Get userId from SharedPreferences
        SharedPreferences sharedPreferences = getContext().getSharedPreferences("MyAppPreferences", Context.MODE_PRIVATE);
        userId = sharedPreferences.getInt("USER_ID", -1);

        // Check if userId is valid
        if (userId != -1) {
            loadUserProfile();
        } else {
            Toast.makeText(getContext(), "User not logged in", Toast.LENGTH_SHORT).show();
            // Handle case where userId is not available, e.g., redirect to login
        }

        // Set onClickListener for logout button
        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Perform logout actions here, such as clearing session or preferences
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.clear();
                editor.apply();

                // For simplicity, let's assume you navigate back to LoginActivity
                Intent intent = new Intent(getContext(), LoginActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                getActivity().finish(); // Close the current activity (ProfileActivity or MainActivity)
            }
        });

        return view;
    }

    private void loadUserProfile() {
        Call<User> call = apiService.getUser(userId);
        call.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                if (response.isSuccessful() && response.body() != null) {
                    User user = response.body();
                    Log.d("ProfileFragment", "User Name: " + user.getName());
                    Log.d("ProfileFragment", "User Email: " + user.getEmail());

                    tvName.setText(user.getName());
                    tvEmail.setText(user.getEmail());
                } else {
                    try {
                        String errorBody = response.errorBody().string();
                        Log.e("ProfileFragment", "Response failed: " + errorBody);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    Toast.makeText(getContext(), "Failed to load user profile", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                Log.e("ProfileFragment", "Request failed: " + t.getMessage());
                Toast.makeText(getContext(), t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}