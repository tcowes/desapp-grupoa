package ar.edu.unq.desapp.grupoa.backenddesappapi.model.exceptions.exceptionsTransaction

class SameUserForTransactionException: Throwable() {
    override val message: String
        get() = "Is not allowed for a user that initiated an intention to begin a transaction with that intention."
}