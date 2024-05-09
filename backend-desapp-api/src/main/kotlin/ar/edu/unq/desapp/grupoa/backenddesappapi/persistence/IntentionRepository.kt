package ar.edu.unq.desapp.grupoa.backenddesappapi.persistence

import ar.edu.unq.desapp.grupoa.backenddesappapi.model.Intention
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query

interface IntentionRepository : JpaRepository<Intention, Long> {
    @Query("select case when count(u) > 0 then true else false end from User u where u.name = ?1 and u.surname = ?2")
    fun existsNameUser(nameUser: String, surnameUser: String): Boolean
}