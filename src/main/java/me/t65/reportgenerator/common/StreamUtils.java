package me.t65.reportgenerator.common;

import me.t65.reportgenerator.config.RetryConfig;

import reactor.core.publisher.Flux;
import reactor.util.retry.Retry;
import reactor.util.retry.RetryBackoffSpec;
import reactor.util.retry.RetrySpec;

import java.time.Duration;
import java.util.function.Consumer;

public class StreamUtils {
    public static <T> Flux<T> doRetries(
            Flux<T> originalStream,
            RetryConfig retryConfig,
            Consumer<? super Throwable> onErrorMessage,
            Consumer<Retry.RetrySignal> retryMessage,
            Consumer<Retry.RetrySignal> exhaustedFailMessage) {
        return originalStream
                .doOnError(onErrorMessage)
                .retryWhen(getRetrySpec(retryConfig, retryMessage, exhaustedFailMessage));
    }

    private static RetryBackoffSpec getRetrySpec(
            RetryConfig retryConfig,
            Consumer<Retry.RetrySignal> retryMessage,
            Consumer<Retry.RetrySignal> exhaustedFailMessage) {
        return RetrySpec.fixedDelay(
                        retryConfig.getMaxRetryAttempts(),
                        Duration.ofMillis(retryConfig.getRetryBackoffMillis()))
                .doBeforeRetry(retryMessage)
                .onRetryExhaustedThrow(
                        (spec, retrySignal) -> {
                            exhaustedFailMessage.accept(retrySignal);
                            return retrySignal.failure();
                        });
    }
}
