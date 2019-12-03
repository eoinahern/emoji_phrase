package com.example.repository

import com.example.model.*
import com.zaxxer.hikari.*
import kotlinx.coroutines.*
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.*

object DatabaseFactory {

    fun init() {
        Database.connect(hikari())

        transaction {
            SchemaUtils.create(EmojiPhrases)

            EmojiPhrases.insert {
                it[emoji] = "emoji"
                it[phrase] = "a great emoji"
            }

            EmojiPhrases.insert {
                it[emoji] = "house"
                it[phrase] = "someone buy me a house"
            }
        }
    }

    private fun hikari(): HikariDataSource {
        val config = HikariConfig()
        config.driverClassName = "org.h2.Driver"
        config.jdbcUrl = "jdbc:h2:mem:test"
        config.maximumPoolSize = 3
        config.isAutoCommit = false
        config.transactionIsolation = "TRANSACTION_REPEATABLE_READ"
        config.validate()
        return HikariDataSource(config)
    }

    suspend fun  <T> query(block : () -> T) : T {
        return withContext(Dispatchers.IO) {
            transaction { block() }
        }
    }
}