package com.example.api

import com.example.*
import com.example.model.*
import com.example.repository.*
import com.google.gson.*
import io.ktor.application.*
import io.ktor.http.*
import io.ktor.locations.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*
import org.h2.engine.*


const val LOGIN_ENDPOINT = "/login"

@Location(LOGIN_ENDPOINT)
class Login


fun Route.login(db: Repository, jwtService: JWTService) {

    post<Login> {
        val loginUser = call.receive<LoginUserCredentials>()
        //val userId = params["userId"] ?: return@post call.redirect(it)
        //val password = params["password"] ?: return@post call.redirect(it)

        val user = db.user(loginUser.userId, hash(loginUser.password))

        if (user != null) {
            val token = jwtService.generateToken(user)
            call.respondText(token)
        } else {
            call.respondText("Invalid user!!!")
        }
    }

}