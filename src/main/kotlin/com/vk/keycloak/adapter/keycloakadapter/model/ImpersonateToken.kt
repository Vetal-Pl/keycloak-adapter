package com.vk.keycloak.adapter.keycloakadapter.model

import com.fasterxml.jackson.annotation.JsonProperty

data class ImpersonateToken(
    @JsonProperty("access_token")
    val token: String,

    @JsonProperty("expires_in")
    val expiresIn: Long,

    @JsonProperty("refresh_expires_in")
    val refreshExpiresIn: Long,

    @JsonProperty("refresh_token")
    val refreshToken: String,

    @JsonProperty("token_type")
    val tokenType: String,

    @JsonProperty("session_state")
    val sessionState: String,
)

