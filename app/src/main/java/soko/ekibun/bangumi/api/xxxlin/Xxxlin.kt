package soko.ekibun.bangumi.api.xxxlin

import android.os.Build
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST
import soko.ekibun.bangumi.BuildConfig
import soko.ekibun.bangumi.api.xxxlin.bean.BaseResult
import java.util.*

/**
 * 错误报告
 */
interface Xxxlin {
    /**
     * 错误报告
     * @param content String
     * @param appVersionCode Int
     * @param appVersionName String
     * @param deviceManufacturer String
     * @param deviceBrand String
     * @param deviceModel String
     * @param deviceLanguage String
     * @param deviceVersionRelease String
     * @param deviceSdkInt Int
     * @return Call<BaseResult>
     */
    @FormUrlEncoded
    @POST("/api/soko/bangumi/v1/log/add")
    fun crashReport(@Field("content") content: String,
                    @Field("appVersionCode") appVersionCode: Int = BuildConfig.VERSION_CODE,
                    @Field("appVersionName") appVersionName: String = BuildConfig.VERSION_NAME,
                    @Field("deviceManufacturer") deviceManufacturer: String = Build.MANUFACTURER,
                    @Field("deviceBrand") deviceBrand: String = Build.BRAND,
                    @Field("deviceModel") deviceModel: String = Build.MODEL,
                    @Field("deviceLanguage") deviceLanguage: String = Locale.getDefault().toString(),
                    @Field("deviceVersionRelease") deviceVersionRelease: String = Build.VERSION.RELEASE,
                    @Field("deviceSdkInt") deviceSdkInt: Int = Build.VERSION.SDK_INT): Call<BaseResult>

    companion object {
        private const val SERVER_API = "http://www.Xxxlin.com"
        /**
         * 创建retrofit实例
         * @return Xxxlin
         */
        fun createInstance(): Xxxlin{
            return Retrofit.Builder().baseUrl(SERVER_API)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build().create(Xxxlin::class.java)
        }
    }
}