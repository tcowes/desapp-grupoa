package ar.edu.unq.desapp.grupoa.backenddesappapi.persistence

import ar.edu.unq.desapp.grupoa.backenddesappapi.model.Transaction
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import java.time.LocalDateTime

interface TransactionRepository : JpaRepository<Transaction, Long>{
    @Query("SELECT t FROM Transaction t WHERE (t.seller.id = :userId OR t.buyer.id = :userId) AND t.createdAt BETWEEN :startDate AND :endDate")
    fun findByUserIdAndDateRange(
        @Param("userId") userId: Long,
        @Param("startDate") startDate: LocalDateTime,
        @Param("endDate") endDate: LocalDateTime
    ): List<Transaction>

    fun existsBySellerId(sellerId: Long): Boolean
    fun existsByBuyerId(buyerId: Long): Boolean
}
