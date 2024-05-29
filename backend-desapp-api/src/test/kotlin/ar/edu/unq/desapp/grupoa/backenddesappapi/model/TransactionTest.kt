package ar.edu.unq.desapp.grupoa.backenddesappapi.model

import ar.edu.unq.desapp.grupoa.backenddesappapi.model.exceptions.exceptionsTransaction.InvalidTransactionState
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.*
import java.time.LocalDateTime

class TransactionTest {

    private lateinit var user: User
    private lateinit var transaction: Transaction

    @BeforeEach
    fun setUp() {
        user = User(
            name = "John",
            surname = "Doe",
            email = "john.doe@gmail.com",
            address = "123 Main St",
            password = "Password1!",
            cvu = "1234567890123456789012",
            walletAddress = "12345678"
        )

        transaction = Transaction(
            seller = user,
            buyer = user,
            cryptocurrency = CryptoCurrencyEnum.AXSUSDT,
            amount = 1.0,
            price = 50000.0,
            createdAt = LocalDateTime.now(),
            status = TransactionStatus.PENDING
        )
    }

    @Test
    fun cancelTransactionShouldCancelAPendingTransaction() {
        transaction.cancelTransaction(user)
        assertEquals(TransactionStatus.CANCELED, transaction.status)
        assertNull(transaction.buyer)
        assertNull(transaction.seller)
        assertEquals(0.0, user.reputation)
    }

    @Test
    fun cancelTransactionShouldThrowInvalidTransactionStateIfTransactionIsNotPending() {
        transaction.status = TransactionStatus.COMPLETED
        val exception = assertThrows(InvalidTransactionState::class.java) {
            transaction.cancelTransaction(user)
        }
        assertEquals("Only PENDING transactions can be marked as CANCELED.", exception.message)
    }
}
