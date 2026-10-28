package com.bootlabs.pdbdemo.controller;

import com.bootlabs.pdbdemo.health.ChaosState;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ChaosController.class)
@Import(ChaosState.class)
class ChaosControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ChaosState chaosState;

    @AfterEach
    void resetChaosState() {
        chaosState.markReady();
    }

    @Test
    void unreadyEndpointFlipsChaosState() throws Exception {
        mockMvc.perform(post("/api/chaos/unready"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.chaos").value("unready"));

        assertThat(chaosState.isUnready()).isTrue();
    }

    @Test
    void recoverEndpointResetsChaosState() throws Exception {
        chaosState.markUnready();

        mockMvc.perform(post("/api/chaos/recover"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.chaos").value("ready"));

        assertThat(chaosState.isUnready()).isFalse();
    }
}
