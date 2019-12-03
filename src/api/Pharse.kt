package com.example.api

import com.example.*
import com.example.model.*
import com.example.repository.*
import io.ktor.application.*
import io.ktor.auth.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*

const val PHRASE_ENDPOINT = "$API_VERSION/phrase"


fun Route.phrase(db : Repository) {


    authenticate("auth") {
        post(PHRASE_ENDPOINT) {
            val req = call.receive<Request>()
            val phrase = db.add(EmojiPhrase(req.emoji, req.phrase))
            call.respond(phrase)
        }
    }
}