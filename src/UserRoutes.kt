package com.example

import com.example.model.*
import com.example.repository.*
import io.ktor.application.*
import io.ktor.freemarker.*
import io.ktor.locations.*
import io.ktor.response.*
import io.ktor.routing.*
import io.ktor.sessions.*


const val HOME = "/"
const val ABOUT = "/about"

@Location(HOME)
class Home

fun Route.home(repo: Repository) = get<Home> {
    val user = call.sessions.get<EPSession>()?.let { repo.user(it.userId) }
    call.respond(FreeMarkerContent("/common/home.ftl", mapOf("user" to user)))
}


fun Route.hello() = get("/hello") { call.respondText("i say hello") }

@Location(ABOUT)
class About


fun Route.about(repo: Repository) = get<About> {
    val user = call.sessions.get<EPSession>()?.let { repo.user(it.userId) }
    call.respond(FreeMarkerContent("about.ftl", mapOf("user" to user)))
}