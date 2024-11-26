package me.t65.reportgenerator.db.postgres.entities.id;

import jakarta.persistence.*;

import java.io.Serializable;
import java.util.UUID;

@lombok.AllArgsConstructor
@lombok.NoArgsConstructor
@lombok.ToString
@lombok.EqualsAndHashCode
@Embeddable
public class ReportArticlesId implements Serializable {
    @Column(name = "report_id")
    private int reportId;

    @Column(name = "article_ID", columnDefinition = "uuid")
    private UUID articleId;
}
