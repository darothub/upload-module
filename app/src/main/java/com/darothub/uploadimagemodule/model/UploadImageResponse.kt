package com.darothub.uploadimagemodule.model


data class UploadImageResponse (
    val status: Long,
    val message: String,
    val payload: Payload
)

data class Payload (

    val fileId: String,
    val fileType: String,
    val fileName: String,
    val downloadUri: String,
    val uploadStatus: Boolean
)