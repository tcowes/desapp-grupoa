package ar.edu.unq.desapp.grupoa.backenddesappapi.webservice

import ar.edu.unq.desapp.grupoa.backenddesappapi.model.CryptoCurrencyEnum
import ar.edu.unq.desapp.grupoa.backenddesappapi.service.impl.CryptoCurrencyService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

// TODO: Documentar controller y endpoints
@RestController
@RequestMapping("/crypto-currency")
class CryptoCurrencyController {

    @Autowired
    private lateinit var cryptoCurrencyService: CryptoCurrencyService

    @GetMapping("/cryptoasset-quotes")
    fun showCryptoAssetQuotes(): ResponseEntity<Map<String, Float?>> {
        val quotes = cryptoCurrencyService.showCryptoAssetQuotes()
        return ResponseEntity.ok(quotes)
    }

    @GetMapping("/quotes-last-24-hours")
    fun getCryptoAssetQuotesLast24Hours(@RequestParam symbol: String): ResponseEntity<List<String>> {
        return try {
            val cryptoEnum = CryptoCurrencyEnum.valueOf(symbol)
            val quotes = cryptoCurrencyService.showCryptoAssetQuotesLast24Hours(cryptoEnum)
            ResponseEntity.ok(quotes)
        } catch (e: IllegalArgumentException) {
            ResponseEntity.status(HttpStatus.BAD_REQUEST).body(listOf("Error: Incorrect cryptocurrency symbol"))
        }
    }
}