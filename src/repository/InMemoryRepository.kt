package com.example.repository

import com.example.model.*
import java.lang.IllegalArgumentException
import java.util.concurrent.atomic.*

class InMemoryRepository : Repository {

    private val counter = AtomicInteger()
    private var phrases = ArrayList<EmojiPhrase>()

    override suspend fun add(phrase: EmojiPhrase): EmojiPhrase {
        if (phrases.contains(phrase)) {
            return phrases.find { it == phrase }!!
        }

        phrase.id = counter.incrementAndGet()
        phrases.add(phrase)
        return phrase
    }

    override suspend fun phrase(id: Int): EmojiPhrase? = phrase(id.toString())

    override suspend fun phrase(id: String): EmojiPhrase? =
        phrases.find { it.id.toString() == id } ?: throw IllegalArgumentException("bugger!!")

    override suspend fun phrases(): ArrayList<EmojiPhrase> = phrases

    override suspend fun remove(emoji: EmojiPhrase) {
        if(!phrases.contains(emoji)) {
            throw IllegalArgumentException("not here man!!")
        }
        phrases.remove(emoji)
    }

    override suspend fun remove(id: Int): Boolean = phrases.remove(phrase(id))

    override suspend fun remove(id: String): Boolean = phrases.remove(phrase(id))

    override suspend fun clear() {
        phrases.clear()
    }
}
