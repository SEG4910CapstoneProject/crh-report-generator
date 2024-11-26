package me.t65.reportgenerator.db.postgres.entities;

import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

import me.t65.reportgenerator.db.postgres.entities.id.ReportArticlesId;

@lombok.Getter
@lombok.AllArgsConstructor
@lombok.NoArgsConstructor
@lombok.ToString
@lombok.EqualsAndHashCode
@Entity
@Table(name = "report_articles")
public class ReportArticlesEntity {
    @EmbeddedId private ReportArticlesId reportArticlesId;

    @Column(name = "article_rank")
    private short articleRank;

    @Column(name = "suggestion")
    private boolean isSuggestion;
}
