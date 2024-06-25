package ar.edu.unq.desapp.grupoa.backenddesappapi.webservice

import ar.edu.unq.desapp.grupoa.backenddesappapi.model.User
import ar.edu.unq.desapp.grupoa.backenddesappapi.model.exceptions.WrongPasswordException
import ar.edu.unq.desapp.grupoa.backenddesappapi.service.UserService
import ar.edu.unq.desapp.grupoa.backenddesappapi.webservice.dtos.ExposedUserDTO
import ar.edu.unq.desapp.grupoa.backenddesappapi.webservice.dtos.LoginUserDTO
import ar.edu.unq.desapp.grupoa.backenddesappapi.webservice.dtos.TransactionDTO
import ar.edu.unq.desapp.grupoa.backenddesappapi.webservice.dtos.UserDTO
import ar.edu.unq.desapp.grupoa.backenddesappapi.webservice.security.JwtUtil
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.ExampleObject
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.persistence.EntityNotFoundException
import jakarta.validation.Valid
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*
import java.time.Clock

@RestController
@RequestMapping("/users")
@Tag(name = "Users", description = "Endpoint that manages users")
@Validated
class UserController(
    private val userService: UserService,
    private val jwtUtil: JwtUtil,
    private val authenticationManager: AuthenticationManager
) {

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
                        schema = Schema(implementation = ExposedUserDTO::class),
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
    @PostMapping("/auth/register")
    fun register(@Valid @RequestBody userData: UserDTO): ResponseEntity<Any> {
        lateinit var user: User
        try {
            user = userService.createUser(userData.toModel())
        } catch (ex: Throwable) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Something went wrong! ${ex.message}")
        }
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(ExposedUserDTO.fromModel(user))
    }

    @Operation(summary = "Login a user", description = "Logs in a user with the provided email and password")
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200", description = "User logged in successfully", content = [
                    Content(schema = Schema(implementation = ExposedUserDTO::class))
                ]
            ),
            ApiResponse(
                responseCode = "400",
                description = "Bad request: the password is incorrect for this user.",
                content = [
                    Content(schema = Schema(implementation = WrongPasswordException::class))
                ]
            ),
            ApiResponse(
                responseCode = "404", description = "User not found with provided email", content = [
                    Content(schema = Schema(implementation = EntityNotFoundException::class))
                ]
            )
        ]
    )
    @PostMapping("/auth/login")
    fun loginUser(@Valid @RequestBody userInput: LoginUserDTO): ResponseEntity<Any> {
        lateinit var user: User
        try {
            user = userService.login(userInput.email, userInput.password)
        } catch (ex: EntityNotFoundException) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("${ex.message}")
        } catch (ex: Throwable) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("${ex.message}")
        }

        val authentication = authenticationManager.authenticate(
            UsernamePasswordAuthenticationToken(userInput.email, userInput.password)
        )
        val jwt = jwtUtil.generateToken(authentication.name)
        val headers = HttpHeaders()
        headers.add("Authorization", "Bearer $jwt")
        return ResponseEntity.status(HttpStatus.OK).headers(headers).body(ExposedUserDTO.fromModel(user))
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
    @PostMapping("/{userId}/create-transaction/{intentionId}")
    fun createTransaction(
        @PathVariable userId: Long,
        @PathVariable intentionId: Long
    ): ResponseEntity<Any> {
        return try {
            val user = userService.getUserById(userId)
            val dto = TransactionDTO.fromModel(userService.beginTransaction(userId, intentionId, defaultClock), user)
            ResponseEntity.status(HttpStatus.CREATED)
                .body(dto)
        } catch (ex: EntityNotFoundException) {
            ResponseEntity.status(HttpStatus.NOT_FOUND).body("${ex.message}")
        } catch (ex: Throwable) {
            ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Something went wrong! ${ex.message}")
        }
    }


    @Operation(
        summary = "Finish a transaction",
        description = "Finish a transaction",
    )
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "Success",
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
    @PostMapping("/{userId}/finish-transaction/{transactionId}")
    fun finishTransaction(
        @PathVariable userId: Long,
        @PathVariable transactionId: Long
    ): ResponseEntity<Any> {
        return try {
            userService.finishTransaction(userId, transactionId, defaultClock)
            ResponseEntity.status(HttpStatus.OK).body("Transaction successfully finished!")
        } catch (ex: EntityNotFoundException) {
            ResponseEntity.status(HttpStatus.NOT_FOUND).body("${ex.message}")
        } catch (ex: Throwable) {
            ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Something went wrong! ${ex.message}")
        }
    }

}