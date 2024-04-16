package ar.edu.unq.desapp.grupoa.backenddesappapi.model.exceptions

class InvalidEmailException: Throwable() {
    override val message: String
        get() = "Not valid email format."
}