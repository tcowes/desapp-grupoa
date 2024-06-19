package ar.edu.unq.desapp.grupoa.backenddesappapi.webservice

import ar.edu.unq.desapp.grupoa.backenddesappapi.service.CryptoService
import org.junit.jupiter.api.Test
import org.mockito.Mockito.`when`
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.get
import org.springframework.http.MediaType

@SpringBootTest
@AutoConfigureMockMvc
class CryptoCurrencyControllerTest {
    @Autowired
    private lateinit var mockMvc: MockMvc

    @MockBean
    private lateinit var cryptoCurrencyService: CryptoService

    @Test
    fun testShowCryptoAssetQuotes() {
        mockMvc.get("/crypto-currency/cryptoasset-quotes")
            .andExpect {
                status { isOk() }
                content {
                    contentType(MediaType.APPLICATION_JSON)
                }
            }
    }

    @Test
    fun testGetCryptoAssetQuotesLast24Hours_ValidSymbol() {
        val crypto = "ATOMUSDT"
        mockMvc.get("/crypto-currency/quotes-last-24-hours?symbol=$crypto")
            .andExpect {
                status { isOk() }
                content {
                    contentType(MediaType.APPLICATION_JSON)
                }
            }
    }

    @Test
    fun testGetCryptoAssetQuotesLast24Hours_InvalidSymbol() {
        val invalidCrypto = "BTC"
        mockMvc.get("/crypto-currency/quotes-last-24-hours?symbol=$invalidCrypto")
            .andExpect {
                status { isBadRequest() }
                content { contentType(MediaType.APPLICATION_JSON) }
            }
    }

    @Test
    fun testGetCryptoQuotesEvery10Minutes() {
        val fakeData = mapOf(
            "BTCUSDT" to listOf("10000.00", "10100.00", "10200.00"),
            "ETHUSDT" to listOf("300.00", "310.00", "320.00")
        )
        `when`(cryptoCurrencyService.showCryptoAssetQuotesEvery10Minutes()).thenReturn(fakeData)

        mockMvc.get("/crypto-currency/quotes-every-10-minutes")
            .andExpect {
                status { isOk() }
                content {
                    contentType(MediaType.APPLICATION_JSON)
                    json("""{
                        "BTCUSDT": ["10000.00", "10100.00", "10200.00"],
                        "ETHUSDT": ["300.00", "310.00", "320.00"]
                    }""")
                }
            }
    }
}