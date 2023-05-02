package com.vk.keycloak.adapter.keycloakadapter.model

data class KeycloakUser(
    val id: String,
    val username: String,
//    val email: String,
//    val firstName: String,
//    val lastName: String,
    val isEnabled: Boolean,
)
