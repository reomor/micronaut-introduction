package com.github.reomor;

import io.micronaut.runtime.Micronaut;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;

@OpenAPIDefinition(
    info = @Info(
        title = "Micronaut Intro",
        version = "0.0.1",
        description = "My API"
    )
)
public class MainApplication {
    public static void main(String[] args) {
        Micronaut.run(MainApplication.class, args);
    }
}
