package ar.edu.unq.desapp.grupoa.backenddesappapi.service

import ar.edu.unq.desapp.grupoa.backenddesappapi.model.CryptoCurrencyEnum
import ar.edu.unq.desapp.grupoa.backenddesappapi.service.integration.BinanceApi
import com.github.benmanes.caffeine.cache.Cache
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.mockito.ArgumentMatchers.anyInt
import org.mockito.ArgumentMatchers.anyString
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean

@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class CryptoCurrencyServiceTest {

    @Autowired
    private lateinit var cryptoService: CryptoService

    @MockBean
    private lateinit var binanceApi: BinanceApi

    @Test
    fun testShowCryptoAssetQuotes() {
        val mockQuotes = mapOf(
            "ETHUSDT" to 1000F,
            "AAVEUSDT" to 2000F,
            "BTCUSDT" to null
        )

        `when`(binanceApi.showCryptoAssetQuotes()).thenReturn(mockQuotes)

        val cryptoAsset = cryptoService.showCryptoAssetQuotes()

        assertNotNull(cryptoAsset)
        assertEquals(3, cryptoAsset.size)
        assertEquals(2000F, cryptoAsset["AAVEUSDT"])
        assertEquals(1000F, cryptoAsset["ETHUSDT"])
        assertEquals(null, cryptoAsset["BTCUSDT"])
    }


    @Test
    fun testShowCryptoAssetQuotesLast24Hours() {
        val quotes = cryptoService.showCryptoAssetQuotesLast24Hours(CryptoCurrencyEnum.AAVEUSDT)

        assertNotNull(quotes)
        assertTrue(quotes.isNotEmpty())
        assertTrue(quotes[0].startsWith("Quotes from the last 24 hours for ${CryptoCurrencyEnum.AAVEUSDT}:"))

    }

    @Test
    fun testGetCryptoQuoteReturnsValue() {
        `when`(binanceApi.getCryptoCurrencyValue(CryptoCurrencyEnum.ETHUSDT.name)).thenReturn(1000F)
        assertEquals(cryptoService.getCryptoQuote(CryptoCurrencyEnum.ETHUSDT), 1000F)
    }

    @Test
    fun testGetCryptoQuoteReturnsNullIfAnExceptionOccurs() {
        `when`(binanceApi.getCryptoCurrencyValue(CryptoCurrencyEnum.ETHUSDT.name)).thenThrow(RuntimeException())
        assertEquals(cryptoService.getCryptoQuote(CryptoCurrencyEnum.ETHUSDT), null)
    }
    @Test
    fun testShowCryptoAssetQuotesEvery10Minutes() {
        val mockData = listOf(
            mapOf("timestamp" to "2024-06-19T10:00:00", "price" to "1000"),
            mapOf("timestamp" to "2024-06-19T10:05:00", "price" to "1100"),
            mapOf("timestamp" to "2024-06-19T10:10:00", "price" to "1200"),
            mapOf("timestamp" to "2024-06-19T10:15:00", "price" to "1300")
        )

        `when`(binanceApi.getCryptoCurrencyValueHistory(anyString(), anyString(), anyInt()))
            .thenReturn(mockData)

        val quotes = cryptoService.showCryptoAssetQuotesEvery10Minutes()

        assertNotNull(quotes)
        assertEquals(14, quotes.size)

        for ((crypto, quotesList) in quotes) {
            assertTrue(quotesList.isNotEmpty())
            assertTrue(quotesList[0].startsWith("Quotes every 10 minutes for $crypto:"))
        }
    }

    @Test
    fun testShowCryptoAssetQuotesErrorHandling() {
        `when`(binanceApi.getCryptoCurrencyValueHistory(anyString(), anyString(), anyInt()))
            .thenThrow(RuntimeException("Error fetching data"))

        val quotesWithError = cryptoService.showCryptoAssetQuotesEvery10Minutes()

        assertTrue(quotesWithError.containsKey("error"))
        assertEquals(1, quotesWithError["error"]?.size)
        assertTrue(quotesWithError["error"]?.get(0)?.startsWith("Error getting quotes:") ?: false)
    }

}