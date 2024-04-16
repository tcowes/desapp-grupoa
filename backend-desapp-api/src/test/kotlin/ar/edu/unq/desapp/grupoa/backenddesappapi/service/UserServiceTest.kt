package ar.edu.unq.desapp.grupoa.backenddesappapi.service

import ar.edu.unq.desapp.grupoa.backenddesappapi.model.User
import ar.edu.unq.desapp.grupoa.backenddesappapi.model.exceptions.ErrorCreatingUser
import jakarta.persistence.EntityNotFoundException
import org.junit.jupiter.api.*
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class UserServiceTest {
	@Autowired
	private lateinit var userService: UserService
	private lateinit var userToCreate: User

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
	fun findByIdWithExistingIdReturnsUser(){
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
			Arguments.of(User(
				"",
				"Nakamoto",
				"satonaka@gmail.com",
				"Fake Street 123",
				"Security1234!",
				"0011223344556677889911",
				"01234567",
			), "Name and surname should have at least 3 characters and a maximum of 30 characters."),
			Arguments.of(User(
				"S",
				"Nakamoto",
				"satonaka@gmail.com",
				"Fake Street 123",
				"Security1234!",
				"0011223344556677889911",
				"01234567",
			), "Name and surname should have at least 3 characters and a maximum of 30 characters."),
			Arguments.of(User(
				"Satoshiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiii",
				"Nakamoto",
				"satonaka@gmail.com",
				"Fake Street 123",
				"Security1234!",
				"0011223344556677889911",
				"01234567",
			), "Name and surname should have at least 3 characters and a maximum of 30 characters."),
			Arguments.of(User(
				"Satoshi",
				"",
				"satonaka@gmail.com",
				"Fake Street 123",
				"Security1234!",
				"0011223344556677889911",
				"01234567",
			), "Name and surname should have at least 3 characters and a maximum of 30 characters."),
			Arguments.of(User(
				"Satoshi",
				"N",
				"satonaka@gmail.com",
				"Fake Street 123",
				"Security1234!",
				"0011223344556677889911",
				"01234567",
			), "Name and surname should have at least 3 characters and a maximum of 30 characters."),
			Arguments.of(User(
				"Satoshi",
				"Nakamotoooooooooooooooooooooooooooooooooooooooooooooo",
				"satonaka@gmail.com",
				"Fake Street 123",
				"Security1234!",
				"0011223344556677889911",
				"01234567",
			), "Name and surname should have at least 3 characters and a maximum of 30 characters."),
			Arguments.of(User(
				"Satoshi",
				"Nakamoto",
				"notvalidmail",
				"Fake Street 123",
				"Security1234!",
				"0011223344556677889911",
				"01234567",
			), "Not valid email format."),
			Arguments.of(User(
				"Satoshi",
				"Nakamoto",
				"satonaka@gmail.com",
				"F",
				"Security1234!",
				"0011223344556677889911",
				"01234567",
			), "Address should have at least 10 characters and a maximum of 30 characters."),
			Arguments.of(User(
				"Satoshi",
				"Nakamoto",
				"satonaka@gmail.com",
				"Fake street 1231231231231231321321321",
				"Security1234!",
				"0011223344556677889911",
				"01234567",
			), "Address should have at least 10 characters and a maximum of 30 characters."),
			Arguments.of(User(
				"Satoshi",
				"Nakamoto",
				"satonaka@gmail.com",
				"Fake Street 123",
				"s",
				"0011223344556677889911",
				"01234567",
			), "The password must have at least 6 characters, including at least one special character, one uppercase letter, and one lowercase letter."),
			Arguments.of(User(
				"Satoshi",
				"Nakamoto",
				"satonaka@gmail.com",
				"Fake Street 123",
				"security",
				"0011223344556677889911",
				"01234567",
			), "The password must have at least 6 characters, including at least one special character, one uppercase letter, and one lowercase letter."),
			Arguments.of(User(
				"Satoshi",
				"Nakamoto",
				"satonaka@gmail.com",
				"Fake Street 123",
				"SECURITY",
				"0011223344556677889911",
				"01234567",
			), "The password must have at least 6 characters, including at least one special character, one uppercase letter, and one lowercase letter."),
			Arguments.of(User(
				"Satoshi",
				"Nakamoto",
				"satonaka@gmail.com",
				"Fake Street 123",
				"sECURITY",
				"0011223344556677889911",
				"01234567",
			), "The password must have at least 6 characters, including at least one special character, one uppercase letter, and one lowercase letter."),
			Arguments.of(User(
				"Satoshi",
				"Nakamoto",
				"satonaka@gmail.com",
				"Fake Street 123",
				"",
				"0011223344556677889911",
				"01234567",
			), "The password must have at least 6 characters, including at least one special character, one uppercase letter, and one lowercase letter."),
			Arguments.of(User(
				"Satoshi",
				"Nakamoto",
				"satonaka@gmail.com",
				"Fake Street 123",
				"Security!",
				"",
				"01234567",
			), "CVU should have exactly 22 characters."),
			Arguments.of(User(
				"Satoshi",
				"Nakamoto",
				"satonaka@gmail.com",
				"Fake Street 123",
				"Security!",
				"",
				"01234567",
			), "CVU should have exactly 22 characters."),
			Arguments.of(User(
				"Satoshi",
				"Nakamoto",
				"satonaka@gmail.com",
				"Fake Street 123",
				"Security!",
				"00112233445566778899111",
				"01234567",
			), "CVU should have exactly 22 characters."),
			Arguments.of(User(
				"Satoshi",
				"Nakamoto",
				"satonaka@gmail.com",
				"Fake Street 123",
				"Security!",
				"0011223344556677889911",
				"",
			), "Crypto Wallet Address should have exactly 8 characters."),
			Arguments.of(User(
				"Satoshi",
				"Nakamoto",
				"satonaka@gmail.com",
				"Fake Street 123",
				"Security!",
				"0011223344556677889911",
				"1",
			), "Crypto Wallet Address should have exactly 8 characters."),
			Arguments.of(User(
				"Satoshi",
				"Nakamoto",
				"satonaka@gmail.com",
				"Fake Street 123",
				"Security!",
				"0011223344556677889911",
				"012345678",
			), "Crypto Wallet Address should have exactly 8 characters."),
		)
	}

	@AfterEach
	fun cleanUp() {
		userService.deleteAll()
	}
}
