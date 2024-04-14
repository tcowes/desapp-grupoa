package ar.edu.unq.desapp.grupoa.backenddesappapi.service

import ar.edu.unq.desapp.grupoa.backenddesappapi.model.User
import ar.edu.unq.desapp.grupoa.backenddesappapi.repositories.UserRepository
import jakarta.persistence.EntityNotFoundException
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class UserService {
    @Autowired
    private lateinit var userRepository: UserRepository

    fun getUserById(userId: Long): User {
        return userRepository.findById(userId).orElseThrow { EntityNotFoundException("User not found with id: $userId") }
    }

    fun createUser(user: User): User {
        return userRepository.save(user)
    }
}