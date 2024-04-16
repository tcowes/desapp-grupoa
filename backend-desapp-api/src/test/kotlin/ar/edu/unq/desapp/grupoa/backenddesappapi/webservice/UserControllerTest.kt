package ar.edu.unq.desapp.grupoa.backenddesappapi.webservice

import ar.edu.unq.desapp.grupoa.backenddesappapi.service.UserService
import ar.edu.unq.desapp.grupoa.backenddesappapi.webservice.dtos.UserDTO
import com.fasterxml.jackson.databind.ObjectMapper
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultMatchers

@SpringBootTest
@AutoConfigureMockMvc
class UserControllerTest {
    @Autowired
    private lateinit var mockMvc: MockMvc
    @Autowired
    private lateinit var userService: UserService

    @Test
    fun userRegistrationReturns201Created() {
        val userData = UserDTO(
            name = "Satoshi",
            surname = "Nakamoto",
            email = "satonaka@gmail.com",
            address = "Shibuya 123",
            password = "123456sD!",
            cvu = "0011223344556677889911",
            walletAddress = "12345678",
        )
        val parsedUserData = ObjectMapper().writeValueAsString(userData)

        mockMvc.perform(
            MockMvcRequestBuilders.post("/users/register")
            .contentType(MediaType.APPLICATION_JSON)
            .content(parsedUserData)
        )
            .andExpect(MockMvcResultMatchers.status().isCreated)
            .andExpect(MockMvcResultMatchers.content().string("Welcome Satoshi Nakamoto! You've been successfully registered."))
    }

    @Test
    fun userRegistrationReturns400WithInvalidData() {
        val userData = UserDTO(
            name = "S",
            surname = "Nakamoto",
            email = "satonaka@gmail.com",
            address = "Shibuya 123",
            password = "123456sD!",
            cvu = "0011223344556677889911",
            walletAddress = "12345678",
        )
        val parsedUserData = ObjectMapper().writeValueAsString(userData)

        mockMvc.perform(
            MockMvcRequestBuilders.post("/users/register")
            .contentType(MediaType.APPLICATION_JSON)
            .content(parsedUserData)
        )
            .andExpect(MockMvcResultMatchers.status().isBadRequest)
    }

    @AfterEach
    fun cleanUp() {
        // TODO: no tendríamos que necesitar esto ya que deberíamos mockear realmente la respuesta.
        userService.deleteAll()
    }
}