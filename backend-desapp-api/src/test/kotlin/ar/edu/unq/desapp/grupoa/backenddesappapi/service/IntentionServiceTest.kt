package ar.edu.unq.desapp.grupoa.backenddesappapi.service

import ar.edu.unq.desapp.grupoa.backenddesappapi.model.CryptoCurrencyEnum
import ar.edu.unq.desapp.grupoa.backenddesappapi.model.OperationEnum
import ar.edu.unq.desapp.grupoa.backenddesappapi.model.User
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest


@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class IntentionServiceTest {

    @Autowired
    private lateinit var intentionService: IntentionService

    @Autowired
    private lateinit var userService: UserService

    private lateinit var user: User

    @BeforeEach
    fun setUp() {
        user = userService.createUser(
            User(
                "Satoshi",
                "Nakamoto",
                "satonaka@gmail.com",
                "Fake Street 123",
                "Security1234!",
                "0011223344556677889911",
                "01234567",
                5.0,
            )
        )
    }

    @Test
    fun listIntentionsForUserHaveTheUserId() {
        val intention1 = intentionService.createIntention(
            CryptoCurrencyEnum.ETHUSDT,
            2.0,
            3999.0,
            user.id!!,
            OperationEnum.BUY
        )
        val intention2 = intentionService.createIntention(
            CryptoCurrencyEnum.ETHUSDT,
            1.0,
            4070.0,
            user.id!!,
            OperationEnum.SELL
        )
        val intention3 = intentionService.createIntention(
            CryptoCurrencyEnum.ETHUSDT,
            1.0,
            4100.0,
            user.id!!,
            OperationEnum.SELL
        )
        val intentions = intentionService.listIntentionsForUser(user.id!!)

        assertTrue(intentions.size == 3)
        assertTrue(intentions.any { it.id == intention1.id && it.user.id == user.id })
        assertTrue(intentions.any { it.id == intention2.id && it.user.id == user.id })
        assertTrue(intentions.any { it.id == intention3.id && it.user.id == user.id })
    }

    @Test
    fun listIntentionsForUserThatHasNoIntentionsReturnsEmptyList() {
        assertTrue(intentionService.listIntentionsForUser(user.id!!).isEmpty())
    }

    @Test
    fun listIntentionsWithNonexistentUserReturnsEmptyList() {
        assertTrue(intentionService.listIntentionsForUser(999).isEmpty())
    }

    @AfterEach
    fun cleanUp() {
        intentionService.deleteAll()
        userService.deleteAll()
    }
}