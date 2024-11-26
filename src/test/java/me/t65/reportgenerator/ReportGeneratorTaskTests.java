package me.t65.reportgenerator;

import static org.mockito.Mockito.*;

import me.t65.reportgenerator.steps.BasicReportInfoStep;
import me.t65.reportgenerator.steps.GeneratorStep;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import reactor.core.publisher.Flux;
import reactor.core.scheduler.Scheduler;
import reactor.core.scheduler.Schedulers;

import java.util.List;

@ExtendWith(MockitoExtension.class)
public class ReportGeneratorTaskTests {

    @Mock BasicReportInfoStep basicReportInfoStep;

    @Mock GeneratorStep generatorStep;

    Scheduler scheduler = Schedulers.immediate();

    ReportGeneratorTask reportGeneratorTask;

    @BeforeEach
    public void beforeEach() {
        reportGeneratorTask =
                new ReportGeneratorTask(basicReportInfoStep, List.of(generatorStep), scheduler);
    }

    @AfterEach
    public void afterEach() {
        reportGeneratorTask = null;
    }

    @Test
    public void testRun_success() {
        int testReportId = 1;

        when(basicReportInfoStep.generateBasicReport()).thenReturn(Flux.just(testReportId));
        when(generatorStep.run(anyInt())).thenReturn(Flux.empty());

        reportGeneratorTask.run(null);

        verify(generatorStep, times(1)).run(testReportId);
    }
}
