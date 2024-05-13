package ar.edu.unq.desapp.grupoa.backenddesappapi.service.impl

import ar.edu.unq.desapp.grupoa.backenddesappapi.model.User
import ar.edu.unq.desapp.grupoa.backenddesappapi.model.exceptions.*
import ar.edu.unq.desapp.grupoa.backenddesappapi.persistence.UserRepository
import ar.edu.unq.desapp.grupoa.backenddesappapi.service.UserService
import jakarta.persistence.EntityNotFoundException
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class UserServiceImpl : UserService {
    @Autowired
    private lateinit var userRepository: UserRepository

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
                is BadBankDataException -> throw ErrorCreatingUser(ex.message!!)

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

    override fun deleteAll() {
        userRepository.deleteAll()
    }
}