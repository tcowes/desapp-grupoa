package ar.edu.unq.desapp.grupoa.backenddesappapi.model.exceptions.exceptionsIntention
class InvalidCryptoactiveException: Throwable() {
    override val message: String
        get() = "Invalid cryptoasset. Please review and make sure it is spelled correctly."
}