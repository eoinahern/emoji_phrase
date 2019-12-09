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
import io.ktor.sessions.*
import java.lang.IllegalArgumentException


const val PHRASES_URL = "/phrases"

@Location(PHRASES_URL)
class Phrases

fun Route.phrases(db: Repository, hashFunction: (String) -> String) {


    get<Phrases> {

        val user = call.sessions.get<EPSession>()?.let { db.user(it.userId) }


        if (user == null) {
            call.redirect(Signin())
        } else {

            val phrases = db.phrases()
            val date = System.currentTimeMillis()
            val code = call.securityCode(date, user, hashFunction)
            call.respond(
                FreeMarkerContent(
                    "phrases.ftl", mapOf(
                        "phrases" to phrases,
                        "user" to user,
                        "date" to date,
                        "code" to code
                    ),
                    user.userId
                )
            )

        }
    }

    post<Phrases> {

        val user = call.sessions.get<EPSession>()?.let { db.user(it.userId) }
        val params = call.receiveParameters()

        val date = params["date"]?.toLongOrNull() ?: return@post call.redirect(it)
        val code = params["code"] ?: return@post call.redirect(it)


        if (user == null || !call.verifyCode(date, user, code, hashFunction)) {
            call.redirect(Signin())
        }

        when (params["action"] ?: throw IllegalArgumentException("no action there lads")) {
            "delete" -> {
                val id = params["id"] ?: throw IllegalArgumentException("no id found")
                db.remove(id)
            }
            "add" -> {
                val emoji = params["emoji"] ?: throw IllegalArgumentException("emoji not found in params")
                val phrase = params["phrase"] ?: throw IllegalArgumentException("phrase not found in params")
                db.add(user!!.userId, emoji, phrase)
            }
        }

        call.redirect(Phrases())
    }
}