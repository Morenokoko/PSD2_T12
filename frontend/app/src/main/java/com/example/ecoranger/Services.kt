package com.example.ecoranger

import com.example.ecoranger.data.Activity
import com.example.ecoranger.data.Bin
import com.example.ecoranger.data.CommunityPost
import com.example.ecoranger.data.User
import com.google.gson.GsonBuilder
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Converter
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Part
import retrofit2.http.Path
import retrofit2.http.Query
import java.lang.reflect.Type


///////////////////////////////////////////////
/////////////////////USER//////////////////////
///////////////////////////////////////////////
interface GetUserApiService {
    @GET("/api/users/{userId}")
    suspend fun getUser(@Path("userId") userId: String): User

    companion object {
        fun create(baseUrl: String): GetUserApiService {
            val retrofit = Retrofit.Builder()
                .baseUrl(baseUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
            return retrofit.create(GetUserApiService::class.java)
        }
    }
}
interface UpdateUserPointsApiService {
    @PUT("/api/users/{userId}/points}")
    suspend fun updatePoints(@Path("userId") userId: String, @Body data: Map<String, Any>): ResponseBody

    companion object {
        fun create(baseUrl: String): UpdateUserPointsApiService {
            val retrofit = Retrofit.Builder()
                .baseUrl(baseUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
            return retrofit.create(UpdateUserPointsApiService::class.java)
        }
    }
}

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
    suspend fun getUserActivity(@Body data: RequestBody): List<Activity>

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



///////////////////////////////////////////////
///////////////COMMUNITY POSTS/////////////////
///////////////////////////////////////////////
interface CreatePostApiService {
    @POST("/api/create_community_post")
    @JvmSuppressWildcards
    suspend fun createPost(@Body data: Map<String, Any>): ResponseBody

    companion object {
        fun create(baseUrl: String): CreatePostApiService {
            val retrofit = Retrofit.Builder()
                .baseUrl(baseUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
            return retrofit.create(CreatePostApiService::class.java)
        }
    }
}
interface GetCommPostApiService {
    @GET("/api/community")
    suspend fun getCommPosts(): List<CommunityPost>

    companion object {
        fun create(baseUrl: String): GetCommPostApiService {
            val retrofit = Retrofit.Builder()
                .baseUrl(baseUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
            return retrofit.create(GetCommPostApiService::class.java)
        }
    }
}
interface GetPostByIdApiService {
    @GET("/api/community/{postId}")
    suspend fun getCommPost(@Path("postId") postId: String): CommunityPost

    companion object {
        fun create(baseUrl: String): GetPostByIdApiService {
            val retrofit = Retrofit.Builder()
                .baseUrl(baseUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
            return retrofit.create(GetPostByIdApiService::class.java)
        }
    }
}
interface UpdatePostApiService {
    @PUT("/api/community/{postId}")
    suspend fun updatePost(@Path("postId") postId: String, @Body data: Map<String, Any>): ResponseBody

    companion object {
        fun create(baseUrl: String): UpdatePostApiService {
            val retrofit = Retrofit.Builder()
                .baseUrl(baseUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
            return retrofit.create(UpdatePostApiService::class.java)
        }
    }
}

interface DeletePostApiService {
    @DELETE("/api/community/{postId}")
    suspend fun deletePost(@Path("postId") postId: String): ResponseBody

    companion object {
        fun create(baseUrl: String): DeletePostApiService {
            val retrofit = Retrofit.Builder()
                .baseUrl(baseUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
            return retrofit.create(DeletePostApiService::class.java)
        }
    }
}