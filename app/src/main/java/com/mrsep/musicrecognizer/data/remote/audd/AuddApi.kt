package com.mrsep.musicrecognizer.data.remote.audd

import com.mrsep.musicrecognizer.domain.model.RemoteRecognizeResult
import com.mrsep.musicrecognizer.domain.model.Track
import okhttp3.MultipartBody
import retrofit2.http.Body
import retrofit2.http.POST

interface AuddApi {

    @POST("/")
    suspend fun recognize(@Body multipartBody: MultipartBody): RemoteRecognizeResult<Track>

    @POST("/recognizeWithOffset")
    suspend fun recognizeHumming(@Body multipartBody: MultipartBody): String

}