package com.bootlabs.pdbdemo.health;

import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.stereotype.Component;

// Contributes to the "readiness" health group (see application.yml). Flipping
// ChaosState makes this one pod fail its readiness probe on demand, which is
// exactly the signal PodDisruptionBudget uses to count "currently healthy"
// replicas against minAvailable.
@Component
public class ChaosHealthIndicator implements HealthIndicator {

    private final ChaosState chaosState;

    public ChaosHealthIndicator(ChaosState chaosState) {
        this.chaosState = chaosState;
    }

    @Override
    public Health health() {
        if (chaosState.isUnready()) {
            return Health.down().withDetail("reason", "chaos toggle forced this pod unready").build();
        }
        return Health.up().build();
    }
}
