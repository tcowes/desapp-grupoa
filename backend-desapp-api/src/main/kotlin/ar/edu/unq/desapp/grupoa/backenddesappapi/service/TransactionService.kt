package ar.edu.unq.desapp.grupoa.backenddesappapi.service

interface TransactionService {
    fun cancelTransaction(userId: Long, transactionId: Long)
    fun deleteAll()
}