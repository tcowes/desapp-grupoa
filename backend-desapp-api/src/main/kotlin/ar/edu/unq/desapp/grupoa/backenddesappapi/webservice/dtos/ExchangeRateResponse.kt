package ar.edu.unq.desapp.grupoa.backenddesappapi.webservice.dtos

import java.math.BigDecimal

data class ExchangeRateResponse(
    val compra: BigDecimal,
    val venta: BigDecimal
)