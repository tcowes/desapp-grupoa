package ar.edu.unq.desapp.grupoa.backenddesappapi.service

import ar.edu.unq.desapp.grupoa.backenddesappapi.model.User

interface UserService {
    fun createUser(user: User): User
    fun getUserById(id: Long): User
    fun deleteAll()
}