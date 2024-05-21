package ar.edu.unq.desapp.grupoa.backenddesappapi.service

import ar.edu.unq.desapp.grupoa.backenddesappapi.model.CryptoCurrencyEnum
import ar.edu.unq.desapp.grupoa.backenddesappapi.model.OperationEnum
import ar.edu.unq.desapp.grupoa.backenddesappapi.model.TransactionStatus
import ar.edu.unq.desapp.grupoa.backenddesappapi.model.User
import ar.edu.unq.desapp.grupoa.backenddesappapi.model.exceptions.ErrorCreatingUser
import ar.edu.unq.desapp.grupoa.backenddesappapi.model.exceptions.UserAlreadyRegisteredException
import jakarta.persistence.EntityNotFoundException
import org.junit.jupiter.api.*
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import org.mockito.Mockito
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean
import java.time.Clock
import java.time.Instant
import java.time.ZoneId

@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class UserServiceTest {
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

        Mockito.`when`(cryptoService.getCryptoQuote(CryptoCurrencyEnum.ETHUSDT)).thenReturn(1000F)
    }

    @Test
    fun aUserGetsCreatedSuccessfully() {
        userService.createUser(userToCreate)
        val userCreated = userService.getUserById(userToCreate.id!!)
        assertEquals(userToCreate.name, userCreated.name)
        assertEquals(userToCreate.surname, userCreated.surname)
        assertEquals(userToCreate.email, userCreated.email)
        assertEquals(userToCreate.address, userCreated.address)
        assertEquals(userToCreate.password, userCreated.password)
        assertEquals(userToCreate.cvu, userCreated.cvu)
        assertEquals(userToCreate.walletAddress, userCreated.walletAddress)
        assertEquals(userToCreate.reputation, userCreated.reputation)
    }

    @Test
    fun findByIdWithExistingIdReturnsUser() {
        val userCreated = userService.createUser(userToCreate)
        val user = userService.getUserById(userCreated.id!!)
        assertEquals(userCreated.id!!, user.id!!)

    }

    @Test
    fun findByIdWithNotExistingIdThrowsException() {
        val error = assertThrows<EntityNotFoundException> { userService.getUserById(9999) }
        assertEquals("User not found with id: 9999", error.message)
    }

    @Test
    fun usersGetCreatedWithZeroReputationByDefault() {
        val user = User(
            "Satoshi",
            "Nakamoto",
            "satonaka@gmail.com",
            "Fake Street 123",
            "Security1234!",
            "0011223344556677889911",
            "01234567",
        )
        userService.createUser(user)
        val userCreated = userService.getUserById(user.id!!)
        assertEquals(0.0, userCreated.reputation)
    }

    @Test
    fun cantRegisterUserWithDuplicatedEmail() {
        val userWithSameMail = User(
            "NotSatoshi",
            "NotNakamoto",
            "satonaka@gmail.com",
            "Fake Street 124",
            "Security1234!",
            "0011223344556677889912",
            "01234563",
        )
        userService.createUser(userToCreate)
        val error = assertThrows<UserAlreadyRegisteredException> {
            userService.createUser(userWithSameMail)
        }
        assertEquals(
            "There's a user with the email satonaka@gmail.com already registered in the database.",
            error.message
        )
    }

    @Test
    fun cantRegisterUserWithDuplicatedCvu() {
        val userWithSameMail = User(
            "NotSatoshi",
            "NotNakamoto",
            "notsatonaka@gmail.com",
            "Fake Street 124",
            "Security1234!",
            "0011223344556677889911",
            "01234563",
        )
        userService.createUser(userToCreate)
        val error = assertThrows<UserAlreadyRegisteredException> {
            userService.createUser(userWithSameMail)
        }
        assertEquals(
            "There's a user with the cvu 0011223344556677889911 already registered in the database.",
            error.message
        )
    }

    @Test
    fun cantRegisterUserWithDuplicatedWalletAddress() {
        val userWithSameMail = User(
            "NotSatoshi",
            "NotNakamoto",
            "notsatonaka@gmail.com",
            "Fake Street 124",
            "Security1234!",
            "0011223344556677889912",
            "01234567",
        )
        userService.createUser(userToCreate)
        val error = assertThrows<UserAlreadyRegisteredException> {
            userService.createUser(userWithSameMail)
        }
        assertEquals("There's a user with the wallet 01234567 already registered in the database.", error.message)
    }

    @ParameterizedTest
    @MethodSource("invalidUsers")
    fun cantRegisterUsersWithInvalidData(user: User, expectedMessage: String) {
        val error = assertThrows<ErrorCreatingUser> {
            userService.createUser(user)
        }
        assertEquals(expectedMessage, error.message)
    }

    companion object {
        @JvmStatic
        fun invalidUsers() = listOf(
            Arguments.of(
                User(
                    "",
                    "Nakamoto",
                    "satonaka@gmail.com",
                    "Fake Street 123",
                    "Security1234!",
                    "0011223344556677889911",
                    "01234567",
                ), "Name and surname should have at least 3 characters and a maximum of 30 characters."
            ),
            Arguments.of(
                User(
                    "S",
                    "Nakamoto",
                    "satonaka@gmail.com",
                    "Fake Street 123",
                    "Security1234!",
                    "0011223344556677889911",
                    "01234567",
                ), "Name and surname should have at least 3 characters and a maximum of 30 characters."
            ),
            Arguments.of(
                User(
                    "Satoshiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiii",
                    "Nakamoto",
                    "satonaka@gmail.com",
                    "Fake Street 123",
                    "Security1234!",
                    "0011223344556677889911",
                    "01234567",
                ), "Name and surname should have at least 3 characters and a maximum of 30 characters."
            ),
            Arguments.of(
                User(
                    "Satoshi",
                    "",
                    "satonaka@gmail.com",
                    "Fake Street 123",
                    "Security1234!",
                    "0011223344556677889911",
                    "01234567",
                ), "Name and surname should have at least 3 characters and a maximum of 30 characters."
            ),
            Arguments.of(
                User(
                    "Satoshi",
                    "N",
                    "satonaka@gmail.com",
                    "Fake Street 123",
                    "Security1234!",
                    "0011223344556677889911",
                    "01234567",
                ), "Name and surname should have at least 3 characters and a maximum of 30 characters."
            ),
            Arguments.of(
                User(
                    "Satoshi",
                    "Nakamotoooooooooooooooooooooooooooooooooooooooooooooo",
                    "satonaka@gmail.com",
                    "Fake Street 123",
                    "Security1234!",
                    "0011223344556677889911",
                    "01234567",
                ), "Name and surname should have at least 3 characters and a maximum of 30 characters."
            ),
            Arguments.of(
                User(
                    "Satoshi",
                    "Nakamoto",
                    "notvalidmail",
                    "Fake Street 123",
                    "Security1234!",
                    "0011223344556677889911",
                    "01234567",
                ), "Not valid email format."
            ),
            Arguments.of(
                User(
                    "Satoshi",
                    "Nakamoto",
                    "satonaka@gmail.com",
                    "F",
                    "Security1234!",
                    "0011223344556677889911",
                    "01234567",
                ), "Address should have at least 10 characters and a maximum of 30 characters."
            ),
            Arguments.of(
                User(
                    "Satoshi",
                    "Nakamoto",
                    "satonaka@gmail.com",
                    "Fake street 1231231231231231321321321",
                    "Security1234!",
                    "0011223344556677889911",
                    "01234567",
                ), "Address should have at least 10 characters and a maximum of 30 characters."
            ),
            Arguments.of(
                User(
                    "Satoshi",
                    "Nakamoto",
                    "satonaka@gmail.com",
                    "Fake Street 123",
                    "s",
                    "0011223344556677889911",
                    "01234567",
                ),
                "The password must have at least 6 characters, including at least one special character, one uppercase letter, and one lowercase letter."
            ),
            Arguments.of(
                User(
                    "Satoshi",
                    "Nakamoto",
                    "satonaka@gmail.com",
                    "Fake Street 123",
                    "security",
                    "0011223344556677889911",
                    "01234567",
                ),
                "The password must have at least 6 characters, including at least one special character, one uppercase letter, and one lowercase letter."
            ),
            Arguments.of(
                User(
                    "Satoshi",
                    "Nakamoto",
                    "satonaka@gmail.com",
                    "Fake Street 123",
                    "SECURITY",
                    "0011223344556677889911",
                    "01234567",
                ),
                "The password must have at least 6 characters, including at least one special character, one uppercase letter, and one lowercase letter."
            ),
            Arguments.of(
                User(
                    "Satoshi",
                    "Nakamoto",
                    "satonaka@gmail.com",
                    "Fake Street 123",
                    "sECURITY",
                    "0011223344556677889911",
                    "01234567",
                ),
                "The password must have at least 6 characters, including at least one special character, one uppercase letter, and one lowercase letter."
            ),
            Arguments.of(
                User(
                    "Satoshi",
                    "Nakamoto",
                    "satonaka@gmail.com",
                    "Fake Street 123",
                    "",
                    "0011223344556677889911",
                    "01234567",
                ),
                "The password must have at least 6 characters, including at least one special character, one uppercase letter, and one lowercase letter."
            ),
            Arguments.of(
                User(
                    "Satoshi",
                    "Nakamoto",
                    "satonaka@gmail.com",
                    "Fake Street 123",
                    "Security!",
                    "",
                    "01234567",
                ), "CVU should have exactly 22 characters."
            ),
            Arguments.of(
                User(
                    "Satoshi",
                    "Nakamoto",
                    "satonaka@gmail.com",
                    "Fake Street 123",
                    "Security!",
                    "",
                    "01234567",
                ), "CVU should have exactly 22 characters."
            ),
            Arguments.of(
                User(
                    "Satoshi",
                    "Nakamoto",
                    "satonaka@gmail.com",
                    "Fake Street 123",
                    "Security!",
                    "00112233445566778899111",
                    "01234567",
                ), "CVU should have exactly 22 characters."
            ),
            Arguments.of(
                User(
                    "Satoshi",
                    "Nakamoto",
                    "satonaka@gmail.com",
                    "Fake Street 123",
                    "Security!",
                    "0011223344556677889911",
                    "",
                ), "Crypto Wallet Address should have exactly 8 characters."
            ),
            Arguments.of(
                User(
                    "Satoshi",
                    "Nakamoto",
                    "satonaka@gmail.com",
                    "Fake Street 123",
                    "Security!",
                    "0011223344556677889911",
                    "1",
                ), "Crypto Wallet Address should have exactly 8 characters."
            ),
            Arguments.of(
                User(
                    "Satoshi",
                    "Nakamoto",
                    "satonaka@gmail.com",
                    "Fake Street 123",
                    "Security!",
                    "0011223344556677889911",
                    "012345678",
                ), "Crypto Wallet Address should have exactly 8 characters."
            ),
        )
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
        assertEquals(transaction.amount, 1000.0)
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
        Mockito.`when`(cryptoService.getCryptoQuote(CryptoCurrencyEnum.ETHUSDT)).thenReturn(1500F)
        val transactionBuy = userService.beginTransaction(anotherUser.id!!, intentionBuy.id!!, defaultClock)
        assertNotNull(transactionBuy.id)
        assertEquals(transactionBuy.status, TransactionStatus.CANCELED)
        assertNull(transactionBuy.buyer)
        assertNull(transactionBuy.seller)
        assertEquals(transactionBuy.amount, 1500.0)
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
        assertEquals(transactionSell.amount, 500.0)
        assertEquals(transactionSell.cryptocurrency, CryptoCurrencyEnum.ETHUSDT)
        val updatedIntentionSell = intentionService.getIntentionById(intentionSell.id!!)
        assertTrue(updatedIntentionSell.available)
    }


    @Test
    fun useFinishesTransactionInLessThan30MinutesSoItGets10Points() {
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
    fun useFinishesTransactionInMoreThan30MinutesSoItGets5Points() {
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

    @AfterEach
    fun cleanUp() {
        intentionService.deleteAll()
        transactionService.deleteAll()
        userService.deleteAll()
    }
}
