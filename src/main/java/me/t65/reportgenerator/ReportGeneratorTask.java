package me.t65.reportgenerator;

import me.t65.reportgenerator.steps.BasicReportInfoStep;
import me.t65.reportgenerator.steps.GeneratorStep;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Scheduler;

import java.util.List;

@Component
public class ReportGeneratorTask implements ApplicationRunner {

    private static final Logger LOGGER = LoggerFactory.getLogger(ReportGeneratorTask.class);

    private final BasicReportInfoStep basicReportInfoStep;
    private final List<GeneratorStep> generatorSteps;
    private final Scheduler scheduler;

    @Autowired
    public ReportGeneratorTask(
            BasicReportInfoStep basicReportInfoStep,
            List<GeneratorStep> generatorSteps,
            Scheduler scheduler) {
        this.basicReportInfoStep = basicReportInfoStep;
        this.generatorSteps = generatorSteps;
        this.scheduler = scheduler;
    }

    @Override
    public void run(ApplicationArguments args) {
        basicReportInfoStep
                .generateBasicReport()
                .doOnNext(reportId -> LOGGER.info("Generating report with Id {}", reportId))
                // Execute generation steps
                .flatMap(this::executeStep)
                // Completed generation steps
                .doOnNext(
                        reportId -> LOGGER.info("Completed generating report with Id {}", reportId))
                .doOnError(
                        throwable ->
                                LOGGER.error(
                                        "An error occurred while generating report", throwable))
                .subscribeOn(scheduler)
                .blockLast();
    }

    private Mono<Integer> executeStep(int reportId) {
        return Flux.fromIterable(generatorSteps)
                .flatMap(step -> step.run(reportId))
                .then(Mono.just(reportId))
                .doOnError(
                        throwable ->
                                LOGGER.error(
                                        "An error occurred while generating report", throwable))
                .onErrorComplete();
    }
}
