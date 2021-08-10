package com.example.controller;

import com.example.auth.AuthenticationProviderUserPassword;
import com.example.domain.WatchList;
import com.example.repository.AccountStore;
import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.*;
import io.micronaut.scheduling.TaskExecutors;
import io.micronaut.scheduling.annotation.ExecuteOn;
import io.reactivex.Flowable;
import io.reactivex.Scheduler;
import io.reactivex.Single;
import io.reactivex.schedulers.Schedulers;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.inject.Named;
import java.util.UUID;
import java.util.concurrent.ExecutorService;

//@Secured(SecurityRule.IS_AUTHENTICATED)
@Controller("/account/watchlist-reactive")
public class WatchListReactiveController {

  private static final Logger log = LogManager.getLogger(AuthenticationProviderUserPassword.class);

  static final UUID ACCOUNT_ID = UUID.randomUUID();

  private final Scheduler scheduler;
  private final AccountStore accountStore;

  public WatchListReactiveController(
    @Named(TaskExecutors.IO) ExecutorService executorService,
    AccountStore accountStore
  ) {
    this.accountStore = accountStore;
    this.scheduler = Schedulers.from(executorService);
  }

  @Get(
    produces = MediaType.APPLICATION_JSON
  )
  @ExecuteOn(TaskExecutors.IO)
  public WatchList get() {
    log.info("Got request - {}", Thread.currentThread().getName());
    return accountStore.getWatchList(ACCOUNT_ID);
  }

  @Get(
    value = "/single",
    produces = MediaType.APPLICATION_JSON
  )
  public Single<WatchList> getSingle() {
    log.info("Got request - {}", Thread.currentThread().getName());
    return Single.fromCallable(() -> accountStore.getWatchList(ACCOUNT_ID))
//            .toFlowable()
      .subscribeOn(scheduler);
  }

  @Put(
    consumes = MediaType.APPLICATION_JSON,
    produces = MediaType.APPLICATION_JSON
  )
  public Flowable<WatchList> update(@Body WatchList watchList) {
    log.info("Got request - {}", Thread.currentThread().getName());
    return Flowable.fromArray(accountStore.updateWatchList(ACCOUNT_ID, watchList));
  }

  @Delete(
    value = "/{accountId}",
    consumes = MediaType.APPLICATION_JSON,
    produces = MediaType.APPLICATION_JSON
  )
  public Single<WatchList> delete(@PathVariable UUID accountId) {
    log.info("Got request - {}", Thread.currentThread().getName());
    return Single.fromCallable(() -> accountStore.deleteWatchList(accountId))
      .subscribeOn(scheduler);
  }
}
