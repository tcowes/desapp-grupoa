package ar.edu.unq.desapp.grupoa.backenddesappapi.model.exceptions.exceptionsTransaction

import ar.edu.unq.desapp.grupoa.backenddesappapi.model.TransactionStatus

class InvalidTransactionState(private val state: TransactionStatus): Throwable() {
    override val message: String
        get() = "Only PENDING transactions can be marked as ${state}."
}
