package com.bootlabs.pdbdemo.controller;

import com.bootlabs.pdbdemo.health.ChaosState;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

// Demo-only chaos endpoints, deliberately unauthenticated for lab
// simplicity — never ship an unauthenticated state-mutating endpoint
// like this in a real service.
@RestController
public class ChaosController {

    private final ChaosState chaosState;

    public ChaosController(ChaosState chaosState) {
        this.chaosState = chaosState;
    }

    @PostMapping("/api/chaos/unready")
    public Map<String, Object> makeUnready() {
        chaosState.markUnready();
        return Map.of("chaos", "unready");
    }

    @PostMapping("/api/chaos/recover")
    public Map<String, Object> recover() {
        chaosState.markReady();
        return Map.of("chaos", "ready");
    }
}
