package ar.edu.unq.desapp.grupoa.backenddesappapi.controller

import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController


@RestController
@Tag(name = "Hello World", description = "Endpoint to test that Spring and Swagger were configured correctly")
class HelloController {

    @GetMapping("/")
    fun index(): String {
        return "Greetings from Spring Boot!"
    }

}