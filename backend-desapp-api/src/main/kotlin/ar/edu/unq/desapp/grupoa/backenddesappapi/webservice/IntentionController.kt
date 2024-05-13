package ar.edu.unq.desapp.grupoa.backenddesappapi.webservice

import ar.edu.unq.desapp.grupoa.backenddesappapi.model.exceptions.exceptionsIntention.ErrorCreatingIntention
import ar.edu.unq.desapp.grupoa.backenddesappapi.service.IntentionService
import ar.edu.unq.desapp.grupoa.backenddesappapi.webservice.dtos.IntentionDTO
import jakarta.validation.Valid
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/intentions")
@Validated
class IntentionController {

    @Autowired
    private lateinit var intentionService: IntentionService

    @PostMapping("/create")
    fun createIntention(@Valid @RequestBody intentionDTO: IntentionDTO): ResponseEntity<String> {
        try {
            intentionService.createIntention(
                intentionDTO.cryptoactive,
                intentionDTO.amountOfCrypto,
                intentionDTO.lastQuotation,
                intentionDTO.userId,
                intentionDTO.operation
            )
        } catch (ex: Throwable) {
            when (ex) {
                is ErrorCreatingIntention ->
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body("Failed to create intention: ${ex.message}")
            }
        }
        return ResponseEntity.status(HttpStatus.CREATED).body("Intention created successfully")
    }
}