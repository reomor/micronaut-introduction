package com.github.reomor.controller;

import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import io.micronaut.security.annotation.Secured;
import io.micronaut.security.rules.SecurityRule;

import java.security.Principal;

@Secured(SecurityRule.IS_AUTHENTICATED)
@Controller("/secured")
public class SecuredEndpoint {

  @Get("/status")
  public String status(Principal principal) {
    return "You are authenticated: " + principal.getName();
  }
}
