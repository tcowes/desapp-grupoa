package ar.edu.unq.desapp.grupoa.backenddesappapi.model
import ar.edu.unq.desapp.grupoa.backenddesappapi.model.exceptions.*
import jakarta.persistence.*
import jakarta.validation.constraints.*

@Entity
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

    fun validateUserData() {
        if (!isValidName(this.name) || !isValidName(this.surname)) { throw InvalidNameAttempException() }
        if (!isValidEmail(this.email)) { throw InvalidEmailException() }
        if (!isValidAddress()) { throw BadAddressException() }
        if (!isValidPassword(this.password)) { throw InvalidPasswordException() }
        if (!isValidBankData(this.cvu, 22)) { throw BadBankDataException("CVU", 22) }
        if (!isValidBankData(this.walletAddress,  8)) { throw BadBankDataException("Crypto Wallet Address", 8) }
    }

    private fun isValidName(name: String): Boolean {
        return IntRange(3, 30).contains(name.length)
    }
    private fun isValidEmail(email: String): Boolean {
        val regex = Regex("""^[A-Za-z0-9._%+-]+@(gmail\.com|hotmail\.com)$""")
        return regex.matches(email)
    }
    private fun isValidPassword(password: String): Boolean {
        // examples at: https://regex101.com/r/bdSKQE/1
        val regex = Regex("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\W).{6,}\$")
        return regex.matches(password)
    }
    private fun isValidBankData(bankAddress: String, expectedLength: Int): Boolean {
        val regex = Regex("^[0-9]+$")
        return regex.matches(bankAddress) && expectedLength == bankAddress.length
    }
    private fun isValidAddress() = IntRange(10, 30).contains(this.address.length)

}
