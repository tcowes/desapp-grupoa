package ar.edu.unq.desapp.grupoa.backenddesappapi.persistence

import ar.edu.unq.desapp.grupoa.backenddesappapi.model.Intention
import org.springframework.data.jpa.repository.JpaRepository

interface IntentionRepository : JpaRepository<Intention, Long> {
    fun findAllByAvailableIsTrue(): List<Intention>
}