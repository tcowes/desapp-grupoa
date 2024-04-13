package ar.edu.unq.desapp.grupoa.backenddesappapi.model

import jakarta.persistence.*


@Entity
class Intention(
    @Column(nullable = false)
    val name: String,

    @Column(nullable = false)
    val symbol: String,

    @Column(nullable = false)
    val lastQuotation: Double
) {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null
}