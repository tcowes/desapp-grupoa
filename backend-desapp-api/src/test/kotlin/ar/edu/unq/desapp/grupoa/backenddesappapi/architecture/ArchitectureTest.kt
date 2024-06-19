package ar.edu.unq.desapp.grupoa.backenddesappapi.architecture

import com.tngtech.archunit.core.domain.JavaClasses
import com.tngtech.archunit.core.importer.ClassFileImporter
import com.tngtech.archunit.core.importer.ImportOption
import com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes
import com.tngtech.archunit.library.Architectures.layeredArchitecture
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.stereotype.Service
import org.springframework.web.bind.annotation.RestController

@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class ArchitectureTest {

    private lateinit var allClasses: JavaClasses
    private lateinit var productiveClasses: JavaClasses

    @BeforeEach
    fun setUp() {
        val basePackage = "ar.edu.unq.desapp.grupoa.backenddesappapi"
        allClasses = ClassFileImporter().importPackages(basePackage)
        productiveClasses = ClassFileImporter()
            .withImportOption(ImportOption.Predefined.DO_NOT_INCLUDE_TESTS).importPackages(basePackage)
        
    }

    @Test
    fun packageNamesShouldFollowConvention() {
        classes().that().resideInAnyPackage("ar.edu.unq.desapp.grupoa.backenddesappapi..")
            .and().haveSimpleNameNotStartingWith("BackendDesappApiApplication")
            .and().haveSimpleNameNotStartingWith("ServletInitializer")
            .should().resideInAnyPackage(
                "ar.edu.unq.desapp.grupoa.backenddesappapi.model..",
                "ar.edu.unq.desapp.grupoa.backenddesappapi.service..",
                "ar.edu.unq.desapp.grupoa.backenddesappapi.persistence..",
                "ar.edu.unq.desapp.grupoa.backenddesappapi.webservice..",
            ).check(productiveClasses)
    }

    @Test
    fun classesWithServiceAnnotationShouldEndWithImpl() {
        classes().that().resideInAPackage("..service..")
            .and().areAnnotatedWith(Service::class.java)
            .should().haveSimpleNameEndingWith("Impl")
            .check(allClasses)
    }

    @Test
    fun controllersShouldBeAnnotatedWithRestController() {
        classes().that().resideInAPackage("..webservice")
            .should().beAnnotatedWith(RestController::class.java).check(productiveClasses)
    }

    @Test
    fun repositoryClassesShouldBeInterfaces() {
        classes().that().resideInAPackage("..persistence..")
            .should().beInterfaces().check(productiveClasses)
    }

    @Test
    fun exceptionsClassesShouldEndWithException() {
        classes().that().resideInAPackage("..exceptions")
            .should().haveSimpleNameEndingWith("Exception").check(productiveClasses)
    }

    @Test
    fun persistenceClassesShouldEndWithRepo() {
        classes().that().resideInAPackage("..persistence..")
            .should().haveSimpleNameEndingWith("Repository").check(productiveClasses)
    }

    @Test
    fun layeredArchitectureShouldBeRespected() {
        layeredArchitecture()
            .consideringAllDependencies()
            .layer("Model").definedBy("..model..")
            .layer("Controller").definedBy("..webservice..")
            .layer("Service").definedBy("..service..")
            .layer("Persistence").definedBy("..persistence..")

            .whereLayer("Model").mayOnlyBeAccessedByLayers("Controller", "Service", "Persistence")
            .whereLayer("Controller").mayNotBeAccessedByAnyLayer()
            .whereLayer("Service").mayOnlyBeAccessedByLayers("Controller")
            .whereLayer("Persistence").mayOnlyBeAccessedByLayers("Service")
    }
}