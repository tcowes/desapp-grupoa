package ar.edu.unq.desapp.grupoa.backenddesappapi.service

import ar.edu.unq.desapp.grupoa.backenddesappapi.model.CryptoCurrencyEnum
import ar.edu.unq.desapp.grupoa.backenddesappapi.service.integration.BinanceApi
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.mockito.Mockito
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
        Mockito.`when`(binanceApi.getCryptoCurrencyValue(CryptoCurrencyEnum.ETHUSDT.name)).thenReturn(1000F)
        Mockito.`when`(binanceApi.getCryptoCurrencyValue(CryptoCurrencyEnum.AAVEUSDT.name)).thenReturn(2000F)
        Mockito.`when`(binanceApi.getCryptoCurrencyValue(CryptoCurrencyEnum.BTCUSDT.name)).thenThrow(RuntimeException())

        val cryptoAsset = cryptoService.showCryptoAssetQuotes()

        assertNotNull(cryptoAsset)
        assertEquals(14, cryptoAsset.size)
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
        Mockito.`when`(binanceApi.getCryptoCurrencyValue(CryptoCurrencyEnum.ETHUSDT.name)).thenReturn(1000F)
        assertEquals(cryptoService.getCryptoQuote(CryptoCurrencyEnum.ETHUSDT), 1000F)
    }

    @Test
    fun testGetCryptoQuoteReturnsNullIfAnExceptionOccurs() {
        Mockito.`when`(binanceApi.getCryptoCurrencyValue(CryptoCurrencyEnum.ETHUSDT.name)).thenThrow(RuntimeException())
        assertEquals(cryptoService.getCryptoQuote(CryptoCurrencyEnum.ETHUSDT), null)
    }
}