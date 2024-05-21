package ar.edu.unq.desapp.grupoa.backenddesappapi.model.exceptions.exceptionsIntention

class OutOfRangePriceException: Throwable() {
    override val message: String
        get() = "Price for the intention must be within five percent of the current price for the cryptocurrency."
}