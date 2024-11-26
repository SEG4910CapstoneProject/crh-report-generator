package me.t65.reportgenerator.steps;

import static org.mockito.Mockito.*;

import me.t65.reportgenerator.common.UtilityService;
import me.t65.reportgenerator.config.ReportGeneratorConfig;
import me.t65.reportgenerator.config.RetryConfig;
import me.t65.reportgenerator.db.DbService;
import me.t65.reportgenerator.db.postgres.ReportTypeEnum;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import reactor.test.StepVerifier;

import java.time.Instant;

@ExtendWith(MockitoExtension.class)
public class BasicReportInfoStepTests {

    @Mock private DbService dbService;

    @Mock private ReportGeneratorConfig reportGeneratorConfig;

    @Mock private UtilityService utilityService;

    @InjectMocks private BasicReportInfoStep basicReportInfoStep;

    @Test
    public void testGenerateBasicReport_success() {
        Instant expectedDate = Instant.ofEpochMilli(10000);
        int expectedReportId = 2;
        ReportTypeEnum expectedType = ReportTypeEnum.daily;

        when(utilityService.currentDate()).thenReturn(expectedDate);
        when(dbService.saveNewReportInformation(eq(expectedDate), eq(expectedType)))
                .thenReturn(expectedReportId);
        when(reportGeneratorConfig.getType()).thenReturn(expectedType);
        when(reportGeneratorConfig.getDbRetry()).thenReturn(new RetryConfig(0, 0));

        StepVerifier.create(basicReportInfoStep.generateBasicReport())
                .expectNext(expectedReportId)
                .verifyComplete();

        verify(dbService, times(1)).saveNewReportInformation(eq(expectedDate), eq(expectedType));
    }

    @Test
    public void testGenerateBasicReport_dbErrorOnce_retries() {
        Instant expectedDate = Instant.ofEpochMilli(10000);
        int expectedReportId = 2;
        ReportTypeEnum expectedType = ReportTypeEnum.daily;

        when(utilityService.currentDate()).thenReturn(expectedDate);
        when(dbService.saveNewReportInformation(eq(expectedDate), eq(expectedType)))
                .thenThrow(new RuntimeException("Test Exception"))
                .thenReturn(expectedReportId);
        when(reportGeneratorConfig.getType()).thenReturn(expectedType);
        when(reportGeneratorConfig.getDbRetry()).thenReturn(new RetryConfig(3, 0));

        StepVerifier.create(basicReportInfoStep.generateBasicReport())
                .expectNext(expectedReportId)
                .verifyComplete();

        verify(dbService, times(2)).saveNewReportInformation(eq(expectedDate), eq(expectedType));
    }

    @Test
    public void testGenerateBasicReport_dbErrorMany_RetryExhausted() {
        Instant expectedDate = Instant.ofEpochMilli(10000);
        ReportTypeEnum expectedType = ReportTypeEnum.daily;

        when(utilityService.currentDate()).thenReturn(expectedDate);
        when(dbService.saveNewReportInformation(eq(expectedDate), eq(expectedType)))
                .thenThrow(new RuntimeException("Test Exception"));
        when(reportGeneratorConfig.getType()).thenReturn(expectedType);
        when(reportGeneratorConfig.getDbRetry()).thenReturn(new RetryConfig(3, 0));

        StepVerifier.create(basicReportInfoStep.generateBasicReport()).verifyError();

        verify(dbService, times(4)).saveNewReportInformation(eq(expectedDate), eq(expectedType));
    }
}
