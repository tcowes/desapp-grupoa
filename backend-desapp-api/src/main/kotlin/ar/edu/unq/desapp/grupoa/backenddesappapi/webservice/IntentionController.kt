package ar.edu.unq.desapp.grupoa.backenddesappapi.webservice

import ar.edu.unq.desapp.grupoa.backenddesappapi.service.IntentionService
import ar.edu.unq.desapp.grupoa.backenddesappapi.webservice.dtos.IntentionDTO
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/intentions")
class IntentionController {

    @Autowired
    private lateinit var intentionService: IntentionService

    @PostMapping("/create")
    fun createIntention(@RequestBody intentionDto: IntentionDTO): ResponseEntity<String> {
        try {
            intentionService.createIntention(intentionDto.toModel())
            return ResponseEntity.status(HttpStatus.CREATED).body("Intention created successfully")
        } catch (ex: Exception) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Failed to create intention: ${ex.message}")
        }
    }
}