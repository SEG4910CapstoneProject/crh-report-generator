package me.t65.reportgenerator.db;

import me.t65.reportgenerator.db.postgres.ReportTypeEnum;
import me.t65.reportgenerator.db.postgres.entities.ArticlesEntity;
import me.t65.reportgenerator.db.postgres.entities.ReportArticlesEntity;
import me.t65.reportgenerator.db.postgres.entities.ReportEntity;
import me.t65.reportgenerator.db.postgres.entities.id.ReportArticlesId;
import me.t65.reportgenerator.db.postgres.repository.ArticlesRepository;
import me.t65.reportgenerator.db.postgres.repository.ReportArticlesEntityRepository;
import me.t65.reportgenerator.db.postgres.repository.ReportEntityRepository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;

@Service
public class DbService {
    private static final Logger LOGGER = LoggerFactory.getLogger(DbService.class);

    private final ArticlesRepository articlesRepository;
    private final ReportArticlesEntityRepository reportArticlesEntityRepository;
    private final ReportEntityRepository reportEntityRepository;

    @Autowired
    public DbService(
            ArticlesRepository articlesRepository,
            ReportArticlesEntityRepository reportArticlesEntityRepository,
            ReportEntityRepository reportEntityRepository) {
        this.articlesRepository = articlesRepository;
        this.reportArticlesEntityRepository = reportArticlesEntityRepository;
        this.reportEntityRepository = reportEntityRepository;
    }

    public int saveNewReportInformation(Instant generateDate, ReportTypeEnum reportType) {
        ReportEntity original =
                ReportEntity.builder()
                        .generateDate(generateDate)
                        .lastModified(generateDate)
                        .reportType(reportType)
                        .build();

        ReportEntity returnedEntity = reportEntityRepository.save(original);

        // return generated id
        return returnedEntity.getReportId();
    }

    /**
     * Gets the latest articles between now and start date
     *
     * @param startDate
     * @return
     */
    public List<ArticlesEntity> getArticlesAfterDate(Instant startDate, int maxSize) {
        PageRequest pageRequest = PageRequest.of(0, maxSize);

        Page<ArticlesEntity> response =
                articlesRepository.findByDateIngestedAfter(startDate, pageRequest);

        return response.getContent();
    }

    public void addArticleToReport(
            ArticlesEntity articlesEntity, int reportId, short rank, boolean isSuggestion) {
        ReportArticlesId reportArticlesId =
                new ReportArticlesId(reportId, articlesEntity.getArticleId());
        ReportArticlesEntity reportArticlesEntity =
                new ReportArticlesEntity(reportArticlesId, rank, isSuggestion);

        reportArticlesEntityRepository.save(reportArticlesEntity);
    }
}
