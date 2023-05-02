package com.vk.keycloak.adapter.keycloakadapter.config

import org.keycloak.OAuth2Constants
import org.keycloak.admin.client.Keycloak
import org.keycloak.admin.client.KeycloakBuilder
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class KeycloakAdapterConfig {
    @Bean
    fun createKeycloakInstance(
        @Value("\${keycloak-admin-client.url}")
        serverUrl: String,
        @Value("\${keycloak-admin-client.realm}")
        realm: String,
        @Value("\${keycloak-admin-client.client-id}")
        clientId: String,
        @Value("\${keycloak-admin-client.client-secret}")
        clientSecret: String,
    ): Keycloak =
        KeycloakBuilder.builder()
            .serverUrl(serverUrl)
            .realm(realm)
            .grantType(OAuth2Constants.CLIENT_CREDENTIALS)
            .clientId(clientId)
            .clientSecret(clientSecret)
            .build()
}