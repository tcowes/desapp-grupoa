package ar.edu.unq.desapp.grupoa.backenddesappapi.webservice

import ar.edu.unq.desapp.grupoa.backenddesappapi.service.impl.CryptoCurrencyService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/cryptoCurrency")
class CryptoCurrencyController {

    @Autowired
    private lateinit var cryptoCurrencyService: CryptoCurrencyService

    @GetMapping("/cryptoasset-quotes")
    fun showCryptoAssetQuotes(): ResponseEntity<List<String>> {
        val quotes = cryptoCurrencyService.showCryptoAssetQuotes()
        return ResponseEntity.ok(quotes)
    }
}