package ar.edu.unq.desapp.grupoa.backenddesappapi.service.impl
import ar.edu.unq.desapp.grupoa.backenddesappapi.service.BinanceApi
import com.github.kittinunf.fuel.httpGet
import com.github.kittinunf.result.Result
import org.springframework.stereotype.Service

@Service
class BinanceApiImpl : BinanceApi {
    override fun getCryptoCurrencyValue(symbol: String): String {
        val url = "https://api.binance.com/api/v3/ticker/price?symbol=$symbol"
        val (_, _, result) = url.httpGet().responseString()
        return when (result) {
            is Result.Success -> result.get()
            is Result.Failure -> throw RuntimeException("Error getting quote for: $symbol: ${result.error}")
        }
    }
}