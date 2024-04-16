package ar.edu.unq.desapp.grupoa.backenddesappapi.model.exceptions

class UserAlreadyRegisteredException(val attribute: String, val value: String): Throwable() {
    override val message: String
        get() = "There's a user with the $attribute $value already registered in the database."
}