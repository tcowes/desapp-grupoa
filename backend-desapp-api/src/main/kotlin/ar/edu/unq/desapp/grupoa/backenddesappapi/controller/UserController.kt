package ar.edu.unq.desapp.grupoa.backenddesappapi.controller

import ar.edu.unq.desapp.grupoa.backenddesappapi.controller.DTO.UserDTO
import ar.edu.unq.desapp.grupoa.backenddesappapi.model.User
import ar.edu.unq.desapp.grupoa.backenddesappapi.service.UserService
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*

@RestController
@Validated
class UserController (private val userService: UserService){
    @PostMapping("/register")
    fun register(@Valid @RequestBody userData: UserDTO): ResponseEntity<String> {
        val newUser = User(
            name = userData.name,
            email = userData.email,
            password = userData.password,
            surname = userData.surname,
            address = userData.address,
            cvu = userData.cvu,
            walletAddress = userData.walletAddress
        )

        userService.createUser(newUser)

        return ResponseEntity.status(HttpStatus.CREATED).body("Usuario registrado exitosamente")
    }
}