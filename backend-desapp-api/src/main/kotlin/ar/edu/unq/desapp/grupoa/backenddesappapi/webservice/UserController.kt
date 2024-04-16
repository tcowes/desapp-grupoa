package ar.edu.unq.desapp.grupoa.backenddesappapi.webservice

import ar.edu.unq.desapp.grupoa.backenddesappapi.model.exceptions.ErrorCreatingUser
import ar.edu.unq.desapp.grupoa.backenddesappapi.model.exceptions.UserAlreadyRegisteredException
import ar.edu.unq.desapp.grupoa.backenddesappapi.webservice.dtos.UserDTO
import ar.edu.unq.desapp.grupoa.backenddesappapi.service.impl.UserService
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/users")
@Tag(name = "Users", description = "Endpoint that manages users")
@Validated
class UserController (private val userService: UserService){

    @PostMapping("/register")
    fun register(@Valid @RequestBody userData: UserDTO): ResponseEntity<String> {
        try {
            userService.createUser(userData.toModel())
        } catch (ex: Throwable) {
            when (ex) {
                is ErrorCreatingUser, is UserAlreadyRegisteredException ->
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Something went wrong! ${ex.message}")
            }
        }
        return ResponseEntity.status(HttpStatus.CREATED).body("Welcome ${userData.name} ${userData.surname}! You've been successfully registered.")
    }

}