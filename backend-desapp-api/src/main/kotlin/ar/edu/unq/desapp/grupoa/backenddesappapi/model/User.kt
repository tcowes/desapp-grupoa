package ar.edu.unq.desapp.grupoa.backenddesappapi.model

import jakarta.persistence.*
//@Entity
//@Table(name = "Users")
class User (name: String, surname: String, email: String, address: String, password: String, cvu: String, walletAddress: String) {
    @Id
    //@GeneratedValue(strategy = GenerationType.IDENTITY)
    //@Column(unique = true, nullable = false)
    var id: Long? = null

    @Column
    var nameUser: String = name
    var surnameUser: String = surname
    var emailUser: String = email
    var addressUser: String = address
    var passwordUser: String = password
    var cvuUser: String = cvu
    var walletAddressUser = walletAddress

    @Column
    var reputation: Double = 0.0
    @Column
    var transactions: MutableList<Transaction> = mutableListOf()
}