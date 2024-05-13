package ar.edu.unq.desapp.grupoa.backenddesappapi.service

import ar.edu.unq.desapp.grupoa.backenddesappapi.model.CryptoCurrencyEnum
import ar.edu.unq.desapp.grupoa.backenddesappapi.model.Intention
import ar.edu.unq.desapp.grupoa.backenddesappapi.model.OperationEnum

interface IntentionService {
    fun createIntention(crypto: CryptoCurrencyEnum, quantity: Double, price: Double, userId: Long, operation: OperationEnum): Intention
    fun listIntentionsForUser(userId: Long): List<Intention>
    fun getIntentionById(id: Long): Intention
    fun deleteAll()
}