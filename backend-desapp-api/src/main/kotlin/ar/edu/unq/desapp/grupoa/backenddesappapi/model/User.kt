package ar.edu.unq.desapp.grupoa.backenddesappapi.model
import jakarta.persistence.*
import jakarta.validation.constraints.*

@Entity
@Table(name = "users")
class User(
    @Column(nullable = false)
    @Size(min = 3, max = 30)
    var name: String,

    @Column(nullable = false)
    @Size(min = 3, max = 30)
    var surname: String,

    @Column(nullable = false, unique = true)
    @Email
    var email: String,

    @Column(nullable = false)
    @Size(min = 10, max = 30)
    var address: String,

    @Column(nullable = false)
    @Pattern.List(
        value = [
            Pattern(regexp = "(?=.*[a-z])", message = "Debe contener al menos una letra minúscula"),
            Pattern(regexp = "(?=.*[A-Z])", message = "Debe contener al menos una letra mayúscula"),
            Pattern(regexp = "(?=.*\\d)", message = "Debe contener al menos un dígito"),
            Pattern(regexp = "(?=.*[@#$%^&+=])", message = "Debe contener al menos un carácter especial"),
            Pattern(regexp = ".{6,}", message = "Debe tener al menos 6 caracteres")
        ]
    )
    var password: String,

    @Column(nullable = false, unique = true)
    @Pattern(regexp = "\\d{22}", message = "El CVU debe tener exactamente 22 dígitos")
    var cvu: String,

    @Column(nullable = false, unique = true)
    @Pattern(regexp = "\\d{8}", message = "La dirección de la billetera debe tener exactamente 8 dígitos")
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
