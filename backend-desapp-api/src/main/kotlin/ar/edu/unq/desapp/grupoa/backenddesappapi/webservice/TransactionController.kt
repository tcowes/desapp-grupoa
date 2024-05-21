package ar.edu.unq.desapp.grupoa.backenddesappapi.webservice

import ar.edu.unq.desapp.grupoa.backenddesappapi.service.TransactionService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException

@RestController
@RequestMapping("/transactions")
class TransactionController {
    @Autowired
    private lateinit var transactionService: TransactionService

    @GetMapping("/volume")
    fun getVolumeOperated(@RequestParam userId: Long, @RequestParam startDate: String, @RequestParam endDate: String): ResponseEntity<Any> {
        val formatter = DateTimeFormatter.ISO_DATE_TIME
        return try {
            val start = LocalDateTime.parse(startDate, formatter)
            val end = LocalDateTime.parse(endDate, formatter)
            ResponseEntity.status(HttpStatus.OK)
                .body(transactionService.getVolumeOperated(userId, start, end))
        } catch (e: DateTimeParseException) {
            ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid date format. Please use 'YYYY-MM-DDTHH:MM:SS'")
        }
    }
}