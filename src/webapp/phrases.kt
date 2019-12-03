package com.example.webapp

import com.example.*
import com.example.model.*
import com.example.repository.*
import com.squareup.moshi.*
import io.ktor.application.*
import io.ktor.auth.*
import io.ktor.freemarker.*
import io.ktor.locations.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*
import java.lang.IllegalArgumentException


const val PHRASES_URL = "/phrases"

@Location(PHRASES_URL)
class Phrases

fun Route.phrases(db: Repository) {

    authenticate("auth") {
        get<Phrases> {

            val user = call.authentication.principal as User
            val phrases = db.phrases()
            call.respond(
                FreeMarkerContent(
                    "phrases.ftl", mapOf(
                        "phrases" to phrases,
                        "displayName" to user.displayName
                    )
                )
            )
        }

        post<Phrases> {
            val params = call.receiveParameters()

            when (params["action"] ?: throw IllegalArgumentException("no action there lads")) {
                "delete" -> {
                    val id = params["id"] ?: throw IllegalArgumentException("no id found")
                    db.remove(id)
                    return@post
                }
                "add" -> {
                    val emoji = params["emoji"] ?: throw IllegalArgumentException("emoji not found in params")
                    val phrase = params["phrase"] ?: throw IllegalArgumentException("phrase not found in params")
                    db.add(emoji, phrase)
                }
            }

            call.redirect(Phrases())
        }
    }
}