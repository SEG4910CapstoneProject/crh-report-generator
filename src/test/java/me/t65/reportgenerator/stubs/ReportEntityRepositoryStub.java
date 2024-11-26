package me.t65.reportgenerator.stubs;

import me.t65.reportgenerator.db.postgres.ReportTypeEnum;
import me.t65.reportgenerator.db.postgres.entities.ReportEntity;
import me.t65.reportgenerator.db.postgres.repository.ReportEntityRepository;

import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.repository.query.FluentQuery;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

@Component
public class ReportEntityRepositoryStub implements ReportEntityRepository {
    @Override
    public void flush() {}

    @Override
    public <S extends ReportEntity> S saveAndFlush(S entity) {
        return null;
    }

    @Override
    public <S extends ReportEntity> List<S> saveAllAndFlush(Iterable<S> entities) {
        return null;
    }

    @Override
    public void deleteAllInBatch(Iterable<ReportEntity> entities) {}

    @Override
    public void deleteAllByIdInBatch(Iterable<Integer> integers) {}

    @Override
    public void deleteAllInBatch() {}

    @Override
    public ReportEntity getOne(Integer integer) {
        return null;
    }

    @Override
    public ReportEntity getById(Integer integer) {
        return null;
    }

    @Override
    public ReportEntity getReferenceById(Integer integer) {
        return null;
    }

    @Override
    public <S extends ReportEntity> Optional<S> findOne(Example<S> example) {
        return Optional.empty();
    }

    @Override
    public <S extends ReportEntity> List<S> findAll(Example<S> example) {
        return null;
    }

    @Override
    public <S extends ReportEntity> List<S> findAll(Example<S> example, Sort sort) {
        return null;
    }

    @Override
    public <S extends ReportEntity> Page<S> findAll(Example<S> example, Pageable pageable) {
        return null;
    }

    @Override
    public <S extends ReportEntity> long count(Example<S> example) {
        return 0;
    }

    @Override
    public <S extends ReportEntity> boolean exists(Example<S> example) {
        return false;
    }

    @Override
    public <S extends ReportEntity, R> R findBy(
            Example<S> example, Function<FluentQuery.FetchableFluentQuery<S>, R> queryFunction) {
        return null;
    }

    @Override
    public <S extends ReportEntity> S save(S entity) {
        return (S)
                new ReportEntity(
                        -1,
                        Instant.ofEpochMilli(10000),
                        ReportTypeEnum.daily,
                        Instant.ofEpochMilli(10000));
    }

    @Override
    public <S extends ReportEntity> List<S> saveAll(Iterable<S> entities) {
        return null;
    }

    @Override
    public Optional<ReportEntity> findById(Integer integer) {
        return Optional.empty();
    }

    @Override
    public boolean existsById(Integer integer) {
        return false;
    }

    @Override
    public List<ReportEntity> findAll() {
        return null;
    }

    @Override
    public List<ReportEntity> findAllById(Iterable<Integer> integers) {
        return null;
    }

    @Override
    public long count() {
        return 0;
    }

    @Override
    public void deleteById(Integer integer) {}

    @Override
    public void delete(ReportEntity entity) {}

    @Override
    public void deleteAllById(Iterable<? extends Integer> integers) {}

    @Override
    public void deleteAll(Iterable<? extends ReportEntity> entities) {}

    @Override
    public void deleteAll() {}

    @Override
    public List<ReportEntity> findAll(Sort sort) {
        return null;
    }

    @Override
    public Page<ReportEntity> findAll(Pageable pageable) {
        return null;
    }
}
