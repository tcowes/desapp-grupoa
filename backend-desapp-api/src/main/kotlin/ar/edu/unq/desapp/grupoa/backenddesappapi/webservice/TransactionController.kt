package ar.edu.unq.desapp.grupoa.backenddesappapi.webservice

import ar.edu.unq.desapp.grupoa.backenddesappapi.service.TransactionService
import ar.edu.unq.desapp.grupoa.backenddesappapi.webservice.dtos.VolumeOperatedDTO
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@RestController
@RequestMapping("/transactions")
class TransactionController(private val transactionService: TransactionService) {

    @GetMapping("/volume")
    fun getVolumeOperated(@RequestParam userId: Long, @RequestParam startDate: String, @RequestParam endDate: String): VolumeOperatedDTO {
        val formatter = DateTimeFormatter.ISO_DATE_TIME
        val start = LocalDateTime.parse(startDate, formatter)
        val end = LocalDateTime.parse(endDate, formatter)
        return transactionService.getVolumeOperated(userId, start, end)
    }
}