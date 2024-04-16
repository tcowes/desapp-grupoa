package ar.edu.unq.desapp.grupoa.backenddesappapi.persistence

import ar.edu.unq.desapp.grupoa.backenddesappapi.model.User
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query

interface UserRepository : JpaRepository<User, Long> {

    @Query("select count(u) = 1 from User u where u.email = ?1")
    fun existsByEmail(email: String): Boolean

    @Query("select count(u) = 1 from User u where u.cvu = ?1")
    fun existsByCVU(cvu: String): Boolean

    @Query("select count(u) = 1 from User u where u.walletAddress = ?1")
    fun existsByWallet(wallet: String): Boolean

}
