package com.karasu256.vcnative.data.remote

import com.karasu256.vcnative.data.model.ExampleModel
import retrofit2.Response
import retrofit2.http.*

interface AppApi {

    @GET("/api/v1/example")
    suspend fun getExampleResult(): Response<List<ExampleModel>>

}