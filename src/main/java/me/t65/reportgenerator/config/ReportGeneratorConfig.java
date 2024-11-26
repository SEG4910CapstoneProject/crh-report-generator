package me.t65.reportgenerator.config;

import me.t65.reportgenerator.db.postgres.ReportTypeEnum;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import reactor.core.scheduler.Scheduler;
import reactor.core.scheduler.Schedulers;

@lombok.Getter
@lombok.Setter
@Configuration
@ConfigurationProperties("report-generator")
public class ReportGeneratorConfig {

    private ReportTypeEnum type;
    private RetryConfig dbRetry;

    @Value("${report-generator.articleSelection.maxSuggestedArticles}")
    private int maxSuggestedArticles;

    @Bean
    public Scheduler getReactiveScheduler() {
        return Schedulers.boundedElastic();
    }
}
