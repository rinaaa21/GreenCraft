package com.example.app_craftideas;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AddFragment extends Fragment {

    private static final int PICK_IMAGE_REQUEST = 1;
    private static final String TAG = "AddFragment";

    private EditText etTitle, etDescription;
    private Button btnSave, btnChooseImage;
    private ImageView imageView;
    private Bitmap bitmap;
    private ApiService apiService;
    private int userId;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add, container, false);

        // Inisialisasi views
        etTitle = view.findViewById(R.id.etTitle);
        etDescription = view.findViewById(R.id.etDescription);
        btnSave = view.findViewById(R.id.btnSave);
        btnChooseImage = view.findViewById(R.id.btnChooseImage);
        imageView = view.findViewById(R.id.imageView);

        // Mendapatkan instance ApiService dari ApiClient
        apiService = ApiClient.getApiService(getContext());

        // Mengganti ini dengan userId sesuai sesi login atau shared preferences
        userId = 1;

        // OnClickListener untuk tombol memilih gambar
        btnChooseImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openFileChooser();
            }
        });

        // OnClickListener untuk tombol simpan
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveCraft();
            }
        });

        return view;
    }

    // Metode untuk membuka pemilih file gambar
    private void openFileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
    }

    // Metode untuk menangani hasil dari pemilihan gambar
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null && data.getData() != null) {
            Uri filePath = data.getData();
            try {
                // Konversi URI gambar ke bitmap
                bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), filePath);
                imageView.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    // Metode untuk menyimpan ide baru beserta gambar ke server
    private void saveCraft() {
        String title = etTitle.getText().toString().trim();
        String description = etDescription.getText().toString().trim();

        if (title.isEmpty()) {
            etTitle.setError("Title is required");
            etTitle.requestFocus();
            return;
        }

        if (description.isEmpty()) {
            etDescription.setError("Description is required");
            etDescription.requestFocus();
            return;
        }

        if (bitmap == null) {
            Toast.makeText(getContext(), "Please select an image", Toast.LENGTH_SHORT).show();
            return;
        }

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
        byte[] imageBytes = byteArrayOutputStream.toByteArray();

        RequestBody titleBody = RequestBody.create(MediaType.parse("text/plain"), title);
        RequestBody descriptionBody = RequestBody.create(MediaType.parse("text/plain"), description);
        RequestBody userIdBody = RequestBody.create(MediaType.parse("text/plain"), String.valueOf(userId));
        RequestBody imageBody = RequestBody.create(MediaType.parse("image/*"), imageBytes);
        MultipartBody.Part imagePart = MultipartBody.Part.createFormData("image", "image.jpg", imageBody);

        Call<ResponseBody> call = apiService.addIdeaWithImage(titleBody, descriptionBody, userIdBody, imagePart);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    Log.d(TAG, "Idea added successfully: " + response.body().toString());
                    Toast.makeText(getContext(), "Idea added successfully", Toast.LENGTH_SHORT).show();
                    etTitle.setText("");
                    etDescription.setText("");
                    imageView.setImageResource(R.drawable.cam);
                    getActivity().getSupportFragmentManager().popBackStack();
                } else {
                    try {
                        String errorBody = response.errorBody().string();
                        Log.e(TAG, "Failed to add idea: " + errorBody);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    Toast.makeText(getContext(), "Failed to add idea", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.e(TAG, "Error: " + t.getMessage());
                Toast.makeText(getContext(), "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}