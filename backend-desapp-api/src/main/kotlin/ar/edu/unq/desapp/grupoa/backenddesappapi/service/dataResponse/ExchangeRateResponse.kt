package ar.edu.unq.desapp.grupoa.backenddesappapi.service.dataResponse

import java.math.BigDecimal

data class ExchangeRateResponse(
    val compra: BigDecimal,
    val venta: BigDecimal
)