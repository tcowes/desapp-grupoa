package ar.edu.unq.desapp.grupoa.backenddesappapi.service.impl

import ar.edu.unq.desapp.grupoa.backenddesappapi.model.Transaction
import ar.edu.unq.desapp.grupoa.backenddesappapi.model.User
import ar.edu.unq.desapp.grupoa.backenddesappapi.model.exceptions.*
import ar.edu.unq.desapp.grupoa.backenddesappapi.persistence.IntentionRepository
import ar.edu.unq.desapp.grupoa.backenddesappapi.persistence.TransactionRepository
import ar.edu.unq.desapp.grupoa.backenddesappapi.persistence.UserRepository
import ar.edu.unq.desapp.grupoa.backenddesappapi.service.CryptoService
import ar.edu.unq.desapp.grupoa.backenddesappapi.service.UserService
import jakarta.persistence.EntityNotFoundException
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.Clock

@Service
@Transactional
class UserServiceImpl : UserService {
    @Autowired
    private lateinit var userRepository: UserRepository

    @Autowired
    private lateinit var intentionRepository: IntentionRepository

    @Autowired
    private lateinit var transactionRepository: TransactionRepository

    @Autowired
    private lateinit var cryptoService: CryptoService

    override fun getUserById(id: Long): User {
        return userRepository.findById(id).orElseThrow { EntityNotFoundException("User not found with id: $id") }
    }

    override fun createUser(user: User): User {
        try {
            user.validateUserData()
        } catch (ex: Throwable) {
            when (ex) {
                is InvalidNameAttempException,
                is InvalidPasswordException,
                is InvalidEmailException,
                is BadAddressException,
                is BadBankDataException -> throw ErrorCreatingUserException(ex.message!!)

                else -> throw ex
            }
        }
        if (userRepository.existsByEmail(user.email)) throw UserAlreadyRegisteredException("email", user.email)
        if (userRepository.existsByCvu(user.cvu)) throw UserAlreadyRegisteredException("cvu", user.cvu)
        if (userRepository.existsByWalletAddress(user.walletAddress)) throw UserAlreadyRegisteredException(
            "wallet",
            user.walletAddress
        )
        return userRepository.save(user)
    }

    override fun beginTransaction(userId: Long, intentionId: Long, clock: Clock?): Transaction {
        val user = getUserById(userId)
        val intention = intentionRepository.findById(intentionId)
            .orElseThrow { EntityNotFoundException("User not found with id: $intentionId") }
        val actualPriceForCrypto = cryptoService.getCryptoQuote(intention.cryptoactive)
        val transaction = user.beginTransaction(intention!!, actualPriceForCrypto!!.toDouble(), clock)
        return transactionRepository.save(transaction)
    }

    override fun finishTransaction(userId: Long, transactionId: Long, clock: Clock?) {
        val user = getUserById(userId)
        val transaction = transactionRepository.findById(transactionId)
            .orElseThrow { EntityNotFoundException("User not found with id: $transactionId") }
        user.finishTransaction(transaction, clock)
        userRepository.save(user)
    }

    override fun listUsers(): Map<String, List<String>> {
        val usersByReputation = mutableMapOf<String, List<String>>()

        try {
            val users = userRepository.findAll()
            for (user in users) {
                val userInfo = mutableListOf<String>()

                userInfo.add("Name: ${user.name}")
                userInfo.add("Surname: ${user.surname}")
                userInfo.add("Number of Operations: ${user.transactionsAsSeller.size + user.transactionsAsBuyer.size}")
                userInfo.add("Reputation: ${user.reputation}")

                usersByReputation[user.name + " " + user.surname] = userInfo
            }
        } catch (e: Exception) {
            usersByReputation["error"] = listOf("Error getting list of users: ${e.message}")
        }

        return usersByReputation
    }

    override fun deleteAll() {
        userRepository.deleteAll()
    }
}