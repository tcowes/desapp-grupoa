package ar.edu.unq.desapp.grupoa.backenddesappapi.webservice

import ar.edu.unq.desapp.grupoa.backenddesappapi.model.exceptions.ErrorCreatingUser
import ar.edu.unq.desapp.grupoa.backenddesappapi.model.exceptions.UserAlreadyRegisteredException
import ar.edu.unq.desapp.grupoa.backenddesappapi.service.UserService
import ar.edu.unq.desapp.grupoa.backenddesappapi.webservice.dtos.TransactionDTO
import ar.edu.unq.desapp.grupoa.backenddesappapi.webservice.dtos.UserDTO
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.ExampleObject
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*
import java.time.Clock

@RestController
@RequestMapping("/users")
@Tag(name = "Users", description = "Endpoint that manages users")
@Validated
class UserController {

    @Autowired
    private lateinit var userService: UserService
    private val defaultClock = Clock.systemDefaultZone()


    @Operation(
        summary = "Register a user",
        description = "Register a user",
    )
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "Success",
                content = [
                    Content(
                        mediaType = "application/json",
                        schema = Schema(implementation = UserDTO::class),
                    )
                ]
            ),
            ApiResponse(
                responseCode = "400",
                description = "Bad Request",
                content = [
                    Content(
                        mediaType = "application/json",
                        examples = [ExampleObject(
                            value = "Something went wrong! ..."
                        )]
                    )
                ]
            ),
        ]
    )
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
        return ResponseEntity.status(HttpStatus.CREATED)
            .body("Welcome ${userData.name} ${userData.surname}! You've been successfully registered.")
    }

    @Operation(
        summary = "Create a transaction",
        description = "Create a transaction",
    )
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "Success",
                content = [
                    Content(
                        mediaType = "application/json",
                        schema = Schema(implementation = TransactionDTO::class),
                    )
                ]
            ),
            ApiResponse(
                responseCode = "400",
                description = "Bad Request",
                content = [
                    Content(
                        mediaType = "application/json",
                        examples = [ExampleObject(
                            value = "A error"
                        )]
                    )
                ]
            ),
            ApiResponse(
                responseCode = "404",
                description = "Not Found",
                content = [
                    Content(
                        mediaType = "application/json",
                        examples = [ExampleObject(
                            value = "A error"
                        )]
                    )
                ]
            )
        ]
    )
    @PostMapping("/{userId}/createTransaction/{intentionId}")
    fun createTransaction(
        @PathVariable userId: Long,
        @PathVariable intentionId: Long
    ): ResponseEntity<Any> {
        return try {
            val user = userService.getUserById(userId)
            val dto = TransactionDTO.fromModel(userService.beginTransaction(userId, intentionId, defaultClock), user)
            ResponseEntity.ok().body(dto)
        } catch (e: NoSuchElementException) {
            ResponseEntity(e.message, HttpStatus.NOT_FOUND)
        } catch (e: Throwable) {
            ResponseEntity.badRequest().body(e.message)
        }
    }

}