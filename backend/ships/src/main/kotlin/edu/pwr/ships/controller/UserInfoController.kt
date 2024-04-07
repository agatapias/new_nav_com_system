package edu.pwr.ships.controller

import edu.pwr.ships.dto.UserInfoDto
import edu.pwr.ships.service.UserInfoService
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

@RestController
class UserInfoController(
    private val userInfoService: UserInfoService
) {

    @PutMapping("/user")
    fun updateUserInfo(@RequestBody userInfoDto: UserInfoDto) : UserInfoDto {
        return userInfoService.updateInformation(userInfoDto)
    }
}