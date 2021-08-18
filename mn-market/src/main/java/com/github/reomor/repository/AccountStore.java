package com.github.reomor.repository;

import com.github.reomor.domain.WatchList;

import java.util.UUID;

public interface AccountStore {

    WatchList getWatchList(UUID accountId);

    WatchList updateWatchList(UUID accountId, WatchList watchList);

    WatchList deleteWatchList(UUID accountId);
}
