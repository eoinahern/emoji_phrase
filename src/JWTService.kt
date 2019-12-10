package com.example

import com.auth0.jwt.*
import com.auth0.jwt.algorithms.*
import com.example.model.*
import java.util.*

class JWTService {
    private val issuer = "emojiphrases"
    private val jwtSecret = System.getenv("JWT_SECRET")
    private val algorithm = Algorithm.HMAC256(jwtSecret)

    val jwtVerifier: JWTVerifier = JWT.require(algorithm).withIssuer(issuer).build()

    fun generateToken(user: User): String {
        return JWT.create()
            .withSubject("Authentication")
            .withIssuer(issuer)
            .withClaim("id", user.userId)
            .withExpiresAt(expiresAt())
            .sign(algorithm)
    }

    private fun expiresAt(): Date = Date(System.currentTimeMillis() + (1000 * 60 * 60 * 24))
}