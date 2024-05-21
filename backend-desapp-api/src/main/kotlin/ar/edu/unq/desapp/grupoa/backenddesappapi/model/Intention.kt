package ar.edu.unq.desapp.grupoa.backenddesappapi.model

import ar.edu.unq.desapp.grupoa.backenddesappapi.model.exceptions.exceptionsIntention.InvalidCryptoactiveException
import ar.edu.unq.desapp.grupoa.backenddesappapi.model.exceptions.exceptionsIntention.InvalidOperationException
import ar.edu.unq.desapp.grupoa.backenddesappapi.model.exceptions.exceptionsIntention.OutOfRangePriceException
import jakarta.persistence.*
import java.time.LocalDateTime


@Entity
class Intention(
    @Column(nullable = false)
    val cryptoactive: CryptoCurrencyEnum,

    @Column(nullable = false)
    val amountOfCrypto: Double,

    @Column(nullable = false)
    val lastQuotation: Double,

    @Column(nullable = false)
    val amountInPesos: Double,

    @ManyToOne(cascade = [CascadeType.ALL])
    val user: User,

    @Column(nullable = false)
    val operation: OperationEnum,

    @Column(nullable = false)
    val dateCreated: LocalDateTime,

    @Column(nullable = false)
    var available: Boolean = true
) {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null

    fun validateIntentionData(priceForCrypto: Float?) {
        if (!isValidCryptoactive(cryptoactive)) { throw InvalidCryptoactiveException() }
        if (!isValidOperation(operation)) { throw InvalidOperationException() }
        if (priceForCrypto == null || !(lastQuotation*0.95 <= priceForCrypto && priceForCrypto <= lastQuotation*1.05)) {
            throw OutOfRangePriceException()
        }
    }

    private fun isValidCryptoactive(cryptoactive: CryptoCurrencyEnum): Boolean {
        return cryptoactive in CryptoCurrencyEnum.entries.toTypedArray()
    }
    private fun isValidOperation(operation: OperationEnum): Boolean {
        return operation in OperationEnum.entries.toTypedArray()
    }
}