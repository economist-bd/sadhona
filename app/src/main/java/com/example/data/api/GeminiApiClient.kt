package com.example.data.api

import com.example.BuildConfig
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.Query
import java.util.concurrent.TimeUnit

@JsonClass(generateAdapter = true)
data class Part(
    @Json(name = "text") val text: String? = null
)

@JsonClass(generateAdapter = true)
data class Content(
    @Json(name = "parts") val parts: List<Part>
)

@JsonClass(generateAdapter = true)
data class GenerateContentRequest(
    @Json(name = "contents") val contents: List<Content>,
    @Json(name = "systemInstruction") val systemInstruction: Content? = null
)

@JsonClass(generateAdapter = true)
data class Candidate(
    @Json(name = "content") val content: Content
)

@JsonClass(generateAdapter = true)
data class GenerateContentResponse(
    @Json(name = "candidates") val candidates: List<Candidate>? = null
)

interface GeminiApiService {
    @POST("v1beta/models/gemini-3.5-flash:generateContent")
    suspend fun generateContent(
        @Query("key") apiKey: String,
        @Body request: GenerateContentRequest
    ): GenerateContentResponse
}

object RetrofitClient {
    private const val BASE_URL = "https://generativelanguage.googleapis.com/"

    private val okHttpClient = OkHttpClient.Builder()
        .connectTimeout(60, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .writeTimeout(60, TimeUnit.SECONDS)
        .build()

    private val moshi = Moshi.Builder()
        .addLast(KotlinJsonAdapterFactory())
        .build()

    val service: GeminiApiService by lazy {
        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()
        retrofit.create(GeminiApiService::class.java)
    }
}

object GeminiApiClient {
    private const val SYSTEM_INSTRUCTION = """
        You are 'সাদনা স্টাডি কোচ' (Sadhana Study Coach), an expert academic mentor and student psychologist.
        Your goal is to help students boost concentration, master time management, handle exam stress, defeat digital/phone addiction, and develop a growth mindset.
        You must ALWAYS respond in a warm, encouraging, compassionate, and natural Bengali language.
        Format your response beautifully using bold text, bullet points, or numbered lists.
        Keep the advice practical, realistic, and highly actionable.
    """

    suspend fun generateStudyAdvice(userMessage: String): String = withContext(Dispatchers.IO) {
        val apiKey = BuildConfig.GEMINI_API_KEY
        if (apiKey.isEmpty() || apiKey == "MY_GEMINI_API_KEY") {
            return@withContext "দুঃখিত, এআই স্টাডি কোচের জন্য এপিআই কী (API Key) সেট আপ করা হয়নি। দয়া করে আপনার AI Studio এর Secrets প্যানেলে GEMINI_API_KEY যোগ করুন।"
        }

        val request = GenerateContentRequest(
            contents = listOf(
                Content(parts = listOf(Part(text = userMessage)))
            ),
            systemInstruction = Content(parts = listOf(Part(text = SYSTEM_INSTRUCTION)))
        )

        try {
            val response = RetrofitClient.service.generateContent(apiKey, request)
            val text = response.candidates?.firstOrNull()?.content?.parts?.firstOrNull()?.text
            text ?: "দুঃখিত, কোনো উপদেশ পাওয়া যায়নি। অনুগ্রহ করে আবার চেষ্টা করুন।"
        } catch (e: Exception) {
            "ত্রুটি ঘটেছে: ${e.localizedMessage ?: "সার্ভারের সাথে সংযোগ করা যাচ্ছে না। অনুগ্রহ করে ইন্টারনেট সংযোগ চেক করে আবার চেষ্টা করুন।"}"
        }
    }
}
