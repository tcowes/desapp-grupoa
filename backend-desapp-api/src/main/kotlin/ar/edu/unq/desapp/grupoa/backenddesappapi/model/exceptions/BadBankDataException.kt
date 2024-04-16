package ar.edu.unq.desapp.grupoa.backenddesappapi.model.exceptions

class BadBankDataException(val bankTitle: String, val expectedLength: Int) : Throwable() {
    override val message: String
        get() = "$bankTitle should have exactly $expectedLength characters."
}