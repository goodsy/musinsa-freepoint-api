package com.musinsa.freepoint.adapters.in.web;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.musinsa.freepoint.adapters.in.web.dto.AccrualRequest;
import com.musinsa.freepoint.application.service.AccrualUseCase;
import com.musinsa.freepoint.application.port.in.Commands.AccrualCommand;
import com.musinsa.freepoint.domain.accrual.PointAccrual;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AccrualController.class)
class AccrualControllerTest {

    @Autowired
    MockMvc mockMvc;

    @MockBean
    AccrualUseCase accrualUseCase;

    @Autowired
    ObjectMapper objectMapper;

    String USER_ID;

    @BeforeEach
    void setUp() {
        USER_ID = "testUserId";
    }

    @Test
    @DisplayName("정상 accrual 요청 성공")
    void createAccrual() throws Exception {
        AccrualRequest request = new AccrualRequest(USER_ID, 1000L, 30, false, "EVENT", "source-1");
        PointAccrual accrual = new PointAccrual(USER_ID, 1000L, 30, false, "EVENT", "source-1");

        given(accrualUseCase.accrue(ArgumentMatchers.any(AccrualCommand.class)))
                .willReturn(accrual);

        mockMvc.perform(post("/api/v1/points/accruals")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(10L))
                .andExpect(jsonPath("$.userId").value(1L))
                .andExpect(jsonPath("$.amount").value(1000L))
                .andExpect(jsonPath("$.remainAmount").value(1000L))
                .andExpect(jsonPath("$.manual").value(false));
    }
}