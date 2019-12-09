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
import io.ktor.http.*
import io.ktor.http.content.*
import io.ktor.locations.*
import io.ktor.response.*
import io.ktor.request.*
import io.ktor.routing.*
import java.net.*
import java.util.concurrent.*

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

    /*install(Authentication) {
        basic(name = "auth") {
            realm = "ktor server"
            validate { credentials ->
                if (credentials.password == credentials.name.plus("123"))
                    User(credentials.name)
                else
                    null
            }
        }
    }*/

    DatabaseFactory.init()

    val db = EmojiPhrasesRepository()

    val hashFunction: (String) -> String = { s -> hash(s) }

    routing {
        static("/static") {
            resources("images")
        }
        home(db)
        hello()
        about(db)
        phrase(db)
        phrases(db, hashFunction)
        signin(db, hashFunction)
        signup(db, hashFunction)
        signout()

    }
}

suspend fun ApplicationCall.redirect(location: Any) {
    respondRedirect(application.locations.href(location))
}


fun ApplicationCall.referrerHost() = request.header(HttpHeaders.Referrer)?.let { URI.create(it).host }

fun ApplicationCall.securityCode(date: Long, user: User, hashFunction: (String) -> String) =
    hashFunction("$date:${user.userId}:${request.host()}:${referrerHost()}")

fun ApplicationCall.verifyCode(date: Long, user: User, code: String, hashFunction: (String) -> String) =
    securityCode(date, user, hashFunction) == code && (System.currentTimeMillis() - date).let {
        it > 0 && it < TimeUnit.MILLISECONDS.convert(2, TimeUnit.HOURS)
    }


