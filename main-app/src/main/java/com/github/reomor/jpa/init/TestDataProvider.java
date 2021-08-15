package com.github.reomor.jpa.init;

import com.github.reomor.jpa.entity.SymbolEntity;
import com.github.reomor.jpa.repository.SymbolsRepository;
import io.micronaut.context.annotation.Requires;
import io.micronaut.context.env.Environment;
import io.micronaut.context.event.StartupEvent;
import io.micronaut.runtime.event.annotation.EventListener;

import javax.inject.Singleton;
import java.util.stream.Stream;

@Singleton
@Requires(notEnv = Environment.TEST)
public class TestDataProvider {

    private final SymbolsRepository symbolsRepository;

    public TestDataProvider(SymbolsRepository userRepository) {
        this.symbolsRepository = userRepository;
    }

    @EventListener
    public void init(StartupEvent event) {
        if (symbolsRepository.findAll().isEmpty()) {
            Stream.of("IBM", "AMZN", "INTEL")
              .map(SymbolEntity::new)
              .forEach(symbolsRepository::save);
        }
    }
}
