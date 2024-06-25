package ar.edu.unq.desapp.grupoa.backenddesappapi.service

import ar.edu.unq.desapp.grupoa.backenddesappapi.model.CryptoCurrencyEnum
import ar.edu.unq.desapp.grupoa.backenddesappapi.model.OperationEnum
import ar.edu.unq.desapp.grupoa.backenddesappapi.model.TransactionStatus
import ar.edu.unq.desapp.grupoa.backenddesappapi.model.User
import ar.edu.unq.desapp.grupoa.backenddesappapi.model.exceptions.exceptionsTransaction.InvalidTransactionState
import org.junit.jupiter.api.*
import org.junit.jupiter.api.Assertions.*
import org.mockito.Mockito
import org.mockito.Mockito.`when`
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean
import java.math.BigDecimal
import java.time.Clock
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId

@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class TransactionServiceTest {
    @Autowired
    private lateinit var intentionService: IntentionService

    @Autowired
    private lateinit var userService: UserService

    @Autowired
    private lateinit var transactionService: TransactionService

    @MockBean
    private lateinit var cryptoService: CryptoService
    private lateinit var userToCreate: User
    private lateinit var anotherUserToCreate: User
    private val defaultClock = Clock.systemDefaultZone()

    @BeforeEach
    fun setUp() {
        userToCreate = User(
            "Satoshi",
            "Nakamoto",
            "satonaka@gmail.com",
            "Fake Street 123",
            "Security1234!",
            "0011223344556677889911",
            "01234567",
            5.0,
        )

        anotherUserToCreate = User(
            "Itachi",
            "Uchiha",
            "longlivesasuke@gmail.com",
            "Konoha Barrio Uchiha",
            "Edotensei1234!=",
            "2222222222222222222222",
            "01234568",
        )

        `when`(cryptoService.getCryptoQuote(CryptoCurrencyEnum.ETHUSDT)).thenReturn(1000F)
    }

    @Test
    fun userBeginsTransactionAsSeller() {
        val anotherUser = userService.createUser(anotherUserToCreate)
        val user = userService.createUser(userToCreate)
        val intention = intentionService.createIntention(
            CryptoCurrencyEnum.ETHUSDT,
            2.0,
            1000.0,
            user.id!!,
            OperationEnum.BUY
        )
        val transaction = userService.beginTransaction(anotherUser.id!!, intention.id!!, defaultClock)
        assertNotNull(transaction.id)
        assertEquals(transaction.status, TransactionStatus.PENDING)
        assertEquals(transaction.buyer!!.id, user.id)
        assertEquals(transaction.seller!!.id, anotherUser.id)
        assertEquals(transaction.price, 1000.0)
        assertEquals(transaction.cryptocurrency, CryptoCurrencyEnum.ETHUSDT)
        val updatedIntention = intentionService.getIntentionById(intention.id!!)
        assertFalse(updatedIntention.available)
    }

    @Test
    fun userBeginsTransactionAsBuyer() {
        val anotherUser = userService.createUser(anotherUserToCreate)
        val user = userService.createUser(userToCreate)
        val intention = intentionService.createIntention(
            CryptoCurrencyEnum.ETHUSDT,
            2.0,
            1000.0,
            user.id!!,
            OperationEnum.SELL
        )
        val transaction = userService.beginTransaction(anotherUser.id!!, intention.id!!, defaultClock)
        assertNotNull(transaction.id)
        assertEquals(transaction.status, TransactionStatus.PENDING)
        assertEquals(transaction.buyer!!.id, anotherUser.id)
        assertEquals(transaction.seller!!.id, user.id)
        val updatedIntention = intentionService.getIntentionById(intention.id!!)
        assertFalse(updatedIntention.available)
    }

    @Test
    fun userBeginsTransactionAfterThePriceWasModifiedSoTheTransacationGetsCancelled() {
        val anotherUser = userService.createUser(anotherUserToCreate)
        val user = userService.createUser(userToCreate)
        val intentionBuy = intentionService.createIntention(
            CryptoCurrencyEnum.ETHUSDT,
            2.0,
            1000.0,
            user.id!!,
            OperationEnum.BUY
        )
        val intentionSell = intentionService.createIntention(
            CryptoCurrencyEnum.ETHUSDT,
            2.0,
            1000.0,
            user.id!!,
            OperationEnum.SELL
        )
        // Cuando se quiere concretar una compra y el precio está por encima de lo pautado por el vendedor
        // se cancela la transacción
        `when`(cryptoService.getCryptoQuote(CryptoCurrencyEnum.ETHUSDT)).thenReturn(1500F)
        val transactionBuy = userService.beginTransaction(anotherUser.id!!, intentionBuy.id!!, defaultClock)
        assertNotNull(transactionBuy.id)
        assertEquals(transactionBuy.status, TransactionStatus.CANCELED)
        assertNull(transactionBuy.buyer)
        assertNull(transactionBuy.seller)
        assertEquals(transactionBuy.price, 1500.0)
        assertEquals(transactionBuy.cryptocurrency, CryptoCurrencyEnum.ETHUSDT)
        val updatedIntentionBuy = intentionService.getIntentionById(intentionBuy.id!!)
        assertTrue(updatedIntentionBuy.available)
        // Cuando se quiere concretar una venta y el precio está por debajo de lo pautado por el comprador
        // se cancela la transacción
        Mockito.`when`(cryptoService.getCryptoQuote(CryptoCurrencyEnum.ETHUSDT)).thenReturn(500F)
        val transactionSell = userService.beginTransaction(anotherUser.id!!, intentionSell.id!!, defaultClock)
        assertNotNull(transactionSell.id)
        assertEquals(transactionSell.status, TransactionStatus.CANCELED)
        assertNull(transactionSell.buyer)
        assertNull(transactionSell.seller)
        assertEquals(transactionSell.price, 500.0)
        assertEquals(transactionSell.cryptocurrency, CryptoCurrencyEnum.ETHUSDT)
        val updatedIntentionSell = intentionService.getIntentionById(intentionSell.id!!)
        assertTrue(updatedIntentionSell.available)
    }


    @Test
    fun userFinishesTransactionInLessThan30MinutesSoItGets10Points() {
        val anotherUser = userService.createUser(anotherUserToCreate)
        val user = userService.createUser(userToCreate)
        val intention = intentionService.createIntention(
            CryptoCurrencyEnum.ETHUSDT,
            2.0,
            1000.0,
            user.id!!,
            OperationEnum.BUY
        )
        val clock = Clock.fixed(Instant.parse("2024-05-20T12:00:00Z"), ZoneId.systemDefault())
        val transaction = userService.beginTransaction(anotherUser.id!!, intention.id!!, clock)
        val clockAfter5Minutes = Clock.fixed(Instant.parse("2024-05-20T12:05:00Z"), ZoneId.systemDefault())
        userService.finishTransaction(anotherUser.id!!, transaction.id!!, clockAfter5Minutes)
        val userUpdated = userService.getUserById(user.id!!)
        val anotherUserUpdated = userService.getUserById(anotherUser.id!!)
        assertEquals(userUpdated.reputation, 15.0)  // ya tenía 5 de antes
        assertEquals(anotherUserUpdated.reputation, 10.0)
    }

    @Test
    fun userFinishesTransactionInMoreThan30MinutesSoItGets5Points() {
        val anotherUser = userService.createUser(anotherUserToCreate)
        val user = userService.createUser(userToCreate)
        val intention = intentionService.createIntention(
            CryptoCurrencyEnum.ETHUSDT,
            2.0,
            1000.0,
            user.id!!,
            OperationEnum.BUY
        )
        val clock = Clock.fixed(Instant.parse("2024-05-20T12:00:00Z"), ZoneId.systemDefault())
        val transaction = userService.beginTransaction(anotherUser.id!!, intention.id!!, clock)
        val clockAfter31Minutes = Clock.fixed(Instant.parse("2024-05-20T12:31:00Z"), ZoneId.systemDefault())
        userService.finishTransaction(anotherUser.id!!, transaction.id!!, clockAfter31Minutes)
        val userUpdated = userService.getUserById(user.id!!)
        val anotherUserUpdated = userService.getUserById(anotherUser.id!!)
        assertEquals(userUpdated.reputation, 10.0)  // ya tenía 5 de antes
        assertEquals(anotherUserUpdated.reputation, 5.0)
    }

    @Test
    fun transactionCannotBeFinishedTwice() {
        val anotherUser = userService.createUser(anotherUserToCreate)
        val user = userService.createUser(userToCreate)
        val intention = intentionService.createIntention(
            CryptoCurrencyEnum.ETHUSDT,
            2.0,
            1000.0,
            user.id!!,
            OperationEnum.BUY
        )
        val transaction = userService.beginTransaction(anotherUser.id!!, intention.id!!, defaultClock)
        userService.finishTransaction(anotherUser.id!!, transaction.id!!, defaultClock)
        val error = assertThrows<InvalidTransactionState> {
            userService.finishTransaction(anotherUser.id!!, transaction.id!!, defaultClock)
        }
        assertEquals("Only PENDING transactions can be marked as COMPLETED.", error.message)
    }

    @Test
    fun transactionCannotBeCancelledIfAlreadyFinished() {
        val anotherUser = userService.createUser(anotherUserToCreate)
        val user = userService.createUser(userToCreate)
        val intention = intentionService.createIntention(
            CryptoCurrencyEnum.ETHUSDT,
            2.0,
            1000.0,
            user.id!!,
            OperationEnum.BUY
        )
        val transaction = userService.beginTransaction(anotherUser.id!!, intention.id!!, defaultClock)
        userService.finishTransaction(anotherUser.id!!, transaction.id!!, defaultClock)
        val error = assertThrows<InvalidTransactionState> {
            transactionService.cancelTransaction(anotherUser.id!!, transaction.id!!)
        }
        assertEquals("Only PENDING transactions can be marked as CANCELED.", error.message)
    }

    @Test
    fun transactionGetsCancelledSoUserDecrementsItsReputation() {
        anotherUserToCreate.reputation = 50.0
        val anotherUser = userService.createUser(anotherUserToCreate)
        val user = userService.createUser(userToCreate)
        val intention = intentionService.createIntention(
            CryptoCurrencyEnum.ETHUSDT,
            2.0,
            1000.0,
            user.id!!,
            OperationEnum.BUY
        )
        val transaction = userService.beginTransaction(anotherUser.id!!, intention.id!!, defaultClock)
        transactionService.cancelTransaction(anotherUser.id!!, transaction.id!!)
        val userUpdated = userService.getUserById(anotherUser.id!!)
        val transactionUpdated = transactionService.getTransactionById(transaction.id!!)
        assertEquals(30.0, userUpdated.reputation)
        assertNull(transactionUpdated.buyer)
        assertNull(transactionUpdated.seller)
        assertEquals(TransactionStatus.CANCELED, transactionUpdated.status)
    }


    @Test
    fun getVolumeOperatedReturnsOnlyFinishedTransactions() {
        val startDate = LocalDateTime.now().minusDays(7)
        val endDate = LocalDateTime.now().plusDays(1)

        val user = userService.createUser(userToCreate)
        val anotherUser = userService.createUser(anotherUserToCreate)

        val anIntention = intentionService.createIntention(
            CryptoCurrencyEnum.ETHUSDT,
            2.0,
            1000.0,
            user.id!!,
            OperationEnum.BUY
        )

        val anotherIntention = intentionService.createIntention(
            CryptoCurrencyEnum.ETHUSDT,
            2.0,
            1000.0,
            anotherUser.id!!,
            OperationEnum.SELL
        )

        val aTransaction = userService.beginTransaction(anotherUser.id!!, anIntention.id!!, defaultClock)
        userService.finishTransaction(user.id!!, aTransaction.id!!, defaultClock)
        // Iniciamos otra transaccion, pero esta no será finalizada y por ende quedará como pendiente
        userService.beginTransaction(user.id!!, anotherIntention.id!!, defaultClock)


        `when`(cryptoService.getCryptoCurrencyValueUSDTtoARS()).thenReturn(BigDecimal.valueOf(78.5))
        val result = transactionService.getVolumeOperated(user.id!!, startDate, endDate)

        assertNotNull(result)
        assertEquals(BigDecimal("2000.0"), result.totalUSD)
        assertEquals(BigDecimal("157000.00"), result.totalARS)
    }

    @AfterEach
    fun cleanUp() {
        intentionService.deleteAll()
        transactionService.deleteAll()
        userService.deleteAll()
    }
}
