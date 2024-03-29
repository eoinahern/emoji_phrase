package com.example

import com.example.api.*
import com.example.model.*
import com.example.repository.*
import com.example.webapp.*
import com.google.gson.*
import freemarker.cache.*
import io.ktor.application.*
import io.ktor.auth.*
import io.ktor.auth.jwt.*
import io.ktor.features.*
import io.ktor.freemarker.*
import io.ktor.gson.*
import io.ktor.http.*
import io.ktor.http.content.*
import io.ktor.locations.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*
import io.ktor.sessions.*
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
        gson()
    }

    this.install(Locations)

    install(Sessions) {
        cookie<EPSession>("SESSION") {
            transform(SessionTransportTransformerMessageAuthentication(hashKey))
        }
    }

    install(FreeMarker) {
        templateLoader = ClassTemplateLoader(this::class.java.classLoader, "templates")
    }

    DatabaseFactory.init()

    val db = EmojiPhrasesRepository()
    val jwtService = JWTService()

    install(Authentication) {
        jwt("jwt") {
            verifier(jwtService.jwtVerifier)
            realm = "emojiphrases app"
            validate {
                val claim = it.payload.getClaim("id")
                val claimStr = claim.asString()
                val user = db.userById(claimStr)
                user
            }
        }
    }

    val hashFunction: (String) -> String = { s -> hash(s) }

    routing {
        static("/static") {
            resources("images")
        }
        home(db)
        about(db)
        login(db, jwtService)
        phrasesApi(db)
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

const val API_VERSION = "/api/v1"


val ApplicationCall.apiUser get() = authentication.principal<User>()


