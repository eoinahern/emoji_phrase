package com.example

import com.example.api.*
import com.example.model.*
import com.example.repository.*
import com.example.webapp.*
import com.ryanharter.ktor.moshi.*
import freemarker.cache.*
import io.ktor.application.*
import io.ktor.auth.*
import io.ktor.features.*
import io.ktor.freemarker.*
import io.ktor.http.content.*
import io.ktor.locations.*
import io.ktor.response.*
import io.ktor.request.*
import io.ktor.routing.*

fun main(args: Array<String>) = io.ktor.server.netty.EngineMain.main(args)

@Suppress("unused") // Referenced in application.conf
@kotlin.jvm.JvmOverloads
fun Application.module(testing: Boolean = false) {

    this.install(DefaultHeaders)

    this.install(StatusPages) {
        exception<Throwable> { e ->
            call.respondText(e.localizedMessage)
        }
    }

    this.install(ContentNegotiation) {
        moshi()
    }

    this.install(Locations)

    install(FreeMarker) {
        templateLoader = ClassTemplateLoader(this::class.java.classLoader, "templates") as TemplateLoader?
    }

    install(Authentication) {
        basic(name = "auth") {
            realm = "ktor server"
            validate { credentials ->
                if (credentials.password == credentials.name.plus("123"))
                    User(credentials.name)
                else
                    null
            }
        }
    }

    val db = InMemoryRepository()

    routing {
        static("/static") {
            resources("images")
        }
        home()
        hello()
        about()
        phrase(db)
        phrases(db)
    }
}

const val API_VERSION = "api/v1"

suspend fun ApplicationCall.redirect(location: Any) {
    respondRedirect(application.locations.href(location))
}

