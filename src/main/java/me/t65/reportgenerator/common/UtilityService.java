package me.t65.reportgenerator.common;

import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
public class UtilityService {

    public Instant currentDate() {
        return Instant.now();
    }

    public Instant currentInstant() {
        return Instant.now();
    }
}
