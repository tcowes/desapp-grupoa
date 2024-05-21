package ar.edu.unq.desapp.grupoa.backenddesappapi.service

import ar.edu.unq.desapp.grupoa.backenddesappapi.model.Transaction

interface TransactionService {
    fun getTransactionById(transactionId: Long): Transaction
    fun cancelTransaction(userId: Long, transactionId: Long)
    fun deleteAll()
}