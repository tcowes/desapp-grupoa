package ar.edu.unq.desapp.grupoa.backenddesappapi.service.impl
import ar.edu.unq.desapp.grupoa.backenddesappapi.model.CryptoCurrencyEnum
import ar.edu.unq.desapp.grupoa.backenddesappapi.service.BinanceApi
import org.springframework.stereotype.Service

@Service
class CryptoCurrencyService(private val binanceApi: BinanceApi) {

    fun showCryptoAssetQuotes(): List<String> {
        val cryptoassets = CryptoCurrencyEnum.values().toList()
        val quotes = mutableListOf<String>()
        for (cryptoasset in cryptoassets) {
            try {
                val data = binanceApi.getCryptoCurrencyValue(cryptoasset.name)
                quotes.add("${cryptoasset.name}: $data")
            } catch (e: Exception) {
                quotes.add("Error getting quote for ${cryptoasset.name}: ${e.message}")
            }
        }
        return quotes
    }
}