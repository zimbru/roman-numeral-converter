package com.alex.aem;

import jakarta.annotation.Resource;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(classes = AemApplication.class)
@AutoConfigureMockMvc
class RomanNumeralConversionIntegrationTest {

    @Resource
    private MockMvc mockMvc;

    @Test
    void Single_number_conversion_request_returns_correct_response() throws Exception {
        mockMvc.perform(get("/romannumeral").param("query", "9"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.input").value(9))
                .andExpect(jsonPath("$.output").value("IX"));
    }

    @Test
    void Range_conversion_request_returns_correct_response() throws Exception {
        mockMvc.perform(get("/romannumeral").param("min", "1").param("max", "3"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.conversions").isArray())
                .andExpect(jsonPath("$.conversions.length()").value(3))
                .andExpect(jsonPath("$.conversions[0].input").value(1))
                .andExpect(jsonPath("$.conversions[0].output").value("I"))
                .andExpect(jsonPath("$.conversions[2].input").value(3))
                .andExpect(jsonPath("$.conversions[2].output").value("III"));
    }

    @Test
    void Invalid_parameter_request_returns_bad_request() throws Exception {
        mockMvc.perform(get("/romannumeral").param("min", "1"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void Invalid_no_parameter_request_returns_bad_request() throws Exception {
        mockMvc.perform(get("/romannumeral"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void Out_of_range_number_request_returns_bad_request() throws Exception {
        mockMvc.perform(get("/romannumeral").param("query", "4000"))
                .andExpect(status().isBadRequest());
    }
}