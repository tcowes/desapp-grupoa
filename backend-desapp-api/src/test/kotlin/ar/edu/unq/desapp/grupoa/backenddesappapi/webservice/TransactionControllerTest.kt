package ar.edu.unq.desapp.grupoa.backenddesappapi.webservice

import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultMatchers
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

@SpringBootTest
@AutoConfigureMockMvc
class TransactionControllerTest {
    @Autowired
    private lateinit var mockMvc: MockMvc

    @Test
    fun testGetVolumeOperatedWithValidParameters() {
        val request = MockMvcRequestBuilders.get("/transactions/volume")
            .param("userId", "1")
            .param("startDate", "2023-01-01T00:00:00")
            .param("endDate", "2023-01-31T2359:59")
            .contentType(MediaType.APPLICATION_JSON_VALUE)

        mockMvc.perform(request)
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.totalUSD").value(27000.00))
            .andExpect(jsonPath("$.totalARS").value(1080000.00))
    }

    @Test
    fun testGetVolumeOperatedWithInvalidDateFormat() {
        val request = MockMvcRequestBuilders.get("/transactions/volume")
            .param("userId", "1")
            .param("startDate", "2023-ABCD-01")
            .param("endDate", "2023-01-31T23:59:59")
            .contentType(MediaType.APPLICATION_JSON_VALUE)

        mockMvc.perform(request)
            .andExpect(status().isBadRequest)
            .andExpect(
                MockMvcResultMatchers.content().string("Invalid date format. Please use 'YYYY-MM-DDTHH:MM:SS'")
            )
    }
}