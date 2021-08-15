package com.github.reomor.jpa.repository;

import com.github.reomor.jpa.entity.SymbolEntity;
import io.micronaut.core.annotation.NonNull;
import io.micronaut.data.annotation.Repository;
import io.micronaut.data.repository.CrudRepository;

import java.util.List;

@Repository
public interface SymbolsRepository extends CrudRepository<SymbolEntity, String> {

    @NonNull
    @Override
    List<SymbolEntity> findAll();
}
