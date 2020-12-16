package io.devolan

import com.auth0.jwt.JWT
import com.auth0.jwt.JWTCreator
import com.auth0.jwt.algorithms.Algorithm

class TokenFactory {

    companion object {

        private const val secret = "secret"

        fun tokenWithAdminRole(): String = jwt()
            .withArrayClaim("groups", arrayOf("admin"))
            .sign(Algorithm.HMAC256(secret))

        fun tokenWithUserRole(): String = jwt()
            .withArrayClaim("groups", arrayOf("user"))
            .sign(Algorithm.HMAC256(secret))

        private fun jwt(): JWTCreator.Builder = JWT.create()
            .withAudience("api://default")
            .withSubject("olivier.antoine@me.com")
            .withIssuer("https://dev-1870027.okta.com/oauth2/default")
    }
}
