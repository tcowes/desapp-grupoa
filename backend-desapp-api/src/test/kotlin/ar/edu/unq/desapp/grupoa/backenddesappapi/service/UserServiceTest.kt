package ar.edu.unq.desapp.grupoa.backenddesappapi.service

import ar.edu.unq.desapp.grupoa.backenddesappapi.model.CryptoCurrencyEnum
import ar.edu.unq.desapp.grupoa.backenddesappapi.model.User
import ar.edu.unq.desapp.grupoa.backenddesappapi.model.exceptions.ErrorCreatingUserException
import ar.edu.unq.desapp.grupoa.backenddesappapi.model.exceptions.UserAlreadyRegisteredException
import ar.edu.unq.desapp.grupoa.backenddesappapi.model.exceptions.WrongPasswordException
import jakarta.persistence.EntityNotFoundException
import org.junit.jupiter.api.*
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import org.mockito.Mockito
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.security.core.authority.SimpleGrantedAuthority

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

    @Test
    fun loginSuccessfully() {
        val createdUser = userService.createUser(userToCreate)
        val loggedUser = userService.login(createdUser.email, "Security1234!")
        assertEquals(loggedUser.id, createdUser.id)
        assertEquals(loggedUser.email, createdUser.email)
    }

    @Test
    fun loginWithWrongPasswordThrowsException() {
        val createdUser = userService.createUser(userToCreate)
        val error = assertThrows<WrongPasswordException> {
            userService.login(createdUser.email, "NotSecurity1234!")
        }
        assertEquals("Couldn't log-in, incorrect password!", error.message)
    }

    @Test
    fun loginWithUnrecognizedEmailThrowsException() {
        val error = assertThrows<EntityNotFoundException> {
            userService.login("fakeemail@gmail.com", "NotSecurity1234!")
        }
        assertEquals("User not found with email: fakeemail@gmail.com", error.message)
    }

    @Test
    fun loadUserByUsernameReturnsDetails() {
        val createdUser = userService.createUser(userToCreate)
        val userDetails = userService.loadUserByUsername(createdUser.email)
        assertEquals(emptyList<SimpleGrantedAuthority>(), userDetails.authorities)
        assertEquals("satonaka@gmail.com", userDetails.username)
        assertTrue(userDetails.isEnabled)
        assertTrue(userDetails.isAccountNonExpired)
        assertTrue(userDetails.isAccountNonLocked)
        assertTrue(userDetails.isCredentialsNonExpired)
    }

    @Test
    fun loadUserByUsernameWithUnrecognizedEmailThrowsException() {
        val error = assertThrows<EntityNotFoundException> {
            userService.loadUserByUsername("fakeemail@gmail.com")
        }
        assertEquals("User not found with email: fakeemail@gmail.com", error.message)
    }

    @ParameterizedTest
    @MethodSource("invalidUsers")
    fun cantRegisterUsersWithInvalidData(user: User, expectedMessage: String) {
        val error = assertThrows<ErrorCreatingUserException> {
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

    @AfterEach
    fun cleanUp() {
        intentionService.deleteAll()
        transactionService.deleteAll()
        userService.deleteAll()
    }
}
