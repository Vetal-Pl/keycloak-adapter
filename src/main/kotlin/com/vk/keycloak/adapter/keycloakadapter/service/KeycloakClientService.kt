package com.vk.keycloak.adapter.keycloakadapter.service

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.KotlinModule
import com.vk.keycloak.adapter.keycloakadapter.config.KeycloakProperties
import com.vk.keycloak.adapter.keycloakadapter.model.ImpersonateToken
import com.vk.keycloak.adapter.keycloakadapter.model.KeycloakUser
import org.apache.http.client.methods.RequestBuilder
import org.apache.http.impl.client.HttpClientBuilder
import org.keycloak.admin.client.Keycloak
import org.keycloak.admin.client.resource.RoleResource
import org.keycloak.admin.client.resource.RolesResource
import org.keycloak.representations.AccessTokenResponse
import org.keycloak.representations.idm.UserRepresentation
import org.springframework.stereotype.Service

interface KeycloakClientService {
    fun getTokenByUserId(vkId: String): ImpersonateToken
}

@Service
class KeycloakClientServiceImpl(
    private val keycloakClient: Keycloak,
    private val properties: KeycloakProperties
) : KeycloakClientService {

    override fun getTokenByUserId(vkId: String): ImpersonateToken {
        val userId = findOrCreateUserByVkId(vkId).id
        return getToken(userId).toImpersonateToken()
    }

    private fun createUser(userId: String) {
        val userToSave = generateUser(userId).toUserRepresentation()
//        userToSave.realmRoles = listOf("vk_id_user")
        keycloakClient.realm(properties.realm).users().create(userToSave)
//        return (response.entity as UserRepresentation)
    }

    private fun generateUser(userId: String): KeycloakUser =
        KeycloakUser(
            id = userId,
            username = userId,
//            firstName = "firstName $userId",
            isEnabled = true,
        )

    private fun findOrCreateUserByVkId(vkId: String): KeycloakUser {
        val realm = keycloakClient.realm(properties.realm)
        val userFrom = realm.users().search(vkId).firstOrNull()
//        val user = if (userFrom == null) {
//            createUser(vkId)
//            keycloakClient.realm(properties.realm).users().search(vkId).first()
//        } else userFrom
//        val role = realm.roles().get("vk_id_user")
        val user =
            userFrom ?: createUser(vkId).let { realm.users().search(vkId).first() }
        return user.toKeycloakUser()
    }

    private fun getToken(userId: String): AccessTokenResponse {
        val httpClient = HttpClientBuilder.create().build();

        val reqBuild = RequestBuilder.post()
            .setUri(properties.url + "/realms/${properties.realm}/protocol/openid-connect/token")
            .addHeader("Content-Type", "application/x-www-form-urlencoded")
            .addParameter("client_id", properties.clientId)
            .addParameter("client_secret", properties.clientSecret) //
            .addParameter("grant_type", "urn:ietf:params:oauth:grant-type:token-exchange")
            .addParameter("subject_token", keycloakClient.tokenManager().accessTokenString)
//            .addParameter("requested_token_type", "urn:ietf:params:oauth:token-type:access_token")
//            .addParameter("requested_token_type", "urn:ietf:params:oauth:token-type:id_token")
//            .addParameter("requested_token_type", "urn:ietf:params:oauth:token-type:refresh_token")
            .addParameter("requested_subject", userId)
            .build()

        val response = httpClient.execute(reqBuild)
        val entity = if (response.statusLine.statusCode == 200) {
            val mapper = ObjectMapper().registerModule(KotlinModule())
            mapper.readValue(response.entity.content, AccessTokenResponse::class.java)
        } else {
//            println("status -> ${response.statusLine.statusCode}")
        }
        return (entity as AccessTokenResponse)
    }
}

fun AccessTokenResponse.toImpersonateToken(): ImpersonateToken = ImpersonateToken(
    token = this.token,
    refreshToken = this.refreshToken,
    expiresIn = this.expiresIn,
    refreshExpiresIn = this.refreshExpiresIn,
    sessionState = this.sessionState,
    tokenType = this.tokenType,
)

fun UserRepresentation.toKeycloakUser(): KeycloakUser = KeycloakUser(
    id = this.id,
    username = this.username,
//    email = this.email,
//    firstName = this.firstName,
//    lastName = this.lastName,
    isEnabled = this.isEnabled
)

fun KeycloakUser.toUserRepresentation(): UserRepresentation {
    val user = UserRepresentation()
    user.id = this.id
    user.username = this.username
//    user.email = this.email
//    user.firstName = this.firstName
//    user.lastName = this.lastName
    user.isEnabled = this.isEnabled
//    user.realmRoles.add("vk_id_user")
    return user
}