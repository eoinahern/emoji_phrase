package com.example.model

import org.jetbrains.exposed.dao.*
import java.io.Serializable

data class EmojiPhrase(
    val id: Int,
    val userId: String,
    val emoji: String,
    val phrase: String
) : Serializable

object EmojiPhrases : IntIdTable() {
    val user = varchar("user_is", 20).index()
    val emoji = varchar("emoji", 255)
    val phrase = varchar("phrase", 255)
}