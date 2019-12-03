package com.example.repository

import com.example.model.*
import com.example.repository.DatabaseFactory.query
import kotlinx.coroutines.*
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.*
import java.lang.IllegalArgumentException


class EmojiPhrasesRepository : Repository {
    override suspend fun add(emojiValue: String, phraseValue: String) {

        transaction {
            EmojiPhrases.insert {
                it[emoji] = emojiValue
                it[phrase] = phraseValue
            }
        }
    }

    override suspend fun phrase(id: Int): EmojiPhrase? = withContext(Dispatchers.IO) {
        transaction {
            EmojiPhrases.select {
                (EmojiPhrases.id eq id)
            }.mapNotNull {
                toEmojiPhrase(it)
            }.singleOrNull()
        }
    }


    override suspend fun phrase(id: String): EmojiPhrase? {
        return phrase(id.toInt())
    }

    override suspend fun phrases(): List<EmojiPhrase> = query {
        EmojiPhrases.selectAll().map { toEmojiPhrase(it) }
    }

    override suspend fun remove(id: Int): Boolean {
        if (phrase(id) == null) {
            throw IllegalArgumentException("id not found in db")
        }
        return query {
            EmojiPhrases.deleteWhere {
                EmojiPhrases.id eq id
            } > 0
        }
    }

    override suspend fun remove(id: String): Boolean {
        return remove(id.toInt())
    }

    override suspend fun clear() {
        EmojiPhrases.deleteAll()
    }

    private fun toEmojiPhrase(row: ResultRow): EmojiPhrase {
        return EmojiPhrase(
            id = row[EmojiPhrases.id].value,
            emoji = row[EmojiPhrases.emoji],
            phrase = row[EmojiPhrases.phrase]
        )
    }


}