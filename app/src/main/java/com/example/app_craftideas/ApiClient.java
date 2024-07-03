package com.example.app_craftideas;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiClient {
    private static Retrofit retrofit = null;

    // Metode untuk mendapatkan instance Retrofit
    public static Retrofit getClient(Context context) {
        // Cek apakah retrofit sudah diinisialisasi sebelumnya
        if (retrofit == null) {
            // Ambil base URL dari SharedPreferences, default jika tidak tersedia
            SharedPreferences sharedPreferences = context.getSharedPreferences("MyAppPreferences", Context.MODE_PRIVATE);
            String baseUrl = sharedPreferences.getString("BASE_URL", "http://192.168.117.210:8080/craft1/");

            // Interceptor untuk log HTTP request dan response
            HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
            loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

            // Setup OkHttpClient dengan logging interceptor
            OkHttpClient client = new OkHttpClient.Builder()
                    .addInterceptor(loggingInterceptor)
                    .build();

            // Konfigurasi Gson untuk deserialisasi JSON
            Gson gson = new GsonBuilder()
                    .setLenient()
                    .create();

            // Konfigurasi Retrofit dengan base URL, OkHttpClient, dan Gson converter
            retrofit = new Retrofit.Builder()
                    .baseUrl(baseUrl)
                    .client(client)
                    .addConverterFactory(GsonConverterFactory.create(gson))
                    .build();
        }
        return retrofit;
    }

    // Metode untuk mendapatkan instance ApiService dari Retrofit
    public static ApiService getApiService(Context context) {
        return getClient(context).create(ApiService.class);
    }
}
