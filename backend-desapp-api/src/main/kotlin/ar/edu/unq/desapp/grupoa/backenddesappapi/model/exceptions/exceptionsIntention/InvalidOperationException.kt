package ar.edu.unq.desapp.grupoa.backenddesappapi.model.exceptions.exceptionsIntention
class InvalidOperationException: Throwable() {
    override val message: String
        get() = "Invalid operation. Please review and make sure it is spelled correctly."
}