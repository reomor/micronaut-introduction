package com.github.reomor.jpa.repository;

import com.github.reomor.jpa.entity.UserEntity;
import io.micronaut.data.annotation.Repository;
import io.micronaut.data.repository.CrudRepository;

import java.util.Optional;

@Repository
public interface UserRepository extends CrudRepository<UserEntity, Integer> {
  Optional<UserEntity> findByEmail(String email);
}
