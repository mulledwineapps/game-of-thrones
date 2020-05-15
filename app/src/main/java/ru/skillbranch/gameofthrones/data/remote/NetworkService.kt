package ru.skillbranch.gameofthrones.data.remote

import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import ru.skillbranch.gameofthrones.AppConfig

// 43:40 мастер-класса
object NetworkService {
    val api: RestService by lazy {

        // сериализатор
        val moshi = Moshi.Builder()
            // add custom adapters if needed
            .add(KotlinJsonAdapterFactory())
            .build()

        // чтобы в процессе отладки видеть, какие сетевые запросы мы совершаем и что нам приходит в ответ
        val logging = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

        val client = OkHttpClient().newBuilder()
            .addInterceptor(logging)
            .build()

        val retrofit = Retrofit.Builder()
            .client(client)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .baseUrl(AppConfig.BASE_URL)
            .build()

        retrofit.create(RestService::class.java)
    }
}