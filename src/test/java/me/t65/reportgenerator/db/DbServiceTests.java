package me.t65.reportgenerator.db;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import me.t65.reportgenerator.db.postgres.ReportTypeEnum;
import me.t65.reportgenerator.db.postgres.entities.ArticlesEntity;
import me.t65.reportgenerator.db.postgres.entities.ReportArticlesEntity;
import me.t65.reportgenerator.db.postgres.entities.ReportEntity;
import me.t65.reportgenerator.db.postgres.entities.id.ReportArticlesId;
import me.t65.reportgenerator.db.postgres.repository.ArticlesRepository;
import me.t65.reportgenerator.db.postgres.repository.ReportArticlesEntityRepository;
import me.t65.reportgenerator.db.postgres.repository.ReportEntityRepository;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@ExtendWith(MockitoExtension.class)
public class DbServiceTests {

    private static final UUID uuid1 = UUID.fromString("8d00f987-20bc-4678-963a-01fd909153fc");

    @Mock private ArticlesRepository articlesRepository;
    @Mock private ReportArticlesEntityRepository reportArticlesEntityRepository;
    @Mock private ReportEntityRepository reportEntityRepository;
    @InjectMocks private DbService dbService;

    @Test
    public void testSaveNewReportInformation_success() {
        Instant expectedDate = Instant.ofEpochMilli(10000);
        ReportTypeEnum expectedReportType = ReportTypeEnum.daily;
        int expectedId = 2;
        ReportEntity expectedEntity =
                new ReportEntity(expectedId, expectedDate, expectedReportType, expectedDate);

        when(reportEntityRepository.save(any(ReportEntity.class))).thenReturn(expectedEntity);

        int actual = dbService.saveNewReportInformation(expectedDate, expectedReportType);

        assertEquals(expectedId, actual);
        verify(reportEntityRepository, times(1)).save(any(ReportEntity.class));
    }

    @Test
    public void testGetLatest15Articles_success() {
        Instant expectedCurrentDate = Instant.ofEpochMilli(10000);
        ArticlesEntity expectedArticle =
                new ArticlesEntity(
                        uuid1, 1, expectedCurrentDate, expectedCurrentDate, true, true, 123);

        Page<ArticlesEntity> pageMock = mock(Page.class);
        when(pageMock.getContent()).thenReturn(List.of(expectedArticle));
        when(articlesRepository.findByDateIngestedAfter(any(), any())).thenReturn(pageMock);

        List<ArticlesEntity> actual = dbService.getArticlesAfterDate(expectedCurrentDate, 15);

        assertEquals(expectedArticle, actual.get(0));
        verify(articlesRepository, times(1))
                .findByDateIngestedAfter(eq(expectedCurrentDate), any());
    }

    @Test
    public void testAddArticleToReport_success() {
        int expectedReportId = 2;
        short expectedRank = 5;
        ReportArticlesId expectedReportArticlesId = new ReportArticlesId(expectedReportId, uuid1);
        ReportArticlesEntity expectedReportArticlesEntity =
                new ReportArticlesEntity(expectedReportArticlesId, expectedRank, true);

        ArticlesEntity passedArticle =
                new ArticlesEntity(
                        uuid1,
                        1,
                        Instant.ofEpochMilli(1000),
                        Instant.ofEpochMilli(1000),
                        true,
                        true,
                        123);

        when(reportArticlesEntityRepository.save(any())).thenReturn(expectedReportArticlesEntity);

        // actual
        dbService.addArticleToReport(passedArticle, expectedReportId, expectedRank, true);

        verify(reportArticlesEntityRepository, times(1)).save(expectedReportArticlesEntity);
    }
}
