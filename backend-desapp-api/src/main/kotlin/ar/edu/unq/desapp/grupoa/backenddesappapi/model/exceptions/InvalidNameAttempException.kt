package ar.edu.unq.desapp.grupoa.backenddesappapi.model.exceptions

class InvalidNameAttempException: Throwable() {
    override val message: String
        get() = "Name and surname should have at least 3 characters and a maximum of 30 characters."
}