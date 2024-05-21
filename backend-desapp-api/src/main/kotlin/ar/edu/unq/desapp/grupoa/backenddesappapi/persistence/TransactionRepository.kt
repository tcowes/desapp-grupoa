package ar.edu.unq.desapp.grupoa.backenddesappapi.persistence

import ar.edu.unq.desapp.grupoa.backenddesappapi.model.Transaction
import org.springframework.data.jpa.repository.JpaRepository

interface TransactionRepository : JpaRepository<Transaction, Long>