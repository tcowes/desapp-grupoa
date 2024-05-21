package ar.edu.unq.desapp.grupoa.backenddesappapi.webservice.dtos

import java.math.BigDecimal

data class CryptoAssetDTO (
    val cryptoAsset: String,
    val amount: Double,
    val currentPriceUSD: BigDecimal,
    val currentPriceARS: BigDecimal
)