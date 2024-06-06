import ar.edu.unq.desapp.grupoa.backenddesappapi.service.TransactionService
import ar.edu.unq.desapp.grupoa.backenddesappapi.webservice.TransactionController
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import org.springframework.http.ResponseEntity
import org.springframework.web.server.ResponseStatusException

@ExtendWith(MockitoExtension::class)
class TransactionControllerTest {

    @Mock
    private lateinit var transactionService: TransactionService

    @InjectMocks
    private lateinit var transactionController: TransactionController

    @Test
    fun getVolumeOperatedShouldReturnOKResponseWhenParametersAreValid() {
        val userId = 1L
        val startDate = "2024-06-01T00:00:00"
        val endDate = "2024-06-05T00:00:00"
        val expectedResponse = ResponseEntity.ok().build<Any>()

        val response = transactionController.getVolumeOperated(userId, startDate, endDate)

        assertEquals(expectedResponse, response)
    }

    @Test
    fun getVolumeOperatedShouldReturnBAD_REQUESTResponseWhenDateformatisinvalid() {
        val userId = 1L
        val startDate = "2024-06-01"
        val endDate = "2024-06-05"
        val expectedErrorMessage = "Invalid date format. Please use 'YYYY-MM-DDTHH:MM:SS'"

        val exception = assertThrows<ResponseStatusException> {
            transactionController.getVolumeOperated(userId, startDate, endDate)
        }

        assertEquals(expectedErrorMessage, exception.reason)
    }

    @Test
    fun getVolumeOperatedShouldThrowResponseStatusExceptionWhenDateTimeParseExceptionOccurs() {
        val userId = 1L
        val startDate = "2024-06-01T00:00:00"
        val endDate = "InvalidDate"

        assertThrows<ResponseStatusException> {
            transactionController.getVolumeOperated(userId, startDate, endDate)
        }
    }
}
