package com.bootlabs.pdbdemo.controller;

import com.bootlabs.pdbdemo.health.ChaosState;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(StatusController.class)
@Import(ChaosState.class)
class StatusControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ChaosState chaosState;

    @AfterEach
    void resetChaosState() {
        // The Spring context — and this singleton bean — is cached and reused
        // across test methods in this class, so state must be reset explicitly
        // or an earlier test's toggle leaks into a later one.
        chaosState.markReady();
    }

    @Test
    void reportsReadyByDefault() throws Exception {
        mockMvc.perform(get("/api/status"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.ready").value(true));
    }

    @Test
    void reportsNotReadyAfterChaosToggle() throws Exception {
        chaosState.markUnready();

        mockMvc.perform(get("/api/status"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.ready").value(false));
    }
}
