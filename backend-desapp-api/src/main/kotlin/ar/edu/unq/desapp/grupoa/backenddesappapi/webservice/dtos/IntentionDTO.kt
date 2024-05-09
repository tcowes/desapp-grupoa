package ar.edu.unq.desapp.grupoa.backenddesappapi.webservice.dtos

import ar.edu.unq.desapp.grupoa.backenddesappapi.model.CryptoCurrencyEnum
import ar.edu.unq.desapp.grupoa.backenddesappapi.model.Intention
import ar.edu.unq.desapp.grupoa.backenddesappapi.model.OperationEnum

data class IntentionDTO (val cryptoactive: CryptoCurrencyEnum, val symbol: String, val amountOfCrypto: Double,
                         val lastQuotation: Double, val amountInPesos: Double, val nameUser: String,
                         val surnameUser: String, val operation: OperationEnum
) {
    fun toModel(): Intention {
        return Intention(cryptoactive = this.cryptoactive, symbol = this.symbol, amountOfCrypto = this.amountOfCrypto,
            lastQuotation = this.lastQuotation, amountInPesos = this.amountInPesos, nameUser = this.nameUser, surnameUser = this.surnameUser,
            operation = this.operation)
    }
}

