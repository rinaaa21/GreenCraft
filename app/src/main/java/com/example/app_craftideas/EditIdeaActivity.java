package com.example.app_craftideas;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;

import java.io.IOException;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EditIdeaActivity extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 1;

    private Idea idea;
    private EditText editTextTitle;
    private EditText editTextDescription;
    private ImageView imageViewIdea;
    private Uri imageUri;
    private ApiService apiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_idea);

        // Inisialisasi views dari layout
        editTextTitle = findViewById(R.id.editTextTitle);
        editTextDescription = findViewById(R.id.editTextDescription);
        imageViewIdea = findViewById(R.id.imageViewIdea);

        // Inisialisasi ApiService menggunakan ApiClient
        apiService = ApiClient.getApiService(this);

        // Mengambil objek Idea dari Intent
        if (getIntent().hasExtra("idea")) {
            idea = (Idea) getIntent().getSerializableExtra("idea");

            // Mengisi form edit dengan data dari objek Idea
            editTextTitle.setText(idea.getTitle());
            editTextDescription.setText(idea.getDescription());

            // Menampilkan gambar terkait ide jika ada
            String image_url = idea.getImageUrl();
            Log.d("EditIdeaActivity", "Image URL: " + image_url);  // Log URL gambar

            if (image_url != null && !image_url.isEmpty()) {
                Glide.with(this)
                        .load(image_url)
                        .placeholder(R.drawable.cam)
                        .into(imageViewIdea);
            } else {
                imageViewIdea.setImageResource(R.drawable.cam);
            }
        } else {
            // Handle jika objek Idea tidak ada di Intent
            Toast.makeText(this, "Data ide tidak ditemukan", Toast.LENGTH_SHORT).show();
            finish(); // Menutup Activity jika data tidak ditemukan
        }
    }

    // Metode untuk menyimpan perubahan setelah pengeditan
    public void saveChanges(View view) {
        // Mengambil data dari form edit dan menyimpan ke objek Idea
        idea.setTitle(editTextTitle.getText().toString());
        idea.setDescription(editTextDescription.getText().toString());

        // Jika ada gambar yang dipilih, menyimpan URI-nya ke dalam objek Idea
        if (imageUri != null) {
            idea.setImageUrl(imageUri.toString());
        }

        // Memanggil API untuk memperbarui data ide di backend
        apiService.updateIdea(idea.getId(), idea.getTitle(), idea.getDescription(), idea.getImageUrl()).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    // Menyimpan kembali objek Idea ke Intent untuk dikirim kembali ke Activity sebelumnya
                    Intent resultIntent = new Intent();
                    resultIntent.putExtra("editedIdea", idea);
                    setResult(Activity.RESULT_OK, resultIntent);

                    Toast.makeText(EditIdeaActivity.this, "Idea berhasil diperbarui", Toast.LENGTH_SHORT).show();

                    // Menutup Activity EditIdeaActivity dan kembali ke Activity sebelumnya
                    finish();
                } else {
                    Toast.makeText(EditIdeaActivity.this, "Gagal memperbarui idea", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Toast.makeText(EditIdeaActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Metode untuk memilih gambar baru terkait ide
    public void chooseImage(View view) {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Pilih Gambar"), PICK_IMAGE_REQUEST);
    }

    // Metode untuk menangani hasil dari pemilihan gambar
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            imageUri = data.getData();

            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri);
                imageViewIdea.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
