package ar.edu.unq.desapp.grupoa.backenddesappapi.webservice

import ar.edu.unq.desapp.grupoa.backenddesappapi.model.exceptions.exceptionsIntention.ErrorCreatingIntention
import ar.edu.unq.desapp.grupoa.backenddesappapi.model.exceptions.exceptionsIntention.OutOfRangePriceException
import ar.edu.unq.desapp.grupoa.backenddesappapi.model.exceptions.exceptionsIntention.UsernameIntentException
import ar.edu.unq.desapp.grupoa.backenddesappapi.service.IntentionService
import ar.edu.unq.desapp.grupoa.backenddesappapi.webservice.dtos.CreationIntentionDTO
import ar.edu.unq.desapp.grupoa.backenddesappapi.webservice.dtos.IntentionDTO
import jakarta.validation.Valid
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/intentions")
@Validated
class IntentionController {

    @Autowired
    private lateinit var intentionService: IntentionService

    @PostMapping("/create")
    fun createIntention(@Valid @RequestBody intention: CreationIntentionDTO): ResponseEntity<String> {
        try {
            intentionService.createIntention(
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

                is ErrorCreatingIntention, is OutOfRangePriceException ->
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body("Failed to create intention: ${ex.message}")
            }
        }
        return ResponseEntity.status(HttpStatus.CREATED).body("Intention created successfully")
    }

    @GetMapping("/all-active")
    fun listActiveIntentios(): List<IntentionDTO> {
        return intentionService.listActiveIntentions().map { IntentionDTO.fromModel(it) }
    }
}
