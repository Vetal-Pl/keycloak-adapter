package com.vk.keycloak.adapter.keycloakadapter.controller

import com.vk.keycloak.adapter.keycloakadapter.model.ImpersonateToken
import com.vk.keycloak.adapter.keycloakadapter.model.KeycloakUser
import com.vk.keycloak.adapter.keycloakadapter.service.KeycloakClientService
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
class KeycloakController(
    val keycloakVkIdService: KeycloakClientService
) {
    companion object{
        const val MAIN_URL = "/admin/vkid"
        const val FIND = "$MAIN_URL/find"
        const val GET_TOKEN = "$MAIN_URL/token"
    }
    @GetMapping(MAIN_URL)
    fun getTokenByUserIdSimple(@RequestParam vkid: String): ImpersonateToken {
        println("vkId = $vkid")
        return keycloakVkIdService.getTokenByUserId(vkid)
    }

    @GetMapping(GET_TOKEN)
    fun getTokenByUserId(@RequestParam vkid: String): ImpersonateToken =
         keycloakVkIdService.getTokenByUserId(vkid)
}