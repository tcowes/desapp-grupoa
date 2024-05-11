package ar.edu.unq.desapp.grupoa.backenddesappapi.model

import ar.edu.unq.desapp.grupoa.backenddesappapi.model.exceptions.exceptionsIntention.InvalidCryptoactiveException
import ar.edu.unq.desapp.grupoa.backenddesappapi.model.exceptions.exceptionsIntention.InvalidOperationException
import jakarta.persistence.*


@Entity
class Intention(
    @Column(nullable = false)
    val cryptoactive: CryptoCurrencyEnum,

    @Column(nullable = false)
    val symbol: String,

    @Column(nullable = false)
    val amountOfCrypto: Double,

    @Column(nullable = false)
    val lastQuotation: Double,

    @Column(nullable = false)
    val amountInPesos: Double,

    @Column(nullable = false)
    val nameUser: String,

    @Column(nullable = false)
    val surnameUser: String,

    @Column(nullable = false)
    val operation: OperationEnum,
) {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null

    fun validateIntentionData() {
        if (!isValidCryptoactive(cryptoactive)) { throw InvalidCryptoactiveException() }
        if (!isValidOperation(operation)) { throw InvalidOperationException() }
    }

    private fun isValidCryptoactive(cryptoactive: CryptoCurrencyEnum): Boolean {
        return cryptoactive in CryptoCurrencyEnum.values()
    }
    private fun isValidOperation(operation: OperationEnum): Boolean {
        return operation in OperationEnum.values()
    }
}