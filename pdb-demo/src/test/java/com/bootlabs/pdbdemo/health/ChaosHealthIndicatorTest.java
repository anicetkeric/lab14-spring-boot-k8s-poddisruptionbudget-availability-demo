package com.bootlabs.pdbdemo.health;

import org.junit.jupiter.api.Test;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.Status;

import static org.assertj.core.api.Assertions.assertThat;

class ChaosHealthIndicatorTest {

    @Test
    void reportsUpByDefault() {
        ChaosState chaosState = new ChaosState();
        ChaosHealthIndicator indicator = new ChaosHealthIndicator(chaosState);

        Health health = indicator.health();

        assertThat(health.getStatus()).isEqualTo(Status.UP);
    }

    @Test
    void reportsDownAfterMarkUnready() {
        ChaosState chaosState = new ChaosState();
        chaosState.markUnready();
        ChaosHealthIndicator indicator = new ChaosHealthIndicator(chaosState);

        Health health = indicator.health();

        assertThat(health.getStatus()).isEqualTo(Status.DOWN);
    }

    @Test
    void reportsUpAgainAfterRecover() {
        ChaosState chaosState = new ChaosState();
        chaosState.markUnready();
        chaosState.markReady();
        ChaosHealthIndicator indicator = new ChaosHealthIndicator(chaosState);

        Health health = indicator.health();

        assertThat(health.getStatus()).isEqualTo(Status.UP);
    }
}
