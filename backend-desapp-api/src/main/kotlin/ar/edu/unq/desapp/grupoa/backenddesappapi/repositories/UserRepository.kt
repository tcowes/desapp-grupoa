package ar.edu.unq.desapp.grupoa.backenddesappapi.repositories

import ar.edu.unq.desapp.grupoa.backenddesappapi.model.User
import org.springframework.data.jpa.repository.JpaRepository

interface UserRepository : JpaRepository<User, Long> {
}