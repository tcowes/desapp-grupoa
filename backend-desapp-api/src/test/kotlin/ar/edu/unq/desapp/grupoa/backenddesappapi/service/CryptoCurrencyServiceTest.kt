package ar.edu.unq.desapp.grupoa.backenddesappapi.service

import ar.edu.unq.desapp.grupoa.backenddesappapi.model.CryptoCurrencyEnum
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class CryptoCurrencyServiceTest {

    @Autowired
    private lateinit var cryptoService: CryptoService

    @Test
    fun testShowCryptoAssetQuotes(){
        val cryptoAsset = cryptoService.showCryptoAssetQuotes()

        assertNotNull(cryptoAsset)
        assertEquals(14, cryptoAsset.size)

    }

    @Test
    fun testShowCryptoAssetQuotesLast24Hours(){
        val quotes = cryptoService.showCryptoAssetQuotesLast24Hours(CryptoCurrencyEnum.AAVEUSDT)

        assertNotNull(quotes)
        assertTrue(quotes.isNotEmpty())
        assertTrue(quotes[0].startsWith("Quotes from the last 24 hours for ${CryptoCurrencyEnum.AAVEUSDT}:"))

    }
}