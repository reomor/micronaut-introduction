package com.github.reomor.jpa.repository;

import com.github.reomor.domain.dto.QuoteDto;
import com.github.reomor.jpa.entity.QuotesEntity;
import com.github.reomor.jpa.entity.SymbolEntity;
import io.micronaut.core.annotation.NonNull;
import io.micronaut.data.annotation.Repository;
import io.micronaut.data.model.Pageable;
import io.micronaut.data.model.Slice;
import io.micronaut.data.repository.CrudRepository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Repository
public interface QuotesRepository extends CrudRepository<QuotesEntity, Integer> {

    @NonNull
    @Override
    List<QuotesEntity> findAll();

    Optional<QuotesEntity> findBySymbol(@NonNull SymbolEntity symbol);

    List<QuoteDto> listOrderByVolumeDesc();

    List<QuotesEntity> listOrderByVolumeAsc();

    List<QuoteDto> findAllByVolumeGreaterThanOrderByVolumeAsc(@NonNull BigDecimal volume);

    List<QuoteDto> findAllByVolumeGreaterThan(@NonNull BigDecimal volume, Pageable pageable);

    Slice<QuoteDto> list(Pageable pageable);
}
