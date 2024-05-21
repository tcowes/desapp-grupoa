package ar.edu.unq.desapp.grupoa.backenddesappapi.model

import ar.edu.unq.desapp.grupoa.backenddesappapi.model.exceptions.*
import jakarta.persistence.*
import jakarta.validation.constraints.Email
import jakarta.validation.constraints.Pattern
import jakarta.validation.constraints.Size
import java.time.Clock
import java.time.Duration
import java.time.LocalDateTime
import kotlin.math.max

@Entity
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
            Pattern(regexp = "(?=.*[a-z])", message = "Must contain at least one lower-case letter"),
            Pattern(regexp = "(?=.*[A-Z])", message = "Must contain at least one upper-case letter"),
            Pattern(regexp = "(?=.*[@#$%^&+=])", message = "Must contain at least one special character"),
            Pattern(regexp = ".{6,}", message = "Must contain at least 6 characters")
        ]
    )
    var password: String,

    @Column(nullable = false, unique = true)
    @Pattern(regexp = "\\d{22}", message = "Must contain exactly 22 characters")
    var cvu: String,

    @Column(nullable = false, unique = true)
    @Pattern(regexp = "\\d{22}", message = "Must contain exactly 8 characters")
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
        if (!isValidName(this.name) || !isValidName(this.surname)) {
            throw InvalidNameAttempException()
        }
        if (!isValidEmail(this.email)) {
            throw InvalidEmailException()
        }
        if (!isValidAddress()) {
            throw BadAddressException()
        }
        if (!isValidPassword(this.password)) {
            throw InvalidPasswordException()
        }
        if (!isValidBankData(this.cvu, 22)) {
            throw BadBankDataException("CVU", 22)
        }
        if (!isValidBankData(this.walletAddress, 8)) {
            throw BadBankDataException("Crypto Wallet Address", 8)
        }
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

    fun beginTransaction(intention: Intention, price: Double, clock: Clock? = Clock.systemDefaultZone()): Transaction {
        val now = LocalDateTime.now(clock)
        var shouldCancel = false
        val buyer: User?
        val seller: User?

        when (intention.operation) {
            OperationEnum.BUY -> {
                shouldCancel = price > intention.lastQuotation
                buyer = intention.user
                seller = this
            }

            OperationEnum.SELL -> {
                shouldCancel = price < intention.lastQuotation
                buyer = this
                seller = intention.user
            }
        }
        return if (shouldCancel) {
            Transaction(null, null, intention.cryptoactive, price, now, TransactionStatus.CANCELED)
        } else {
            intention.available = false
            Transaction(seller, buyer, intention.cryptoactive, price, now)
        }
    }

    fun finishTransaction(transaction: Transaction, clock: Clock? = Clock.systemDefaultZone()) {
        if (transaction.status != TransactionStatus.PENDING) {
            throw InvalidTransactionState(TransactionStatus.COMPLETED)
        }
        transaction.status = TransactionStatus.COMPLETED
        val now = LocalDateTime.now(clock)
        val duration = Duration.between(transaction.createdAt, now)
        val minutesPassed = duration.toMinutes()

        var points = 10
        if (minutesPassed > 30) {
            points = 5
        }
        transaction.buyer!!.increaseReputation(points)
        transaction.seller!!.increaseReputation(points)
    }

    fun discountReputation(amount: Int) {
        reputation = max(reputation - amount, 0.0)
    }

    private fun increaseReputation(amount: Int) {
        reputation += amount
    }

}
