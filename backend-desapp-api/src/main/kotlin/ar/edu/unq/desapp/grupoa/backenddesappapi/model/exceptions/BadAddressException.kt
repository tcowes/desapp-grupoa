package ar.edu.unq.desapp.grupoa.backenddesappapi.model.exceptions

class BadAddressException: Throwable() {
    override val message: String
        get() = "Address should have at least 10 characters and a maximum of 30 characters."
}