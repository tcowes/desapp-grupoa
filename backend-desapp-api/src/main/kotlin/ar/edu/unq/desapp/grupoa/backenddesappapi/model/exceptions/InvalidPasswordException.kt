package ar.edu.unq.desapp.grupoa.backenddesappapi.model.exceptions

class InvalidPasswordException: Throwable() {
    override val message: String
        get() = "The password must have at least 6 characters, including at least one special character, one uppercase letter, and one lowercase letter."
}