package ar.edu.unq.desapp.grupoa.backenddesappapi.webservice

import ar.edu.unq.desapp.grupoa.backenddesappapi.model.CryptoCurrencyEnum
import ar.edu.unq.desapp.grupoa.backenddesappapi.service.CryptoService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.ExampleObject
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/crypto-currency")
@Tag(name = "Crypto Currency", description = "Endpoint that shows crypto currencies actual and historical quotes")
@Validated
class CryptoCurrencyController {

    @Autowired
    private lateinit var cryptoCurrencyService: CryptoService

    @Operation(
        summary = "Show all crypto currencies actual quotes",
        description = "Show all crypto currencies actual quotes",
    )
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "Success",
                content = [
                    Content(
                        mediaType = "application/json",
                        schema = Schema(implementation = Map::class),
                    )
                ]
            ),
        ]
    )
    @GetMapping("/cryptoasset-quotes")
    fun showCryptoAssetQuotes(): ResponseEntity<Map<String, Float?>> {
        val quotes = cryptoCurrencyService.showCryptoAssetQuotes()
        return ResponseEntity.ok(quotes)
    }

    @Operation(
        summary = "Show a crypto currency's quotes from the las 24 hours",
        description = "Show a crypto currency's quotes from the las 24 hours",
    )
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "Success",
                content = [
                    Content(
                        mediaType = "application/json",
                        schema = Schema(implementation = Map::class),
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
                            value = "Error: Incorrect cryptocurrency symbol"
                        )]
                    )
                ]
            ),
        ]
    )
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
    @GetMapping("/quotes-every-10-minutes")
    fun getCryptoQuotesEvery10Minutes(): Map<String, List<String>> {
        return cryptoCurrencyService.showCryptoAssetQuotesEvery10Minutes()
    }
}