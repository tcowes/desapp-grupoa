package ar.edu.unq.desapp.grupoa.backenddesappapi.service.integration

interface BinanceApi {
    fun getCryptoCurrencyValue(symbol: String): Float
    fun showCryptoAssetQuotes(): Map<String, Float?>
    fun getCryptoCurrencyValueHistory(symbol: String, interval: String, hours: Int): List<Map<String, String>>
}