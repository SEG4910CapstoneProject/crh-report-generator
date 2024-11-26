package me.t65.reportgenerator.steps;

import me.t65.reportgenerator.common.StreamUtils;
import me.t65.reportgenerator.common.UtilityService;
import me.t65.reportgenerator.config.ReportGeneratorConfig;
import me.t65.reportgenerator.db.DbService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import reactor.core.publisher.Flux;

@Service
public class BasicReportInfoStep {
    private static final Logger LOGGER = LoggerFactory.getLogger(BasicReportInfoStep.class);

    private final DbService dbService;
    private final ReportGeneratorConfig reportGeneratorConfig;
    private final UtilityService utilityService;

    @Autowired
    public BasicReportInfoStep(
            DbService dbService,
            ReportGeneratorConfig reportGeneratorConfig,
            UtilityService utilityService) {
        this.dbService = dbService;
        this.reportGeneratorConfig = reportGeneratorConfig;
        this.utilityService = utilityService;
    }

    public Flux<Integer> generateBasicReport() {
        return Flux.just(utilityService.currentDate())
                .map(
                        curDate ->
                                dbService.saveNewReportInformation(
                                        curDate, reportGeneratorConfig.getType()))
                .transform(
                        stream ->
                                StreamUtils.doRetries(
                                        stream,
                                        reportGeneratorConfig.getDbRetry(),
                                        err ->
                                                LOGGER.error(
                                                        "Failed to save report information to db",
                                                        err),
                                        retrySignal ->
                                                LOGGER.warn(
                                                        "Retrying to save new report information."
                                                                + " Retry: {} Backoff: {}ms",
                                                        retrySignal.totalRetriesInARow(),
                                                        reportGeneratorConfig
                                                                .getDbRetry()
                                                                .getRetryBackoffMillis()),
                                        retrySignal ->
                                                LOGGER.error(
                                                        "Retries exhausted. Failed to read stream."
                                                                + " {} retries attempted.",
                                                        retrySignal.totalRetriesInARow())));
    }
}
