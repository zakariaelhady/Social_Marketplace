package com.example.krilia.models

data class Conversation(
    var data: ConvData
)

data class ConvData(
    val conversations: List<Conversations>,
    val firstConversation: Int,
    val auth_image : String
)
data class Conversations(
    val other_id: Int,
    val title: String,
    val image: String,
    val id: Int,
    val name: String,
    val product_id: Int,
    val date: String,
)
