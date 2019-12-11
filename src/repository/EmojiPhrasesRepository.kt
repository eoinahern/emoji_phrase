package com.example.repository

import com.example.model.*
import com.example.repository.DatabaseFactory.query
import kotlinx.coroutines.*
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.*
import java.lang.IllegalArgumentException


class EmojiPhrasesRepository : Repository {


    override suspend fun add(userid: String, emojiValue: String, phraseValue: String): EmojiPhrase? {
        return query {

            val insertStatement = EmojiPhrases.insert {
                it[user] = userid
                it[emoji] = emojiValue
                it[phrase] = phraseValue
            }

            val result = insertStatement.resultedValues?.get(0)
            if (result != null) toEmojiPhrase(result) else null
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

    override suspend fun user(userId: String, hash: String?): User? {
        val user = query {
            Users.select {
                (Users.id eq userId)
            }.mapNotNull { toUser(it) }
                .singleOrNull()
        }

        return when {
            user == null -> null
            hash == null -> user
            user.passwordHash == hash -> user
            else -> null
        }
    }

    override suspend fun userByEmail(email: String): User? = query {
        Users.select {
            Users.email.eq(email)
        }.mapNotNull {
            toUser(it)
        }.singleOrNull()
    }

    override suspend fun userById(userId: String): User? = query {
        Users.select { Users.id.eq(userId) }
            .map {
                User(
                    userId, it[Users.email],
                    it[Users.displayName], it[Users.passwordHash]
                )
            }
            .singleOrNull()
    }

    override suspend fun createUser(user: User) = query {

        Users.insert { row ->
            row[id] = user.userId
            row[email] = user.email
            row[displayName] = user.displayName
            row[passwordHash] = user.passwordHash
        }

        Unit
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
            userId = row[EmojiPhrases.user],
            emoji = row[EmojiPhrases.emoji],
            phrase = row[EmojiPhrases.phrase]
        )
    }

    private fun toUser(res: ResultRow): User = User(
        userId = res[Users.id],
        email = res[Users.email],
        displayName = res[Users.displayName],
        passwordHash = res[Users.passwordHash]
    )


}