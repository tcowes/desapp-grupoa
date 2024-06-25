package ar.edu.unq.desapp.grupoa.backenddesappapi.service.impl

import ar.edu.unq.desapp.grupoa.backenddesappapi.model.Transaction
import ar.edu.unq.desapp.grupoa.backenddesappapi.model.User
import ar.edu.unq.desapp.grupoa.backenddesappapi.model.exceptions.*
import ar.edu.unq.desapp.grupoa.backenddesappapi.persistence.IntentionRepository
import ar.edu.unq.desapp.grupoa.backenddesappapi.persistence.TransactionRepository
import ar.edu.unq.desapp.grupoa.backenddesappapi.persistence.UserRepository
import ar.edu.unq.desapp.grupoa.backenddesappapi.service.CryptoService
import ar.edu.unq.desapp.grupoa.backenddesappapi.service.UserService
import ar.edu.unq.desapp.grupoa.backenddesappapi.service.dto.UserDetailsDTO
import jakarta.persistence.EntityNotFoundException
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.Clock

@Service
@Transactional
class UserServiceImpl(
    private val userRepository: UserRepository,
    private val intentionRepository: IntentionRepository,
    private val transactionRepository: TransactionRepository,
    private val cryptoService: CryptoService
) : UserService {

    // Para evitar dependencias circulares, instanciamos el passwordEncoder acá:
    private val passwordEncoder: PasswordEncoder = BCryptPasswordEncoder()

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
        user.password = passwordEncoder.encode(user.password)
        return userRepository.save(user)
    }

    override fun login(email: String, password: String): User {
        val user = userRepository.findByEmail(email)
            ?: throw EntityNotFoundException("User not found with email: $email")
        if (!passwordEncoder.matches(password, user.password)) {
            throw WrongPasswordException()
        }
        return user
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

    override fun deleteAll() {
        userRepository.deleteAll()
    }

    override fun loadUserByUsername(username: String?): UserDetails {
        // username en este caso sería el email, hacemos esta simplificacion para no re-implementar el loadUserByUsername
        val user = userRepository.findByEmail(username!!)
            ?: throw EntityNotFoundException("User not found with email: $username")
        return UserDetailsDTO(user)
    }
}