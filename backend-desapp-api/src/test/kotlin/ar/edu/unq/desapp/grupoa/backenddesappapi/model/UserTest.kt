package ar.edu.unq.desapp.grupoa.backenddesappapi.model

import ar.edu.unq.desapp.grupoa.backenddesappapi.model.exceptions.*
import ar.edu.unq.desapp.grupoa.backenddesappapi.model.exceptions.exceptionsTransaction.SameUserForTransactionException
import jakarta.validation.Validation
import jakarta.validation.Validator
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.time.Clock
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneOffset

class UserTest {

    private lateinit var validator: Validator
    private lateinit var user: User

    @BeforeEach
    fun setUp() {
        validator = Validation.buildDefaultValidatorFactory().validator

        user = User(
            name = "John",
            surname = "Doe",
            email = "john.doe@gmail.com",
            address = "123 Main St",
            password = "Password1!",
            cvu = "1234567890123456789012",
            walletAddress = "12345678"
        )
    }

    @Test
    fun validateUserDataShouldNotThrowAnyExceptionForValidUserData() {
        assertDoesNotThrow { user.validateUserData() }
    }

    @Test
    fun validateUserDataShouldThrowInvalidNameAttempExceptionForInvalidName() {
        user.name = "Jo"
        assertThrows(InvalidNameAttempException::class.java) { user.validateUserData() }
    }

    @Test
    fun validateUserDataShouldThrowInvalidEmailExceptionForInvalidEmail() {
        user.email = "john.doegmail.com"
        assertThrows(InvalidEmailException::class.java) { user.validateUserData() }
    }

    @Test
    fun validateUserDataShouldThrowBadAddressExceptionForInvalidAddress() {
        user.address = "Short"
        assertThrows(BadAddressException::class.java) { user.validateUserData() }
    }

    @Test
    fun validateUserDataShouldThrowInvalidPasswordExceptionForInvalidPassword() {
        user.password = "pass"
        assertThrows(InvalidPasswordException::class.java) { user.validateUserData() }
    }

    @Test
    fun validateUserDataShouldThrowBadBankDataExceptionForinvalidCVU() {
        user.cvu = "123"
        assertThrows(BadBankDataException::class.java) { user.validateUserData() }
    }

    @Test
    fun validateUserDatasShouldThrowBadBankDataExceptionForInvalidWalletAddress() {
        user.walletAddress = "1234"
        assertThrows(BadBankDataException::class.java) { user.validateUserData() }
    }


    @Test
    fun discountReputationShouldDecreaseReputationButNotBelowZero() {
        user.reputation = 5.0
        user.discountReputation(10)
        assertEquals(0.0, user.reputation)
    }


    @Test
    fun beginTransactionShouldCreateAPendingTransactionIfConditionsAreMet() {
        val buyer = User(
            name = "Buyer",
            surname = "Doe",
            email = "buyer.doe@gmail.com",
            address = "456 Main St",
            password = "Password1!",
            cvu = "0987654321098765432109",
            walletAddress = "87654321"
        )
        buyer.id = 2

        val intention = Intention(
            cryptoactive = CryptoCurrencyEnum.AAVEUSDT,
            amountOfCrypto = 1.0,
            lastQuotation = 50000.0,
            amountInPesos = 50000.0,
            user = buyer,
            operation = OperationEnum.BUY,
            dateCreated = LocalDateTime.now(),
            available = true
        )

        val transaction = user.beginTransaction(intention, 49999.0)
        assertEquals(TransactionStatus.PENDING, transaction.status)
    }

    @Test
    fun aUserCannotBeginATransactionWithItsOwnIntention() {
        val intention = Intention(
            cryptoactive = CryptoCurrencyEnum.AAVEUSDT,
            amountOfCrypto = 1.0,
            lastQuotation = 50000.0,
            amountInPesos = 50000.0,
            user = user,
            operation = OperationEnum.BUY,
            dateCreated = LocalDateTime.now(),
            available = true
        )
        val error = assertThrows<SameUserForTransactionException> { user.beginTransaction(intention, 49999.0) }
        assertEquals(
            "Is not allowed for a user that initiated an intention to begin a transaction with that intention.",
            error.message
        )
    }

    @Test
    fun beginTransactionShouldCreateACanceledTransactionIfConditionsAreNotMet() {
        val buyer = User(
            name = "Buyer",
            surname = "Doe",
            email = "buyer.doe@gmail.com",
            address = "456 Main St",
            password = "Password1!",
            cvu = "0987654321098765432109",
            walletAddress = "87654321"
        )
        buyer.id = 2

        val intention = Intention(
            cryptoactive = CryptoCurrencyEnum.AAVEUSDT,
            amountOfCrypto = 1.0,
            lastQuotation = 50000.0,
            amountInPesos = 50000.0,
            user = buyer,
            operation = OperationEnum.BUY,
            dateCreated = LocalDateTime.now(),
            available = true
        )

        val transaction = user.beginTransaction(intention, 50001.0)
        assertEquals(TransactionStatus.CANCELED, transaction.status)
    }

    @Test
    fun finishTransactionShouldCompleteAPendingTransactionAndUpdateReputation() {
        val now = Instant.now()
        val clock = Clock.fixed(now, ZoneOffset.UTC)

        val buyer = User(
            name = "Jane",
            surname = "Smith",
            email = "jane.smith@gmail.com",
            address = "456 Main St",
            password = "Password1!",
            cvu = "1234567890123456789013",
            walletAddress = "87654321"
        )

        val transaction = Transaction(
            seller = user,
            buyer = buyer,
            cryptocurrency = CryptoCurrencyEnum.ATOMUSDT,
            amount = 1.0,
            price = 50000.0,
            createdAt = LocalDateTime.now(clock),
            status = TransactionStatus.PENDING
        )

        transaction.createdAt = LocalDateTime.now(clock).minusMinutes(29)

        user.finishTransaction(transaction, clock)
        assertEquals(TransactionStatus.COMPLETED, transaction.status)
        assertEquals(10.0, user.reputation)
        assertEquals(10.0, buyer.reputation)
    }
}
