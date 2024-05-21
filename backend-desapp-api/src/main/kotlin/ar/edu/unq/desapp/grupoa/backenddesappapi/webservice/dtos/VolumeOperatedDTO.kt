package ar.edu.unq.desapp.grupoa.backenddesappapi.webservice.dtos

import java.math.BigDecimal
import java.time.LocalDateTime

data class VolumeOperatedDTO (
    val timestamp: LocalDateTime,
    val totalUSD: BigDecimal,
    val totalARS: BigDecimal,
    val assets: List<CryptoAssetDTO>
)