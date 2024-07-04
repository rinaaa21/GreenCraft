package com.example.app_craftideas;

import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import android.util.Log;

import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.example.app_craftideas.databinding.ItemIdeaBinding;
import java.util.List;

public class IdeaAdapter extends RecyclerView.Adapter<IdeaAdapter.IdeaViewHolder> {

    // Daftar ide yang akan ditampilkan
    private List<Idea> ideaList;

    // Listener untuk menangani klik item, edit, dan tombol delete
    private OnItemClickListener onItemClickListener;

    // Interface untuk mendefinisikan listener klik
    public interface OnItemClickListener {
        void onItemClick(Idea idea);
        void onItemDelete(Idea idea);
        void onItemEdit(Idea idea); // Metode untuk menangani klik tombol edit
    }

    // Constructor untuk inisialisasi adapter dengan daftar ide dan listener
    public IdeaAdapter(List<Idea> ideaList, OnItemClickListener onItemClickListener) {
        this.ideaList = ideaList;
        this.onItemClickListener = onItemClickListener;
    }

    @NonNull
    @Override
    public IdeaViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflating layout item_idea.xml menggunakan ViewBinding
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        ItemIdeaBinding binding = ItemIdeaBinding.inflate(inflater, parent, false);
        return new IdeaViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull IdeaViewHolder holder, int position) {
        // Mengambil data ide berdasarkan posisi
        Idea idea = ideaList.get(position);
        // Mengikat data ide ke ViewHolder
        holder.bind(idea);

        // Menangani klik pada item
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onItemClickListener.onItemClick(idea);
            }
        });

        // Menangani klik pada tombol delete
        holder.binding.btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onItemClickListener.onItemDelete(idea);
            }
        });

        // Menangani klik pada tombol edit
        holder.binding.btnEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onItemClickListener.onItemEdit(idea);
            }
        });
    }

    @Override
    public int getItemCount() {
        // Mengembalikan jumlah item dalam daftar
        return ideaList.size();
    }

    // Kelas ViewHolder untuk menahan dan mengikat data ke view
    public class IdeaViewHolder extends RecyclerView.ViewHolder {
        // Menggunakan ViewBinding untuk mengakses view dalam item layout
        private final ItemIdeaBinding binding;

        public IdeaViewHolder(@NonNull ItemIdeaBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        // Metode untuk mengikat data ide ke view
        public void bind(Idea idea) {
            // Menampilkan judul ide
            binding.ideaTitle.setText(idea.getTitle());

            // Mengambil URL gambar dari ide
            String imageUrl = idea.getImageUrl();
            Log.d("IdeaAdapter", "Image URL: " + imageUrl);

            // Menggunakan Glide untuk memuat gambar dari URL
            Glide.with(itemView.getContext())
                    .load(imageUrl)
                    .placeholder(R.drawable.ic_camera) // Gambar placeholder saat loading
                    .error(R.drawable.image)          // Gambar default jika terjadi kesalahan
                    .into(binding.ivImage);            // ImageView untuk menampilkan gambar
        }
    }
}