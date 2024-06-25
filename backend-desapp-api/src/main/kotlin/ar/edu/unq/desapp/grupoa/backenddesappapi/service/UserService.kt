package ar.edu.unq.desapp.grupoa.backenddesappapi.service

import ar.edu.unq.desapp.grupoa.backenddesappapi.model.Transaction
import ar.edu.unq.desapp.grupoa.backenddesappapi.model.User
import java.time.Clock

interface UserService {
    fun createUser(user: User): User
    fun getUserById(id: Long): User
    fun beginTransaction(userId: Long, intentionId: Long, clock: Clock?): Transaction
    fun finishTransaction(userId: Long, transactionId: Long, clock: Clock?)
    fun listUsers(): Map<String, List<String>>
    fun deleteAll()
}