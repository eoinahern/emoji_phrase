package com.example.webapp

import com.example.repository.*
import io.ktor.application.*
import io.ktor.freemarker.*
import io.ktor.locations.*
import io.ktor.response.*
import io.ktor.routing.*

const val SIGNIN_PATH = "/signin"


@Location(SIGNIN_PATH)
data class Signin(val userId: String = "", val error: String = "")


fun Route.signin(db: Repository, hashfunction: (String) -> String) {

    get<Signin> {
        call.respond(FreeMarkerContent("signin.ktl", null))
    }

}