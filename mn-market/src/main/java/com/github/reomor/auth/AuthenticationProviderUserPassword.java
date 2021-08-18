package com.github.reomor.auth;

import io.micronaut.http.HttpRequest;
import io.micronaut.security.authentication.AuthenticationException;
import io.micronaut.security.authentication.AuthenticationFailed;
import io.micronaut.security.authentication.AuthenticationProvider;
import io.micronaut.security.authentication.AuthenticationRequest;
import io.micronaut.security.authentication.AuthenticationResponse;
import io.micronaut.security.authentication.UserDetails;
import io.reactivex.BackpressureStrategy;
import io.reactivex.Flowable;
import lombok.extern.slf4j.Slf4j;
import org.reactivestreams.Publisher;

import javax.inject.Singleton;
import java.util.ArrayList;

@Slf4j
@Singleton
public class AuthenticationProviderUserPassword implements AuthenticationProvider {
    @Override
    public Publisher<AuthenticationResponse> authenticate(
        HttpRequest<?> httpRequest,
        AuthenticationRequest<?, ?> authenticationRequest
    ) {
        return Flowable.create(emitter -> {
                Object identity = authenticationRequest.getIdentity();
                Object secret = authenticationRequest.getSecret();
                log.debug("User {} tries to authenticate by {}", identity, secret);

                if (identity.equals("myuser") && secret.equals("secret")) {
                    emitter.onNext(new UserDetails((String) identity, new ArrayList<>()));
                    emitter.onComplete();
                    return;
                }

                emitter.onError(new AuthenticationException(new AuthenticationFailed("Wrong name or password")));
            },
            BackpressureStrategy.ERROR
        );
    }
}
