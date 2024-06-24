package ar.edu.unq.desapp.grupoa.backenddesappapi.service.impl

import ar.edu.unq.desapp.grupoa.backenddesappapi.model.CryptoCurrencyEnum
import ar.edu.unq.desapp.grupoa.backenddesappapi.model.Intention
import ar.edu.unq.desapp.grupoa.backenddesappapi.model.OperationEnum
import ar.edu.unq.desapp.grupoa.backenddesappapi.model.exceptions.exceptionsIntention.UsernameIntentException
import ar.edu.unq.desapp.grupoa.backenddesappapi.persistence.IntentionRepository
import ar.edu.unq.desapp.grupoa.backenddesappapi.persistence.UserRepository
import ar.edu.unq.desapp.grupoa.backenddesappapi.service.CryptoService
import ar.edu.unq.desapp.grupoa.backenddesappapi.service.IntentionService
import jakarta.persistence.EntityNotFoundException
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime

@Service
@Transactional
class IntentionServiceImpl : IntentionService {
    @Autowired
    private lateinit var intentionRepository: IntentionRepository

    @Autowired
    private lateinit var userRepository: UserRepository

    @Autowired
    private lateinit var cryptoService: CryptoService

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
            quantity * price * 1085,  // Valor simbolico del dolar, lo ideal sería traerlo actualizado de alguna api externa
            user,
            operation,
            LocalDateTime.now(),
        )
        val actualPriceForCrypto: Float? = cryptoService.getCryptoQuote(crypto)
        intention.validateIntentionData(actualPriceForCrypto)
        return intentionRepository.save(intention)
    }

    override fun updateIntention(intention: Intention): Intention {
        return intentionRepository.save(intention)
    }

    override fun listActiveIntentions(): List<Intention> {
        return intentionRepository.findAllByAvailableIsTrue()
    }

    override fun getIntentionById(id: Long): Intention {
        return intentionRepository.findById(id)
            .orElseThrow { EntityNotFoundException("Intention not found with id: $id") }
    }

    override fun deleteAll() {
        intentionRepository.deleteAll()
    }
}