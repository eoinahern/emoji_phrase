package com.example.webapp

import com.example.*
import com.example.model.*
import io.ktor.application.*
import io.ktor.locations.*
import io.ktor.routing.*
import io.ktor.sessions.*


const val SIGNOUT = "/signout"

@Location(SIGNOUT)
class Signout


fun Route.signout() {
    get<Signout> {
        call.sessions.clear<EPSession>()
        call.redirect(Signin())
    }
}