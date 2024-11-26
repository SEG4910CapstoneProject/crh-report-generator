package me.t65.reportgenerator.steps;

import static org.mockito.Mockito.*;

import me.t65.reportgenerator.common.UtilityService;
import me.t65.reportgenerator.config.ReportGeneratorConfig;
import me.t65.reportgenerator.config.RetryConfig;
import me.t65.reportgenerator.db.DbService;
import me.t65.reportgenerator.db.postgres.entities.ArticlesEntity;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import reactor.core.scheduler.Scheduler;
import reactor.core.scheduler.Schedulers;
import reactor.test.StepVerifier;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@ExtendWith(MockitoExtension.class)
public class ArticleSelectionStepTests {

    @Mock private DbService dbService;

    @Mock private ReportGeneratorConfig reportGeneratorConfig;

    @Mock private UtilityService utilityService;

    Scheduler scheduler = Schedulers.immediate();

    ArticleSelectionStep articleSelectionStep;

    @BeforeEach
    public void beforeEach() {
        articleSelectionStep =
                new ArticleSelectionStep(
                        dbService, scheduler, reportGeneratorConfig, utilityService);
    }

    @AfterEach
    public void afterEach() {
        articleSelectionStep = null;
    }

    @Test
    public void testRun_success() {
        List<ArticlesEntity> expectedArticles = generate15ArticleEntities();
        int expectedReportId = 2;

        when(utilityService.currentInstant()).thenReturn(Instant.ofEpochMilli(10000));
        when(dbService.getArticlesAfterDate(any(), anyInt())).thenReturn(expectedArticles);
        when(reportGeneratorConfig.getDbRetry()).thenReturn(new RetryConfig(0, 0));

        StepVerifier.create(
                        articleSelectionStep
                                .run(expectedReportId)
                                .filter(a -> false) // Remove all items
                        )
                .verifyComplete();

        verify(dbService, times(1)).getArticlesAfterDate(any(), anyInt());
        for (ArticlesEntity entity : expectedArticles) {
            verify(dbService, times(1))
                    .addArticleToReport(eq(entity), eq(expectedReportId), anyShort(), anyBoolean());
        }
    }

    @Test
    public void testRun_dbReadFailOnce_retry() {
        List<ArticlesEntity> expectedArticles = generate15ArticleEntities();
        int expectedReportId = 2;

        when(utilityService.currentInstant()).thenReturn(Instant.ofEpochMilli(10000));
        when(dbService.getArticlesAfterDate(any(), anyInt()))
                .thenThrow(new RuntimeException("Test Exception"))
                .thenReturn(expectedArticles);
        when(reportGeneratorConfig.getDbRetry()).thenReturn(new RetryConfig(1, 0));

        StepVerifier.create(
                        articleSelectionStep
                                .run(expectedReportId)
                                .filter(a -> false) // Remove all items
                        )
                .verifyComplete();

        verify(dbService, times(2)).getArticlesAfterDate(any(), anyInt());
        for (ArticlesEntity entity : expectedArticles) {
            verify(dbService, times(1))
                    .addArticleToReport(eq(entity), eq(expectedReportId), anyShort(), anyBoolean());
        }
    }

    @Test
    public void testRun_dbReadFailMany_retriesExhausted() {
        int expectedReportId = 2;

        when(utilityService.currentInstant()).thenReturn(Instant.ofEpochMilli(10000));
        when(dbService.getArticlesAfterDate(any(), anyInt()))
                .thenThrow(new RuntimeException("Test Exception"));
        when(reportGeneratorConfig.getDbRetry()).thenReturn(new RetryConfig(1, 0));

        StepVerifier.create(
                        articleSelectionStep
                                .run(expectedReportId)
                                .filter(a -> false) // Remove all items
                        )
                .verifyError();

        verify(dbService, times(2)).getArticlesAfterDate(any(), anyInt());
        verify(dbService, never()).addArticleToReport(any(), anyInt(), anyShort(), anyBoolean());
    }

    @Test
    public void testRun_dbWriteFailOnce_retry() {
        List<ArticlesEntity> expectedArticles = generate15ArticleEntities();
        int expectedReportId = 2;

        when(utilityService.currentInstant()).thenReturn(Instant.ofEpochMilli(10000));
        when(dbService.getArticlesAfterDate(any(), anyInt())).thenReturn(expectedArticles);
        when(reportGeneratorConfig.getDbRetry()).thenReturn(new RetryConfig(1, 0));
        doThrow(new RuntimeException("Test Exception"))
                .doNothing()
                .when(dbService)
                .addArticleToReport(any(), anyInt(), anyShort(), anyBoolean());

        StepVerifier.create(
                        articleSelectionStep
                                .run(expectedReportId)
                                .filter(a -> false) // Remove all items
                        )
                .verifyComplete();

        verify(dbService, times(1)).getArticlesAfterDate(any(), anyInt());
        verify(dbService, atLeast(16))
                .addArticleToReport(any(), anyInt(), anyShort(), anyBoolean());
    }

    @Test
    public void testRun_dbWriteFailMany_retriesExhausted() {
        List<ArticlesEntity> expectedArticles = generate15ArticleEntities();
        int expectedReportId = 2;

        when(utilityService.currentInstant()).thenReturn(Instant.ofEpochMilli(10000));
        when(dbService.getArticlesAfterDate(any(), anyInt())).thenReturn(expectedArticles);
        when(reportGeneratorConfig.getDbRetry()).thenReturn(new RetryConfig(3, 0));
        doThrow(new RuntimeException("Test Exception"))
                .when(dbService)
                .addArticleToReport(any(), anyInt(), anyShort(), anyBoolean());

        StepVerifier.create(
                        articleSelectionStep
                                .run(expectedReportId)
                                .filter(a -> false) // Remove all items
                        )
                .verifyError();

        verify(dbService, times(1)).getArticlesAfterDate(any(), anyInt());
        verify(dbService, atLeast(4)).addArticleToReport(any(), anyInt(), anyShort(), anyBoolean());
    }

    public List<ArticlesEntity> generate15ArticleEntities() {
        ArrayList<ArticlesEntity> result = new ArrayList<ArticlesEntity>();

        for (int x = 0; x < 15; x++) {
            result.add(
                    new ArticlesEntity(
                            UUID.randomUUID(),
                            1,
                            Instant.ofEpochMilli(1000),
                            Instant.ofEpochMilli(1000),
                            true,
                            true,
                            123));
        }

        return result;
    }
}
