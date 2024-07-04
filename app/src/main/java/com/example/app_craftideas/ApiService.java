package com.example.app_craftideas;

import java.util.List;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ApiService {

    // Endpoint untuk login pengguna
    @FormUrlEncoded
    @POST("login.php")
    Call<User> login(
            @Field("email") String email,
            @Field("password") String password
    );

    // Endpoint untuk mendapatkan informasi pengguna berdasarkan ID
    @GET("getUser.php")
    Call<User> getUser(@Query("userId") int userId);

    // Endpoint untuk menambahkan ide beserta gambar ke server
    @Multipart
    @POST("addIdea.php")
    Call<ResponseBody> addIdeaWithImage(
            @Part("title") RequestBody title,
            @Part("description") RequestBody description,
            @Part("user_id") RequestBody userId,
            @Part MultipartBody.Part image
    );

    // Endpoint untuk mendapatkan daftar ide dari server
    @GET("getIdeas.php")
    Call<List<Idea>> getIdeas();

    // Endpoint untuk memperbarui data ide di server
    @FormUrlEncoded
    @POST("updateIdea.php")
    Call<ResponseBody> updateIdea(
            @Field("id") int id,
            @Field("title") String title,
            @Field("description") String description,
            @Field("image_url") String image_url
    );

    // Endpoint untuk menghapus ide dari server berdasarkan ID
    @GET("deleteIdea.php")
    Call<ResponseBody> deleteIdea(@Query("id") int id);
}
