package ar.edu.unq.desapp.grupoa.backenddesappapi.model
import jakarta.persistence.*
import java.util.*

@Entity
class Transaction(
    @ManyToOne
    @JoinColumn(name = "seller_id")
    val seller: User,

    @ManyToOne
    @JoinColumn(name = "buyer_id")
    var buyer: User,

    @ManyToOne
    var cryptocurrency: Cryptocurrency,

    @Column
    var amount: Double,

    @Column
    var createdAt: Date
) {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    var status: TransactionStatus = TransactionStatus.PENDING
}