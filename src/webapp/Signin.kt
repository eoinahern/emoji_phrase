package com.example.webapp

import com.example.*
import com.example.model.*
import com.example.repository.*
import io.ktor.application.*
import io.ktor.freemarker.*
import io.ktor.http.*
import io.ktor.locations.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*
import io.ktor.sessions.*

const val SIGNIN_PATH = "/signin"


@Location(SIGNIN_PATH)
data class Signin(val userId: String = "", val error: String = "")


fun Route.signin(db: Repository, hashfunction: (String) -> String) {


    post<Signin> {
        val params = call.receive<Parameters>()

        val userId = params["userId"] ?: return@post call.redirect(it)
        val password = params["password"] ?: return@post call.redirect(it)

        val signInError = Signin(userId)

        val signin = when {
            userId.length < MIN_USER_ID_LENGTH -> null
            password.length < MIN_USER_PASS_LENGTH -> null
            !userNameValid(userId) -> null
            else -> db.user(userId, hashfunction(password))
        }

        if (signin == null) {
            call.redirect(signInError.copy(error = "sign in failed!!"))
        } else {
            call.sessions.set(EPSession(userId))
            call.redirect(Phrases())
        }
    }

    get<Signin> {
        val user = call.sessions.get<EPSession>()?.let { session -> db.user(session.userId) }

        if (user != null) {
            call.redirect(Home())
        } else {
            call.respond(FreeMarkerContent("signin.ftl", mapOf("userId" to it.userId, "error" to it.error), ""))
        }
    }

}