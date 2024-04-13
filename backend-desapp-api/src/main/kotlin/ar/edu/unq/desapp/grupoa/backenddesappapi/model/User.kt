package ar.edu.unq.desapp.grupoa.backenddesappapi.model
import jakarta.persistence.*

@Entity
@Table(name = "users")
class User(
    @Column(nullable = false)
    var name: String,

    @Column(nullable = false)
    var surname: String,

    @Column(nullable = false, unique = true)
    var email: String,

    @Column(nullable = false)
    var address: String,

    @Column(nullable = false)
    var password: String,

    @Column(nullable = false, unique = true)
    var cvu: String,

    @Column(nullable = false, unique = true)
    var walletAddress: String,

    @Column(nullable = false)
    var reputation: Double = 0.0,

    @OneToMany(mappedBy = "seller", cascade = [CascadeType.ALL], orphanRemoval = true)
    var transactionsAsSeller: MutableList<Transaction> = mutableListOf(),

    @OneToMany(mappedBy = "buyer", cascade = [CascadeType.ALL], orphanRemoval = true)
    var transactionsAsBuyer: MutableList<Transaction> = mutableListOf()
) {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null
}
