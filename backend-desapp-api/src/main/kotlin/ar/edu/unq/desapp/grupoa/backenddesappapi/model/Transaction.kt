package ar.edu.unq.desapp.grupoa.backenddesappapi.model

import ar.edu.unq.desapp.grupoa.backenddesappapi.model.exceptions.InvalidTransactionState
import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
class Transaction(
    @ManyToOne
    @JoinColumn(name = "seller_id")
    var seller: User?,

    @ManyToOne
    @JoinColumn(name = "buyer_id")
    var buyer: User?,

    @Column(nullable = false)
    var cryptocurrency: CryptoCurrencyEnum,

    @Column
    var amount: Double,

    @Column
    var createdAt: LocalDateTime,

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    var status: TransactionStatus = TransactionStatus.PENDING
) {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null

    fun cancelTransaction(user: User) {
        if (status != TransactionStatus.PENDING) {
            throw InvalidTransactionState(TransactionStatus.CANCELED)
        }
        user.discountReputation(20)
        status = TransactionStatus.CANCELED
        buyer = null
        seller = null
    }
}