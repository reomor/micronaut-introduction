package com.example.repository;

import com.example.domain.WatchList;

import java.util.UUID;

public interface AccountStore {

    WatchList getWatchList(UUID accountId);

    WatchList updateWatchList(UUID accountId, WatchList watchList);

    WatchList deleteWatchList(UUID accountId);
}
