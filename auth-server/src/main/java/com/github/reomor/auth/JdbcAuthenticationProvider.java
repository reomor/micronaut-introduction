package com.github.reomor.auth;

import com.github.reomor.jpa.entity.UserEntity;
import com.github.reomor.jpa.repository.UserRepository;
import io.micronaut.core.annotation.Nullable;
import io.micronaut.http.HttpRequest;
import io.micronaut.security.authentication.AuthenticationException;
import io.micronaut.security.authentication.AuthenticationFailed;
import io.micronaut.security.authentication.AuthenticationProvider;
import io.micronaut.security.authentication.AuthenticationRequest;
import io.micronaut.security.authentication.AuthenticationResponse;
import io.micronaut.security.authentication.UserDetails;
import io.reactivex.BackpressureStrategy;
import io.reactivex.Flowable;
import org.reactivestreams.Publisher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Singleton;
import java.util.List;
import java.util.Map;

@Singleton
public class JdbcAuthenticationProvider implements AuthenticationProvider {

  private static final Logger log = LoggerFactory.getLogger(JdbcAuthenticationProvider.class);

  private final UserRepository userRepository;

  public JdbcAuthenticationProvider(UserRepository userRepository) {
    this.userRepository = userRepository;
  }

  @Override
  public Publisher<AuthenticationResponse> authenticate(
    @Nullable HttpRequest<?> httpRequest,
    AuthenticationRequest<?, ?> authenticationRequest
  ) {
    return Flowable.create(
      emitter -> {

        String identity = (String) authenticationRequest.getIdentity();
        String secret = (String) authenticationRequest.getSecret();
        log.debug("User '{}' tries to login ...", identity);

        userRepository.findByEmail(identity)
          .map(UserEntity::getPassword)
          .filter(password -> secret != null && secret.equals(password))
          .ifPresentOrElse(password -> {
              emitter.onNext(
                new UserDetails(
                  identity,
                  List.of("ROLE_USER", "ROLE_NONUSER"),
                  Map.of(
                    "locale", "en",
                    "sex", "M"
                  )
                )
              );
              emitter.onComplete();
            },
            () -> emitter.onError(
              new AuthenticationException(
                new AuthenticationFailed("Wrong username or password")
              )
            )
          );
      },
      BackpressureStrategy.ERROR
    );
  }
}
