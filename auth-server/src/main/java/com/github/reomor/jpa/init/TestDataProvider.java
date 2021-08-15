package com.github.reomor.jpa.init;

import com.github.reomor.jpa.entity.UserEntity;
import com.github.reomor.jpa.repository.UserRepository;
import io.micronaut.context.event.StartupEvent;
import io.micronaut.runtime.event.annotation.EventListener;

import javax.inject.Singleton;

@Singleton
public class TestDataProvider {

  private static final String ALICE_EMAIL = "alice@bob.com";
  private static final String ALICE_SECRET = "secret";

  private final UserRepository userRepository;

  public TestDataProvider(UserRepository userRepository) {
    this.userRepository = userRepository;
  }

  @EventListener
  public void init(StartupEvent event) {
    if (userRepository.findByEmail(ALICE_EMAIL).isEmpty()) {
      UserEntity userEntity = new UserEntity();
      userEntity.setEmail(ALICE_EMAIL);
      userEntity.setPassword(ALICE_SECRET);
      userRepository.save(userEntity);
    }
  }
}
