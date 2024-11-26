package me.t65.reportgenerator.steps;

import me.t65.reportgenerator.common.StreamUtils;
import me.t65.reportgenerator.common.UtilityService;
import me.t65.reportgenerator.config.ReportGeneratorConfig;
import me.t65.reportgenerator.db.DbService;
import me.t65.reportgenerator.db.postgres.entities.ArticlesEntity;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Scheduler;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

@Service
public class ArticleSelectionStep implements GeneratorStep {
    private static Logger LOGGER = LoggerFactory.getLogger(ArticleSelectionStep.class);

    private final DbService dbService;
    private final Scheduler scheduler;
    private final ReportGeneratorConfig reportGeneratorConfig;
    private final UtilityService utilityService;

    @Autowired
    public ArticleSelectionStep(
            DbService dbService,
            Scheduler scheduler,
            ReportGeneratorConfig reportGeneratorConfig,
            UtilityService utilityService) {
        this.dbService = dbService;
        this.scheduler = scheduler;
        this.reportGeneratorConfig = reportGeneratorConfig;
        this.utilityService = utilityService;
    }

    @Override
    public Flux<?> run(int reportId) {
        Instant yesterdayInstant = utilityService.currentInstant().minus(24, ChronoUnit.HOURS);
        AtomicInteger rankCounter = new AtomicInteger();

        return Flux.just(yesterdayInstant)
                .doOnNext(d -> LOGGER.info("Starting Article Selection Step"))
                // get articles
                .flatMap(this::getSuggestedArticlesAsStream)
                .flatMap(Flux::fromIterable)
                // Select and rank articles
                .map(
                        articlesEntity ->
                                new SelectedArticle(
                                        articlesEntity, (short) rankCounter.getAndIncrement()))
                // Save selections
                .flatMap(
                        selectedArticle ->
                                addArticleToReportAsStream(selectedArticle, reportId, true))
                .doOnComplete(() -> LOGGER.info("Completed Article Selection Step"))
                .subscribeOn(scheduler);
    }

    private Flux<List<ArticlesEntity>> getSuggestedArticlesAsStream(Instant dateToSearch) {
        return Flux.just(dateToSearch)
                .map(
                        searchDate ->
                                dbService.getArticlesAfterDate(
                                        searchDate,
                                        reportGeneratorConfig.getMaxSuggestedArticles()))
                .transform(
                        stream ->
                                StreamUtils.doRetries(
                                        stream,
                                        reportGeneratorConfig.getDbRetry(),
                                        err -> LOGGER.error("Failed to get articles from db", err),
                                        retrySignal ->
                                                LOGGER.warn(
                                                        "Retrying to get articles from db. Retry:"
                                                                + " {} Backoff: {}ms",
                                                        retrySignal.totalRetriesInARow(),
                                                        reportGeneratorConfig
                                                                .getDbRetry()
                                                                .getRetryBackoffMillis()),
                                        retrySignal ->
                                                LOGGER.error(
                                                        "Retries exhausted. Failed to read from db."
                                                                + " {} retries attempted.",
                                                        retrySignal.totalRetriesInARow())))
                .subscribeOn(scheduler);
    }

    private Mono<Void> addArticleToReportAsStream(
            SelectedArticle selectedArticle, int reportId, boolean isSuggestion) {
        return Flux.just(0)
                .doOnNext(
                        a ->
                                dbService.addArticleToReport(
                                        selectedArticle.articlesEntity,
                                        reportId,
                                        selectedArticle.rank,
                                        isSuggestion))
                .transform(
                        stream ->
                                StreamUtils.doRetries(
                                        stream,
                                        reportGeneratorConfig.getDbRetry(),
                                        err ->
                                                LOGGER.error(
                                                        "Failed to add article to report in db",
                                                        err),
                                        retrySignal ->
                                                LOGGER.warn(
                                                        "Retrying to save article to report. Retry:"
                                                                + " {} Backoff: {}ms",
                                                        retrySignal.totalRetriesInARow(),
                                                        reportGeneratorConfig
                                                                .getDbRetry()
                                                                .getRetryBackoffMillis()),
                                        retrySignal ->
                                                LOGGER.error(
                                                        "Retries exhausted. Failed to write to db."
                                                                + " {} retries attempted.",
                                                        retrySignal.totalRetriesInARow())))
                .subscribeOn(scheduler)
                .then();
    }

    @lombok.Getter
    @lombok.AllArgsConstructor
    private static class SelectedArticle {
        private ArticlesEntity articlesEntity;
        private short rank;
    }
}
