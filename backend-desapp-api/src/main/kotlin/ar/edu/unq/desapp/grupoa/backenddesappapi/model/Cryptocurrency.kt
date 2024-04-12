package ar.edu.unq.desapp.grupoa.backenddesappapi.model

class Cryptocurrency (
    var id : Long? = null,
    val name: String,
    val symbol: String,
    val lastQuotation: Double
)