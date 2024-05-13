package ar.edu.unq.desapp.grupoa.backenddesappapi.service.impl

import ar.edu.unq.desapp.grupoa.backenddesappapi.model.CryptoCurrencyEnum
import ar.edu.unq.desapp.grupoa.backenddesappapi.model.Intention
import ar.edu.unq.desapp.grupoa.backenddesappapi.model.OperationEnum
import ar.edu.unq.desapp.grupoa.backenddesappapi.model.exceptions.exceptionsIntention.ErrorCreatingIntention
import ar.edu.unq.desapp.grupoa.backenddesappapi.model.exceptions.exceptionsIntention.InvalidCryptoactiveException
import ar.edu.unq.desapp.grupoa.backenddesappapi.model.exceptions.exceptionsIntention.InvalidOperationException
import ar.edu.unq.desapp.grupoa.backenddesappapi.model.exceptions.exceptionsIntention.UsernameIntentException
import ar.edu.unq.desapp.grupoa.backenddesappapi.persistence.IntentionRepository
import ar.edu.unq.desapp.grupoa.backenddesappapi.persistence.UserRepository
import ar.edu.unq.desapp.grupoa.backenddesappapi.service.IntentionService
import jakarta.persistence.EntityNotFoundException
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class IntentionServiceImpl : IntentionService {
    @Autowired
    private lateinit var intentionRepository: IntentionRepository

    @Autowired
    private lateinit var userRepository: UserRepository

    override fun createIntention(
        crypto: CryptoCurrencyEnum,
        quantity: Double,
        price: Double,
        userId: Long,
        operation: OperationEnum
    ): Intention {
        val user = userRepository.findById(userId).orElseThrow { UsernameIntentException(userId) }
        val intention = Intention(
            crypto,
            quantity,
            price,
            quantity * price * 1085, // TODO: 1085 es un valor actual del dolar, habría que saberlo en el momento
            user,
            operation
        )
        try {
            intention.validateIntentionData() // TODO: acá habría que validar que price no está fuera del margen del 10% en base a la cotización de la cripto
        } catch (ex: Throwable) {
            when (ex) {
                is InvalidOperationException,
                is InvalidCryptoactiveException -> throw ErrorCreatingIntention(ex.message!!)

                else -> throw ex
            }
        }
        return intentionRepository.save(intention)
    }

    override fun listIntentionsForUser(userId: Long): List<Intention> {
        return intentionRepository.findAllByUserId(userId)
    }

    override fun getIntentionById(id: Long): Intention {
        return intentionRepository.findById(id)
            .orElseThrow { EntityNotFoundException("Intention not found with id: $id") }
    }

    override fun deleteAll() {
        intentionRepository.deleteAll()
    }
}