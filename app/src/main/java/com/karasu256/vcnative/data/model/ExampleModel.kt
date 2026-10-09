package com.karasu256.vcnative.data.model

import com.google.gson.annotations.SerializedName

data class ExampleModel(
    @SerializedName("title")
    var title: String?,
    @SerializedName("description")
    var description: String?
)
