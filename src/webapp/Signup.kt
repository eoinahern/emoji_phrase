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

const val SIGNUP = "/signup"


@Location(SIGNUP)
data class Signup(
    val userId: String = "",
    val displayName: String = "",
    val email: String = "",
    val error: String = ""
)


fun Route.signup(db: Repository, hashFunction: (String) -> String) {

    post<Signup> {
        val user = call.sessions.get<EPSession>()?.let {
            db.user(it.userId)
        }

        user?.let {
            return@post call.redirect(Phrases())
        }

        val params = call.receive<Parameters>()
        val userId = params["userId"] ?: return@post call.redirect(it)
        val pass = params["password"] ?: return@post call.redirect(it)
        val dispName = params["displayName"] ?: return@post call.redirect(it)
        val email = params["email"] ?: return@post call.redirect(it)

        val signupError = Signup(userId, dispName, email)

        when {
            pass.length < MIN_USER_PASS_LENGTH ->
                call.redirect(signupError.copy(error = "pass length too small!!!"))
            userId.length < MIN_USER_ID_LENGTH ->
                call.redirect(signupError.copy(error = "User name too short. like the rapper!!"))
            !userNameValid(userId) ->
                call.redirect(signupError.copy(error = "Invalid username!!!!"))
            db.user(userId) != null -> call.redirect(signupError.copy(error = "user already exists you fool!!!"))
            else -> {
                val hash = hashFunction(pass)
                val user = User(userId, email, dispName, hash)


                try {
                    db.createUser(user)
                } catch (e: Exception) {
                    when {
                        db.user(userId) != null -> call.redirect(signupError.copy(error = "user already exists!!"))
                        db.userByEmail(email) != null -> call.redirect(signupError.copy(error = "email already exists!!"))
                        else -> {
                            application.log.error("couldnt save user in the DB!!")
                            call.redirect(signupError.copy(error = "Failed to register sorry!!!"))

                        }
                    }
                }

                call.sessions.set(EPSession(user.userId))
                call.redirect(Phrases())

            }

        }
    }

    get<Signup> {
        val user = call.sessions.get<EPSession>()?.let { db.user(it.userId) }
        if (user != null) call.redirect(Phrases()) else call.respond(
            FreeMarkerContent(
                "signup.ftl",
                mapOf("error" to it.error)
            )
        )
    }
}