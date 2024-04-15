package ar.edu.unq.desapp.grupoa.backenddesappapi.controller.DTO

data class UserDTO(
    val name: String,
    val email: String,
    val password: String,
    val surname: String,
    val address: String,
    val cvu: String,
    val walletAddress: String
)