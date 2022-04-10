package com.waterfly.vendor.network


import com.waterfly.vendor.util.Constants.BASE_URL
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Response
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.time.Duration
import java.util.concurrent.TimeUnit

class RetrofitInstance {
    companion object {

        private val retrofitLogin by lazy {
            val logging = HttpLoggingInterceptor()
            logging.setLevel(HttpLoggingInterceptor.Level.BODY)
            val client = OkHttpClient.Builder()
                .addInterceptor(logging)
                .callTimeout(18,TimeUnit.SECONDS)
                .build()
            Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .build()
        }

        private val updateVendorRequest by lazy {
            val logging = HttpLoggingInterceptor()
            logging.setLevel(HttpLoggingInterceptor.Level.BODY)
            val client = OkHttpClient.Builder()
                .addInterceptor(logging)
                .callTimeout(18,TimeUnit.SECONDS)
                .build()
            Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .build()
        }


        private val updateVendorStatusRequest by lazy {
            val logging = HttpLoggingInterceptor()
            logging.setLevel(HttpLoggingInterceptor.Level.BODY)
            val client = OkHttpClient.Builder()
                .addInterceptor(logging)
                .callTimeout(18,TimeUnit.SECONDS)
                .build()

           /* val okHttpClientBuilder = OkHttpClient.Builder()
            okHttpClientBuilder.addInterceptor(object: Interceptor{
                override fun intercept(chain: Interceptor.Chain): Response {
                    val original = chain.request()
                    val request = original.newBuilder()
                        .header("Accept", "application/http://econnecto.com+json")
                        .header("Connection", "close")
                        .method(original.method, original.body)
                        .build()
                    return chain.proceed(request)
                }
            })
            val client: OkHttpClient = okHttpClientBuilder.addInterceptor(logging)
                .callTimeout(18,TimeUnit.SECONDS)
                .build()
*/
            Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .build()
        }

        private val updateVendorLiveLocationRequest by lazy {
            val logging = HttpLoggingInterceptor()
            logging.setLevel(HttpLoggingInterceptor.Level.BODY)
            val client = OkHttpClient.Builder()
                .addInterceptor(logging)
                .callTimeout(18,TimeUnit.SECONDS)
                .build()
            Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .build()
        }

        private val checkVendorLiveLocationRequest by lazy {
            val logging = HttpLoggingInterceptor()
            logging.setLevel(HttpLoggingInterceptor.Level.BODY)
            val client = OkHttpClient.Builder()
                .addInterceptor(logging)
                .callTimeout(18,TimeUnit.SECONDS)
                .build()
            Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .build()
        }

        val loginApi by lazy {
            retrofitLogin.create(API::class.java)
        }

        val updateVendorApi by lazy {
            updateVendorRequest.create(API::class.java)
        }

        val updateVendorStatus by lazy {
            updateVendorStatusRequest.create(API::class.java)
        }

        val updateVendorLocationStatus by lazy {
            updateVendorLiveLocationRequest.create(API::class.java)
        }

        val checkVendorLocationStatus by lazy {
            checkVendorLiveLocationRequest.create(API::class.java)
        }
    }
}