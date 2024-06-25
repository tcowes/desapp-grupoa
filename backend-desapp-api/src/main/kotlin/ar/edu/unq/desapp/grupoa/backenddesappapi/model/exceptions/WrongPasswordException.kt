package ar.edu.unq.desapp.grupoa.backenddesappapi.model.exceptions

class WrongPasswordException: Throwable() {
    override val message: String
        get() = "Couldn't log-in, incorrect password!"
}