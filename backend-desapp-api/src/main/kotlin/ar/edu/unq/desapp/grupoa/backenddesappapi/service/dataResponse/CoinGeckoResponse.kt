package ar.edu.unq.desapp.grupoa.backenddesappapi.service.dataResponse

import java.math.BigDecimal

data class CoinGeckoResponse(
    val market_data: MarketData? = null
) {
    data class MarketData(
        val current_price: Map<String, BigDecimal>? = null
    )
}