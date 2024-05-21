package ar.edu.unq.desapp.grupoa.backenddesappapi.service

import ar.edu.unq.desapp.grupoa.backenddesappapi.model.Transaction
import ar.edu.unq.desapp.grupoa.backenddesappapi.webservice.dtos.VolumeOperatedDTO
import java.time.LocalDateTime

interface TransactionService {
    fun getTransactionById(transactionId: Long): Transaction
    fun cancelTransaction(userId: Long, transactionId: Long)
    fun getVolumeOperated(userId: Long, startDate: LocalDateTime, endDate: LocalDateTime): VolumeOperatedDTO
    fun deleteAll()
}