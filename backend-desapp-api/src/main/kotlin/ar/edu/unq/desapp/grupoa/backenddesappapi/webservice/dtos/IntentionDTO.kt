package ar.edu.unq.desapp.grupoa.backenddesappapi.webservice.dtos

import ar.edu.unq.desapp.grupoa.backenddesappapi.model.CryptoCurrencyEnum
import ar.edu.unq.desapp.grupoa.backenddesappapi.model.OperationEnum

data class IntentionDTO(
    val cryptoactive: CryptoCurrencyEnum, val amountOfCrypto: Double,
    val lastQuotation: Double, val amountInPesos: Double, val userId: Long, val operation: OperationEnum
)
