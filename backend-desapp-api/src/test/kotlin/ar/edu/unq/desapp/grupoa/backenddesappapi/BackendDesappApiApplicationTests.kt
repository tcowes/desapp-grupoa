package ar.edu.unq.desapp.grupoa.backenddesappapi

import ar.edu.unq.desapp.grupoa.backenddesappapi.model.MockCoverage
import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
class BackendDesappApiApplicationTests {

	@Test
	fun mockCoverageUno() {
		assert(MockCoverage().unoODos(true) == 1)
	}

	@Test
	fun mockCoverageDos() {
		assert(MockCoverage().unoODos(false) == 2)
	}

}
