package ar.edu.unq.desapp.grupoa.backenddesappapi.webservice

import ar.edu.unq.desapp.grupoa.backenddesappapi.model.Intention
import ar.edu.unq.desapp.grupoa.backenddesappapi.model.exceptions.exceptionsIntention.OutOfRangePriceException
import ar.edu.unq.desapp.grupoa.backenddesappapi.model.exceptions.exceptionsIntention.UsernameIntentException
import ar.edu.unq.desapp.grupoa.backenddesappapi.service.IntentionService
import ar.edu.unq.desapp.grupoa.backenddesappapi.webservice.dtos.CreationIntentionDTO
import ar.edu.unq.desapp.grupoa.backenddesappapi.webservice.dtos.IntentionDTO
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

@RestController
@RequestMapping("/intentions")
@Tag(name = "Intentions", description = "Endpoint that manages selling/buying intentions from users")
@Validated
class IntentionController {

    @Autowired
    private lateinit var intentionService: IntentionService

    @Operation(
        summary = "Create an intention",
        description = "Create an intention with for a user",
    )
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "Success",
                content = [
                    Content(
                        mediaType = "application/json",
                        schema = Schema(implementation = CreationIntentionDTO::class),
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
                            value = "Failed to create intention: ..."
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
                            value = "Didn't found any user with id 999"
                        )]
                    )
                ]
            )
        ]
    )
    @PostMapping("/create")
    fun createIntention(@Valid @RequestBody intention: CreationIntentionDTO): ResponseEntity<Any> {  // TODO: SEGURIZAR
        lateinit var intentionCreated: Intention
        try {
            intentionCreated = intentionService.createIntention(
                intention.cryptoactive,
                intention.amountOfCrypto,
                intention.lastQuotation,
                intention.userId,
                intention.operation
            )
        } catch (ex: Throwable) {
            when (ex) {
                is UsernameIntentException ->
                    return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("Didn't found any user with id ${intention.userId}")

                is OutOfRangePriceException ->
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body("Failed to create intention: ${ex.message}")
            }
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(IntentionDTO.fromModel(intentionCreated))
    }

    @Operation(
        summary = "List all active intentions",
        description = "List all active intentions",
    )
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "Success",
                content = [
                    Content(
                        mediaType = "application/json",
                        schema = Schema(implementation = Array<IntentionDTO>::class)
                    )
                ]
            ),
        ]
    )
    @GetMapping("/all-active")
    fun listActiveIntentions(): List<IntentionDTO> {
        return intentionService.listActiveIntentions().map { IntentionDTO.fromModel(it) }
    }
}
