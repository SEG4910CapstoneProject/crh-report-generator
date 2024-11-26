package me.t65.reportgenerator.db.postgres.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.Instant;
import java.util.UUID;

@lombok.Getter
@lombok.AllArgsConstructor
@lombok.NoArgsConstructor
@lombok.ToString
@lombok.EqualsAndHashCode
@Entity
@Table(name = "articles")
public class ArticlesEntity {

    @Id
    @Column(name = "article_ID", columnDefinition = "uuid")
    private UUID articleId;

    @Column(name = "source_ID")
    private Integer sourceId;

    @Column(name = "date_ingested")
    private Instant dateIngested;

    @Column(name = "date_published")
    private Instant datePublished;

    @Column(name = "is_feature_ext")
    private Boolean isFeatureExt;

    @Column(name = "is_ML_ext")
    private Boolean isMLExt;

    @Column(name = "hashlink")
    private long hashlink;
}
