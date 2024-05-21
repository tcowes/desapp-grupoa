package ar.edu.unq.desapp.grupoa.backenddesappapi.webservice.dtos

import ar.edu.unq.desapp.grupoa.backenddesappapi.model.CryptoCurrencyEnum
import ar.edu.unq.desapp.grupoa.backenddesappapi.model.Intention
import ar.edu.unq.desapp.grupoa.backenddesappapi.model.OperationEnum
import java.time.LocalDateTime

data class CreationIntentionDTO(
    val cryptoactive: CryptoCurrencyEnum,
    val amountOfCrypto: Double,
    val lastQuotation: Double,
    val amountInPesos: Double,
    val userId: Long,
    val operation: OperationEnum,
    val dateCreated: LocalDateTime
)

data class IntentionDTO(
    val cryptoactive: CryptoCurrencyEnum,
    val amountOfCrypto: Double,
    val lastQuotation: Double,
    val amountInPesos: Double,
    val userFirstName: String,
    val userLastName: String,
    val userAmountOfTransactions: Int,
    val userReputation: Any,
    val operation: OperationEnum,
    val dateCreated: LocalDateTime
) {

    companion object {
        fun fromModel(intention: Intention): IntentionDTO {
            val totalTransactions: Int = intention.user.transactionsAsBuyer.size + intention.user.transactionsAsSeller.size
            val reputation: Any = if (intention.user.reputation == 0.0) { "No operations" } else { totalTransactions / intention.user.reputation }
            return IntentionDTO(
                intention.cryptoactive,
                intention.amountOfCrypto,
                intention.lastQuotation,
                intention.amountInPesos,
                intention.user.name,
                intention.user.surname,
                totalTransactions,
                reputation,
                intention.operation,
                intention.dateCreated,
            )
        }
    }

}

