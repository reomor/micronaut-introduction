package com.example.repository.impl;

import com.example.domain.WatchList;
import com.example.repository.AccountStore;

import javax.inject.Singleton;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Singleton
public class InMemoryAccountStore implements AccountStore {

    private final Map<UUID, WatchList> watchListMap = new ConcurrentHashMap<>();

    @Override
    public WatchList getWatchList(UUID accountId) {
        return watchListMap.getOrDefault(accountId, WatchList.EMPTY);
    }

    @Override
    public WatchList updateWatchList(UUID accountId, WatchList watchList) {
        watchListMap.put(accountId, watchList);
        return getWatchList(accountId);
    }

    @Override
    public WatchList deleteWatchList(UUID accountId) {
        return watchListMap.remove(accountId);
    }
}
