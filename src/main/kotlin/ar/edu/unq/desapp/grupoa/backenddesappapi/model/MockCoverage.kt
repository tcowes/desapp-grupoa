package ar.edu.unq.desapp.grupoa.backenddesappapi.model

class MockCoverage {
    fun unoODos(input: Boolean): Int {
        return if (input) {
            1
        } else {
            2
        }
    }
}