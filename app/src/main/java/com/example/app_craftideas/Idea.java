package com.example.app_craftideas;

import java.io.Serializable;

public class Idea implements Serializable {
    private int id;                 // ID unik dari ide
    private String title;           // Judul dari ide
    private String description;     // Deskripsi atau konten dari ide
    private String imageUrl;        // URL gambar terkait dengan ide
    private int userId;             // ID pengguna yang membuat ide ini

    // Constructor kosong untuk keperluan serialisasi
    public Idea() {
    }

    // Constructor dengan semua parameter
    public Idea(int id, String title, String description, String imageUrl, int userId) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.imageUrl = imageUrl;
        this.userId = userId;
    }

    // Getter untuk mendapatkan ID ide
    public int getId() {
        return id;
    }

    // Setter untuk mengatur ID ide
    public void setId(int id) {
        this.id = id;
    }

    // Getter untuk mendapatkan judul ide
    public String getTitle() {
        return title;
    }

    // Setter untuk mengatur judul ide
    public void setTitle(String title) {
        this.title = title;
    }

    // Getter untuk mendapatkan deskripsi ide
    public String getDescription() {
        return description;
    }

    // Setter untuk mengatur deskripsi ide
    public void setDescription(String description) {
        this.description = description;
    }

    // Getter untuk mendapatkan URL gambar ide
    public String getImageUrl() {
        return imageUrl;
    }

    // Setter untuk mengatur URL gambar ide
    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    // Getter untuk mendapatkan ID pengguna yang membuat ide
    public int getUserId() {
        return userId;
    }

    // Setter untuk mengatur ID pengguna yang membuat ide
    public void setUserId(int userId) {
        this.userId = userId;
    }
}
