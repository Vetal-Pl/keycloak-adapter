package com.vk.keycloak.adapter.keycloakadapter.config

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class KeycloakPropertiesConfig {

    @Bean
    @ConfigurationProperties(prefix = "keycloak-admin-client")
    fun keycloakProperties(): KeycloakProperties = KeycloakProperties()
}