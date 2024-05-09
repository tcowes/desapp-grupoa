package ar.edu.unq.desapp.grupoa.backenddesappapi.service

import ar.edu.unq.desapp.grupoa.backenddesappapi.model.Intention

interface IntentionService {
    fun createIntention(intention: Intention): Intention
    fun getIntentionById(id: Long): Intention
    fun deleteAll()
}