package ar.edu.unq.desapp.grupoa.backenddesappapi.model

import jakarta.persistence.*

@Entity
class Cryptocurrency (
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id : Long? = null,
    val name: String,
    val symbol: String,
    val lastQuotation: Double
)