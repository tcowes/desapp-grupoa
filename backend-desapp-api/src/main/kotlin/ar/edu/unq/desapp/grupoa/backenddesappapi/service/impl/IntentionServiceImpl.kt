package ar.edu.unq.desapp.grupoa.backenddesappapi.service.impl

import ar.edu.unq.desapp.grupoa.backenddesappapi.model.Intention
import ar.edu.unq.desapp.grupoa.backenddesappapi.model.exceptions.exceptionsIntention.ErrorCreatingIntention
import ar.edu.unq.desapp.grupoa.backenddesappapi.model.exceptions.exceptionsIntention.InvalidCryptoactiveException
import ar.edu.unq.desapp.grupoa.backenddesappapi.model.exceptions.exceptionsIntention.InvalidOperationException
import ar.edu.unq.desapp.grupoa.backenddesappapi.model.exceptions.exceptionsIntention.UsernameIntentException
import ar.edu.unq.desapp.grupoa.backenddesappapi.persistence.IntentionRepository
import ar.edu.unq.desapp.grupoa.backenddesappapi.service.IntentionService
import jakarta.persistence.EntityNotFoundException
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class IntentionServiceImpl : IntentionService {
    @Autowired
    private lateinit var intentionRepository: IntentionRepository
    override fun createIntention(intention: Intention): Intention {
        try{
            intention.validateIntentionData()
        } catch (ex: Throwable) {
            when (ex) {
                is InvalidOperationException,
                is InvalidCryptoactiveException,
                is UsernameIntentException -> throw ErrorCreatingIntention(ex.message!!)
                else -> throw ex
            }
        }
        if(!intentionRepository.existsNameUser(intention.nameUser, intention.surnameUser)) throw UsernameIntentException(intention.nameUser, intention.surnameUser)
        return intentionRepository.save(intention)
    }

    override fun getIntentionById(id: Long): Intention {
        return intentionRepository.findById(id).orElseThrow { EntityNotFoundException("Intention not found with id: $id") }
    }

    override fun deleteAll() {
        intentionRepository.deleteAll()
    }
}