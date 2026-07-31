package com.example.data.remote

import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query
import java.util.concurrent.TimeUnit

data class RemotiveJob(
    val id: Long,
    val url: String,
    val title: String,
    val company_name: String,
    val company_logo: String?,
    val category: String,
    val job_type: String,
    val publication_date: String,
    val candidate_required_location: String,
    val salary: String,
    val description: String
)

data class RemotiveResponse(
    val job_count: Int,
    val jobs: List<RemotiveJob>
)

interface RemotiveApiService {
    @GET("api/remote-jobs")
    suspend fun getRemoteJobs(
        @Query("search") query: String? = null,
        @Query("limit") limit: Int = 20
    ): RemotiveResponse
}

object JobClient {
    private const val BASE_URL = "https://remotive.com/"

    private val okHttpClient = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .build()

    private val moshi = Moshi.Builder()
        .add(KotlinJsonAdapterFactory())
        .build()

    val service: RemotiveApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()
            .create(RemotiveApiService::class.java)
    }
}
