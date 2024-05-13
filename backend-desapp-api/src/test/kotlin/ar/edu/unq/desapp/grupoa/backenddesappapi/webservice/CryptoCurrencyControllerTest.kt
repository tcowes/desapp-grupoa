package ar.edu.unq.desapp.grupoa.backenddesappapi.webservice

import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.get
import org.springframework.http.MediaType

@SpringBootTest
@AutoConfigureMockMvc
class CryptoCurrencyControllerTest {
    @Autowired
    private lateinit var mockMvc: MockMvc

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
}