package com.tondz.chatbot.services;

import java.util.concurrent.TimeUnit;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitClient {
    private static final String BASE_URL = "http://103.112.211.226:1111/";
    private static Retrofit retrofit = null;

    public static ApiService getApiService() {
        if (retrofit == null) {
            // Cấu hình OkHttpClient với timeout
            OkHttpClient okHttpClient = new OkHttpClient.Builder()
                    .connectTimeout(100, TimeUnit.SECONDS) // Timeout kết nối
                    .readTimeout(100, TimeUnit.SECONDS) // Timeout đọc dữ liệu
                    .writeTimeout(100, TimeUnit.SECONDS) // Timeout ghi dữ liệu
                    .retryOnConnectionFailure(true) // Tự động thử lại khi lỗi kết nối
                    .build();

            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .client(okHttpClient) // Thêm OkHttpClient vào Retrofit
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit.create(ApiService.class);
    }
}
