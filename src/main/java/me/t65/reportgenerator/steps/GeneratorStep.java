package me.t65.reportgenerator.steps;

import reactor.core.publisher.Flux;

public interface GeneratorStep {
    Flux<?> run(int reportId);
}
