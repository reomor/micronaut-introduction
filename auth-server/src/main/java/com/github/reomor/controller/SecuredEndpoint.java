package com.github.reomor.controller;

import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import io.micronaut.security.annotation.Secured;
import io.micronaut.security.authentication.Authentication;
import io.micronaut.security.rules.SecurityRule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.security.Principal;
import java.util.Arrays;
import java.util.List;

@Secured(SecurityRule.IS_AUTHENTICATED)
@Controller("/secured")
public class SecuredEndpoint {

  private static final Logger log = LoggerFactory.getLogger(SecuredEndpoint.class);

  @Get("/status")
  public List<Object> status(Principal principal) {

    Authentication authentication = (Authentication) principal;

    log.debug("Token attributes: {}", authentication.getAttributes());

    return Arrays.asList(
      principal.getName(),
      authentication.getAttributes().get("locale")
    );
  }
}
