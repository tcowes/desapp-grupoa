package ar.edu.unq.desapp.grupoa.backenddesappapi.webservice.dtos

import ar.edu.unq.desapp.grupoa.backenddesappapi.model.*
import org.springframework.boot.autoconfigure.amqp.RabbitConnectionDetails.Address

data class TransactionDTO(
    val cryptoactive: CryptoCurrencyEnum,
    val amountOfCrypto: Double,
    val lastQuotation: Double,
    val userFirstName: String,
    val userLastName: String,
    val userAmountOfTransactions: Int,
    val userReputation: Any,
    val address: String,
    val action: String,
    val cancelAction: String
) {

    companion object {
        fun fromModel(transaction: Transaction, user: User): TransactionDTO {
            val totalTransactions: Int = user.transactionsAsBuyer.size + user.transactionsAsSeller.size
            val reputation: Any = if (user.reputation == 0.0) {
                "No operations"
            } else {
                totalTransactions / user.reputation
            }
            val address: String
            val action: String
            if (transaction.buyer!!.id == user.id) {
                // Es operacion de compra
                address = user.walletAddress
                action = "Please proceed to transfer"
            } else {
                address = user.cvu
                action = "Please confirm reception"
            }
            return TransactionDTO(
                transaction.cryptocurrency,
                transaction.amount,
                transaction.price,
                user.name,
                user.surname,
                totalTransactions,
                reputation,
                address,
                action,
                "CANCEL"
            )
        }
    }

}