package com.example

import io.ktor.util.*
import javax.crypto.*
import javax.crypto.spec.*


const val MIN_USER_ID_LENGTH: Int = 4
const val MIN_USER_PASS_LENGTH: Int = 6


val hashKey = hex(System.getenv("SECRET_KEY"))

val hmacKey = SecretKeySpec(hashKey, "hmacSHA1")

fun hash(password: String): String {
    val hmac = Mac.getInstance("hmacSHA1")
    hmac.init(hmacKey)
    return hex(hmac.doFinal(password.toByteArray(Charsets.UTF_8)))
}

private val userIdPattern = "[a-zA-Z0-9\\.]+".toRegex()


fun userNameValid(userId: String): Boolean = userId.matches(userIdPattern)
