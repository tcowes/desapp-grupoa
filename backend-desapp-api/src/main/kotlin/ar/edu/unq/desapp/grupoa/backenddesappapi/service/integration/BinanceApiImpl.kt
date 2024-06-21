package ar.edu.unq.desapp.grupoa.backenddesappapi.service.integration
import com.github.kittinunf.fuel.httpGet
import com.github.kittinunf.result.Result
import org.json.JSONArray
import org.json.JSONObject
import org.springframework.stereotype.Service
import java.time.Instant
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter

@Service
class BinanceApiImpl : BinanceApi {
    override fun getCryptoCurrencyValue(symbol: String): Float {
        val url = "https://api.binance.com/api/v3/ticker/price?symbol=$symbol"
        val (_, _, result) = url.httpGet().responseString()
        return when (result) {
            is Result.Success -> JSONObject(result.get())["price"].toString().toFloat()
            is Result.Failure -> throw RuntimeException("Error getting quote for: $symbol: ${result.error}")
        }
    }

    override fun showCryptoAssetQuotes(): Map<String, Float?> {
        val url = "https://api.binance.com/api/v3/ticker/price"
        val (_, _, result) = url.httpGet().responseString()
        return when (result) {
            is Result.Success -> parseCryptoAssetQuotes(result.get())
            is Result.Failure -> throw RuntimeException("Error getting crypto asset quotes: ${result.error}")
        }
    }

    override fun getCryptoCurrencyValueHistory(symbol: String, interval: String, hours: Int): List<Map<String, String>> {
        val endTime = Instant.now().toEpochMilli()
        val startTime = endTime - hours * 60 * 60 * 1000

        val url = "https://api.binance.com/api/v3/klines?symbol=$symbol&interval=$interval&startTime=$startTime&endTime=$endTime"
        val (_, _, result) = url.httpGet().responseString()
        return when (result) {
            is Result.Success -> parseHistoryData(result.get())
            is Result.Failure -> throw RuntimeException("Error getting history data for: $symbol: ${result.error}")
        }
    }

    private fun parseHistoryData(response: String): List<Map<String, String>> {
        val historyData = mutableListOf<Map<String, String>>()
        val dataArray = JSONArray(response)
        for (data in dataArray) {
            val timestamp = Instant.ofEpochMilli((data as JSONArray)[0] as Long)
            val formattedTimestamp = DateTimeFormatter.ISO_INSTANT.withZone(ZoneOffset.UTC).format(timestamp)
            val price = (data[4] as String).toDouble()
            val entry = mapOf("timestamp" to formattedTimestamp, "price" to price.toString())
            historyData.add(entry)
        }
        return historyData
    }

    private fun parseCryptoAssetQuotes(response: String): Map<String, Float?> {
        val quotesMap = mutableMapOf<String, Float?>()
        val dataArray = JSONArray(response)
        for (i in 0 until dataArray.length()) {
            val jsonObject = dataArray.getJSONObject(i)
            val symbol = jsonObject.getString("symbol")
            val price = jsonObject.getString("price").toFloatOrNull()
            quotesMap[symbol] = price
        }
        return quotesMap
    }
}