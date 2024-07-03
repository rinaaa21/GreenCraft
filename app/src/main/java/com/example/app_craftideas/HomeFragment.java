package com.example.app_craftideas;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;
import android.app.AlertDialog;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeFragment extends Fragment {

    private static final int EDIT_IDEA_REQUEST = 1;
    private RecyclerView recyclerView;
    private IdeaAdapter ideaAdapter;
    private List<Idea> ideaList = new ArrayList<>();
    private List<Idea> filteredIdeaList = new ArrayList<>();
    private ApiService apiService;
    private SearchView searchView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate layout untuk fragment ini
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        // Inisialisasi RecyclerView dan set layout manager-nya
        recyclerView = view.findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        // Inisialisasi SearchView
        searchView = view.findViewById(R.id.search);

        // Inisialisasi adapter dengan list kosong dan set adapter pada RecyclerView
        ideaAdapter = new IdeaAdapter(filteredIdeaList, new IdeaAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Idea idea) {
                // Tampilkan dialog dengan detail ide
                showIdeaDetailsDialog(idea);
            }

            @Override
            public void onItemEdit(Idea idea) {
                // Menjalankan EditIdeaActivity untuk mengedit idea dengan hasil balikan
                Intent intent = new Intent(getContext(), EditIdeaActivity.class);
                intent.putExtra("idea", idea);
                startActivityForResult(intent, EDIT_IDEA_REQUEST);
            }

            @Override
            public void onItemDelete(Idea idea) {
                // Menghapus ide dari server
                deleteIdea(idea.getId());
            }
        });
        recyclerView.setAdapter(ideaAdapter);

        // Menginisialisasi ApiService menggunakan ApiClient
        apiService = ApiClient.getApiService(getContext());

        // Memuat daftar ide dari server
        loadIdeas();

        // Set up SearchView listener
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                filterIdeas(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                filterIdeas(newText);
                return false;
            }
        });

        return view;
    }

    // Method untuk memuat daftar ide dari server menggunakan Retrofit
    private void loadIdeas() {
        Call<List<Idea>> call = apiService.getIdeas();
        call.enqueue(new Callback<List<Idea>>() {
            @Override
            public void onResponse(Call<List<Idea>> call, Response<List<Idea>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    // Bersihkan ideaList dan tambahkan semua ide dari response
                    ideaList.clear();
                    ideaList.addAll(response.body());
                    // Set empty image URL if null or empty
                    for (Idea idea : ideaList) {
                        if (idea.getImageUrl() == null || idea.getImageUrl().isEmpty()) {
                            idea.setImageUrl("http://192.168.117.210:8080/craft1/uploads/default.jpg"); // URL default jika kosong
                        }
                    }
                    // Copy ideaList ke filteredIdeaList dan update RecyclerView
                    filteredIdeaList.clear();
                    filteredIdeaList.addAll(ideaList);
                    ideaAdapter.notifyDataSetChanged();
                } else {
                    // Tampilkan pesan gagal jika tidak berhasil memuat ide
                    Toast.makeText(getContext(), "Gagal memuat ide", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Idea>> call, Throwable t) {
                // Tampilkan pesan kesalahan jika gagal melakukan request
                Toast.makeText(getContext(), "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Method untuk menghapus ide dari server
    private void deleteIdea(int id) {
        Call<ResponseBody> call = apiService.deleteIdea(id);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    // Tampilkan pesan sukses jika ide berhasil dihapus
                    Toast.makeText(getContext(), "Idea berhasil dihapus", Toast.LENGTH_SHORT).show();

                    // Hapus ide dari ideaList dan update RecyclerView
                    for (int i = 0; i < ideaList.size(); i++) {
                        if (ideaList.get(i).getId() == id) {
                            ideaList.remove(i);
                            filteredIdeaList.remove(i);
                            ideaAdapter.notifyItemRemoved(i);
                            break;
                        }
                    }
                } else {
                    // Tampilkan pesan gagal jika gagal menghapus ide
                    Toast.makeText(getContext(), "Gagal menghapus idea", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                // Tampilkan pesan kesalahan jika gagal melakukan request
                Toast.makeText(getContext(), "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Handle hasil balikan dari EditIdeaActivity untuk mengupdate ide yang sudah diedit
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == EDIT_IDEA_REQUEST && resultCode == Activity.RESULT_OK && data != null) {
            // Ambil ide yang sudah diedit dari data balikan
            Idea editedIdea = (Idea) data.getSerializableExtra("editedIdea");
            if (editedIdea != null) {
                // Update ide di ideaList dan update RecyclerView
                for (int i = 0; i < ideaList.size(); i++) {
                    if (ideaList.get(i).getId() == editedIdea.getId()) {
                        ideaList.set(i, editedIdea);
                        filteredIdeaList.set(i, editedIdea);
                        ideaAdapter.notifyItemChanged(i);
                        break;
                    }
                }
            }
        }
    }

    // Method untuk menampilkan dialog dengan detail ide
    private void showIdeaDetailsDialog(Idea idea) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_idea_details, null);

        ImageView imageView = dialogView.findViewById(R.id.idea_image);
        TextView titleTextView = dialogView.findViewById(R.id.idea_title);
        TextView descriptionTextView = dialogView.findViewById(R.id.idea_description);

        Glide.with(this).load(idea.getImageUrl()).into(imageView);
        titleTextView.setText(idea.getTitle());
        descriptionTextView.setText(idea.getDescription());

        builder.setView(dialogView)
                .setPositiveButton("OK", (dialog, id) -> dialog.dismiss());

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    // Method untuk memfilter ide berdasarkan query
    private void filterIdeas(String query) {
        filteredIdeaList.clear();
        if (query.isEmpty()) {
            filteredIdeaList.addAll(ideaList);
        } else {
            for (Idea idea : ideaList) {
                if (idea.getTitle().toLowerCase().contains(query.toLowerCase()) ||
                        idea.getDescription().toLowerCase().contains(query.toLowerCase())) {
                    filteredIdeaList.add(idea);
                }
            }
        }
        ideaAdapter.notifyDataSetChanged();
    }
}