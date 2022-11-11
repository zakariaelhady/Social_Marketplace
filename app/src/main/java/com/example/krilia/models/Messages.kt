package com.example.krilia.models

data class Messages(
    val data: MutableList<Message>
)
data class Message(
    val other_id: Int,
    val image: String,
    val image_msg: String,
    val image_exist: Boolean,
    val message: String,
    val message_exist : Boolean,
    val id: Int,
    val date: String,
    val receiver : Boolean,
    val sender: Boolean

)
