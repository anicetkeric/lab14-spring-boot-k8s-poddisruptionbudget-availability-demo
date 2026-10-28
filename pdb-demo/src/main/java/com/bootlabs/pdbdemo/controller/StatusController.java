package com.bootlabs.pdbdemo.controller;

import com.bootlabs.pdbdemo.dto.StatusResponse;
import com.bootlabs.pdbdemo.health.ChaosState;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class StatusController {

    private final String podName;
    private final String nodeName;
    private final ChaosState chaosState;

    public StatusController(ChaosState chaosState) {
        this.chaosState = chaosState;
        this.podName = System.getenv().getOrDefault("POD_NAME", "local");
        this.nodeName = System.getenv().getOrDefault("NODE_NAME", "local");
    }

    @GetMapping("/api/status")
    public StatusResponse status() {
        return new StatusResponse(podName, nodeName, !chaosState.isUnready());
    }
}
