package me.t65.reportgenerator.config;

@lombok.Setter
@lombok.Getter
@lombok.Builder
@lombok.AllArgsConstructor
@lombok.NoArgsConstructor
public class RetryConfig {
    private int maxRetryAttempts;
    private long retryBackoffMillis;
}
