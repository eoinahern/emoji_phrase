package com.example

import io.ktor.application.*
import io.ktor.freemarker.*
import io.ktor.locations.*
import io.ktor.response.*
import io.ktor.routing.*


const val HOME = "/"
const val ABOUT = "/about"

@Location(HOME)
class Home

fun Route.home() = get<Home> {
    call.respond(FreeMarkerContent("/common/home.ftl", null))
}


fun Route.hello() = get("/hello") { call.respondText("i say hello") }

@Location(ABOUT)
class About


fun Route.about() = get<About> { call.respond(FreeMarkerContent("about.ftl", null)) }