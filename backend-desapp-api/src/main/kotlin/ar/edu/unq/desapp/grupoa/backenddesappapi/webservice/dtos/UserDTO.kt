package ar.edu.unq.desapp.grupoa.backenddesappapi.webservice.dtos

import ar.edu.unq.desapp.grupoa.backenddesappapi.model.User

data class UserDTO(
    val name: String,
    val email: String,
    val password: String,
    val surname: String,
    val address: String,
    val cvu: String,
    val walletAddress: String
) {
    fun toModel(): User {
        return User(this.name, this.password, this.email, this.surname, this.address, this.cvu, this.walletAddress)
    }
}