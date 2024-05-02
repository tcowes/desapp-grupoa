package ar.edu.unq.desapp.grupoa.backenddesappapi.service

interface BinanceApi {
    fun getCryptoCurrencyValue(symbol: String): String
}