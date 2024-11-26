package me.t65.reportgenerator.db.postgres.entities;

import jakarta.persistence.*;

import me.t65.reportgenerator.db.postgres.ReportTypeEnum;

import java.time.Instant;

@lombok.Getter
@lombok.Builder
@lombok.AllArgsConstructor
@lombok.NoArgsConstructor
@lombok.ToString
@lombok.EqualsAndHashCode
@Entity
@Table(name = "report")
public class ReportEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "report_id", columnDefinition = "SERIAL")
    private int reportId;

    @Column(name = "generate_date")
    private Instant generateDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "report_type")
    private ReportTypeEnum reportType;

    @Column(name = "last_modified")
    private Instant lastModified;
}
