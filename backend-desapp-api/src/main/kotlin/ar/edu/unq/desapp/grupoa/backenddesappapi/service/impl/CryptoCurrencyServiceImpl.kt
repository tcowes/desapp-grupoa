package ar.edu.unq.desapp.grupoa.backenddesappapi.service.impl

import ar.edu.unq.desapp.grupoa.backenddesappapi.model.CryptoCurrencyEnum
import ar.edu.unq.desapp.grupoa.backenddesappapi.service.CryptoService
import ar.edu.unq.desapp.grupoa.backenddesappapi.service.integration.BinanceApi
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import java.math.BigDecimal
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@Service
class CryptoCurrencyServiceImpl : CryptoService {

    @Autowired
    private lateinit var binanceApi: BinanceApi


    override fun getCryptoQuote(cryptoActiveName: CryptoCurrencyEnum): Float? {
        return try {
            binanceApi.getCryptoCurrencyValue(cryptoActiveName.name)
        } catch (e: Exception) {
            null
        }
    }

    override fun showCryptoAssetQuotes(): Map<String, Float?> {
        return try {
            val allQuotes = binanceApi.showCryptoAssetQuotes()
            val cryptoAssets = CryptoCurrencyEnum.values().map { it.name }
            allQuotes.filterKeys { it in cryptoAssets }
        } catch (e: Exception) {
            emptyMap()
        }

    }

    override fun getCryptoCurrencyValueUSDTtoARS(): BigDecimal? {
        return try {
            val value = binanceApi.getCryptoCurrencyValue("USDTARS").toString()
            BigDecimal(value)
        } catch (e: Exception) {
            null
        }
    }

    override fun showCryptoAssetQuotesLast24Hours(cryptoCurrency: CryptoCurrencyEnum): List<String> {
        val quotes = mutableListOf<String>()
        try {
            val data = binanceApi.getCryptoCurrencyValueHistory(cryptoCurrency.name, "1h", 24)
            quotes.add("Quotes from the last 24 hours for ${cryptoCurrency.name}:")
            for (quote in data) {
                val dateTime = LocalDateTime.parse(quote["timestamp"], DateTimeFormatter.ISO_DATE_TIME)
                val price = quote["price"]
                quotes.add("Quote Day and Time: $dateTime - Crypto Asset Quote: $price")
            }
        } catch (e: Exception) {
            quotes.add("Error getting quotes for ${cryptoCurrency.name}: ${e.message}")
        }
        return quotes
    }
    override fun showCryptoAssetQuotesEvery10Minutes(): Map<String, List<String>> {
        val quotesByCrypto = mutableMapOf<String, List<String>>()

        try {
            val cryptoAssets = CryptoCurrencyEnum.values()
            for (crypto in cryptoAssets) {
                val quotes = mutableListOf<String>()
                val dataFirstInterval = binanceApi.getCryptoCurrencyValueHistory(crypto.name, "5m", 12)
                val dataSecondInterval = binanceApi.getCryptoCurrencyValueHistory(crypto.name, "5m", 12)

                val combinedData = combineData(dataFirstInterval, dataSecondInterval)

                quotes.add("Quotes every 10 minutes for ${crypto.name}:")
                for (quote in combinedData) {
                    val dateTime = LocalDateTime.parse(quote["timestamp"], DateTimeFormatter.ISO_DATE_TIME)
                    val price = quote["price"]
                    quotes.add("Active Crypto: ${crypto.name}, Price: $price, Update time: $dateTime")
                }
                quotesByCrypto[crypto.name] = quotes
            }
        } catch (e: Exception) {
            quotesByCrypto["error"] = listOf("Error getting quotes: ${e.message}")
        }

        return quotesByCrypto
    }


    fun combineData(data1: List<Map<String, String>>, data2: List<Map<String, String>>): List<Map<String, String>> {
        if (data1.size != data2.size) {
            throw IllegalArgumentException("Los datos de los intervalos no tienen la misma longitud")
        }

        val combinedData = mutableListOf<Map<String, String>>()

        for (i in data1.indices) {
            val timestamp1 = data1[i]["timestamp"]
            val timestamp2 = data2[i]["timestamp"]

            if (timestamp1 != timestamp2) {
                throw IllegalArgumentException("Los timestamps de los datos no coinciden")
            }

            val price1 = data1[i]["price"]?.toFloatOrNull() ?: continue
            val price2 = data2[i]["price"]?.toFloatOrNull() ?: continue
            val averagePrice = (price1 + price2) / 2

            val combinedItem = mapOf(
                "timestamp" to timestamp1!!,
                "price" to averagePrice.toString()
            )

            combinedData.add(combinedItem)
        }

        return combinedData
    }

}