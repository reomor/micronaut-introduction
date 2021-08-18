package com.github.reomor.controller;

import com.github.reomor.domain.Greeting;
import com.github.reomor.service.HelloService;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import io.micronaut.security.annotation.Secured;
import io.micronaut.security.rules.SecurityRule;

@Secured(SecurityRule.IS_ANONYMOUS)
@Controller("/hello")
public class HelloController {

    private final HelloService helloService;

    public HelloController(HelloService helloService) {
        this.helloService = helloService;
    }

    @Get("/index")
    public String index() {
        return helloService.greeting();
    }

    @Get("/json")
    public Greeting json() {
        return new Greeting();
    }

    @Get("/exception")
    public void exception() {
        throw new RuntimeException("exception serialization example");
    }
}
