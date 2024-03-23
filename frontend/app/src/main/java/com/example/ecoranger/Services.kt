package com.example.ecoranger

import com.example.ecoranger.data.Bin
import com.google.gson.GsonBuilder
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Converter
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Query
import java.lang.reflect.Type


///////////////////////////////////////////////
///////////////////ACTIVITY////////////////////
///////////////////////////////////////////////
interface CreateActivityApiService {
    @POST("/api/activities")
    @JvmSuppressWildcards
    suspend fun createActivity(@Body data: Map<String, Any>): ResponseBody

    companion object {
        fun create(baseUrl: String): CreateActivityApiService {
            val retrofit = Retrofit.Builder()
                .baseUrl(baseUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
            return retrofit.create(CreateActivityApiService::class.java)
        }
    }
}

interface GetUserActivityApiService {
    @POST("/api/activities/get_activity_by_userid")
    @JvmSuppressWildcards
    suspend fun getUserActivity(@Body data: Map<String, Any>): ResponseBody

    companion object {
        fun create(baseUrl: String): GetUserActivityApiService {
            val retrofit = Retrofit.Builder()
                .baseUrl(baseUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
            return retrofit.create(GetUserActivityApiService::class.java)
        }
    }
}

///////////////////////////////////////////////
///////////////RECYCLING CENTER////////////////
///////////////////////////////////////////////
class ToStringConverterFactory : Converter.Factory() {
    override fun responseBodyConverter(
        type: Type,
        annotations: Array<Annotation>,
        retrofit: Retrofit
    ): Converter<ResponseBody, *>? {
        // Check if the expected response type is a String, return a converter if so
        if (String::class.java == type) {
            return Converter<ResponseBody, String> { responseBody -> responseBody.string() }
        }
        // Return null to continue searching for other converters if the type is not String
        return null
    }
}

interface GetRecyclingCenterApiService {
    @Headers("Content-Type: text/plain")
    @POST("/recycling_centers/get_bin_by_id")
    suspend fun getRecyclingCenter(@Body address: RequestBody): Response<String>

    companion object {
        fun create(baseUrl: String): GetRecyclingCenterApiService {
            val retrofit = Retrofit.Builder()
                .baseUrl(baseUrl)
                .addConverterFactory(ToStringConverterFactory())
                .addConverterFactory(GsonConverterFactory.create())
                .build()
            return retrofit.create(GetRecyclingCenterApiService::class.java)
        }
    }
}

interface GetRecyclingCenters500mApiService {
    @GET("recycling_bins_500m")
    suspend fun getRecyclingBins(
        @Query("lat") lat: Double,
        @Query("lon") lon: Double
    ): List<Bin>

    companion object {
        fun create(baseUrl: String): GetRecyclingCenters500mApiService {
            val gson = GsonBuilder().setLenient().create() // Enable lenient parsing
            val retrofit = Retrofit.Builder()
                .baseUrl(baseUrl)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build()

            return retrofit.create(GetRecyclingCenters500mApiService::class.java)
        }
    }
}


///////////////////////////////////////////////
////////////////////IMAGE//////////////////////
///////////////////////////////////////////////
interface ImageApiService {
    @Multipart
    @POST("/infer")
    suspend fun inferImage(@Part image: MultipartBody.Part): String

    companion object {
        fun create(baseUrl: String): ImageApiService {
            val retrofit = Retrofit.Builder()
                .baseUrl(baseUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
            return retrofit.create(ImageApiService::class.java)
        }
    }
}