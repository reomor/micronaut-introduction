package com.example.client;

import com.example.domain.WatchList;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.annotation.Body;
import io.micronaut.http.annotation.Delete;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.Header;
import io.micronaut.http.annotation.PathVariable;
import io.micronaut.http.annotation.Post;
import io.micronaut.http.annotation.Put;
import io.micronaut.http.client.annotation.Client;
import io.micronaut.security.authentication.UsernamePasswordCredentials;
import io.micronaut.security.token.jwt.render.BearerAccessRefreshToken;
import io.reactivex.Flowable;

import java.util.UUID;

@Client("/")
public interface JwtWatchListClient {

    @Post("/login")
    BearerAccessRefreshToken login(@Body UsernamePasswordCredentials credentials);

    @Get("/account/watchlist")
    Flowable<WatchList> retrieve(@Header String authorization);

    @Put("/account/watchlist")
    HttpResponse<WatchList> put(@Header String authorization, @Body WatchList watchList);

    @Delete("/account/watchlist/{accountId}")
    HttpResponse<WatchList> delete(@Header String authorization, @PathVariable UUID accountId);
}
