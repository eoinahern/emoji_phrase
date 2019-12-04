package com.example.webapp

import com.example.*
import io.ktor.application.*
import io.ktor.locations.*
import io.ktor.routing.*


const val SIGNOUT = "/signout"

@Location(SIGNOUT)
class Signout


fun Route.signout() {
    get<Signout> {
        call.redirect(Signin())
    }
}