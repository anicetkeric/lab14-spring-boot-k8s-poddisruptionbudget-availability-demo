package com.bootlabs.pdbdemo.health;

import org.springframework.stereotype.Component;

import java.util.concurrent.atomic.AtomicBoolean;

// Shared in-memory toggle, flipped by the chaos endpoints and read by
// ChaosHealthIndicator, so this one pod's readiness can be forced down
// on demand without redeploying — used to make PDB's eviction blocking
// reproducible on command instead of racing real node-drain timing.
@Component
public class ChaosState {

    private final AtomicBoolean unready = new AtomicBoolean(false);

    public void markUnready() {
        unready.set(true);
    }

    public void markReady() {
        unready.set(false);
    }

    public boolean isUnready() {
        return unready.get();
    }
}
