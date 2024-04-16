package ar.edu.unq.desapp.grupoa.backenddesappapi.service.impl

import ar.edu.unq.desapp.grupoa.backenddesappapi.model.User
import ar.edu.unq.desapp.grupoa.backenddesappapi.model.exceptions.*
import ar.edu.unq.desapp.grupoa.backenddesappapi.persistence.UserRepository
import ar.edu.unq.desapp.grupoa.backenddesappapi.service.UserService
import jakarta.persistence.EntityNotFoundException
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class UserService: UserService {
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
        return userRepository.save(user)
    }

    override fun deleteAll() {
        userRepository.deleteAll()
    }
}