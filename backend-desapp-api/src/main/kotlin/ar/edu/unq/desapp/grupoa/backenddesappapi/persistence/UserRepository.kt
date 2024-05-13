package ar.edu.unq.desapp.grupoa.backenddesappapi.persistence

import ar.edu.unq.desapp.grupoa.backenddesappapi.model.User
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query

interface UserRepository : JpaRepository<User, Long> {

    fun existsByEmail(email: String): Boolean
    fun existsByCvu(cvu: String): Boolean
    fun existsByWalletAddress(wallet: String): Boolean

}
