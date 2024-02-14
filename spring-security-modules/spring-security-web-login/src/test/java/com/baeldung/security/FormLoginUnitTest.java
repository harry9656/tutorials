package com.baeldung.security;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestBuilders.formLogin;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.authenticated;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import com.baeldung.security.config.SecSecurityConfig;

import jakarta.servlet.Filter;

@ContextConfiguration(classes = { SecSecurityConfig.class })
@WebMvcTest
class FormLoginUnitTest {

    @Autowired
    private WebApplicationContext context;

    @Autowired
    private Filter springSecurityFilterChain;

    private MockMvc mvc;

    @BeforeEach
    void setup() {
        mvc = MockMvcBuilders.webAppContextSetup(context)
            .addFilters(springSecurityFilterChain)
            .build();
    }

    @Test
    void givenValidRequestWithValidCredentials_shouldLoginSuccessfully() throws Exception {
        mvc.perform(formLogin("/perform_login").user("user1")
                .password("user1Pass"))
            .andExpect(status().isFound())
            .andExpect(authenticated().withUsername("user1"));
    }

    @Test
    void givenValidRequestWithInvalidCredentials_shouldFailWith401() throws Exception {
        MvcResult result = mvc.perform(formLogin("/perform_login").user("random")
                .password("random"))
            .andReturn();
                /*.andExpect(status().isUnauthorized())
                .andDo(print())
                .andExpect(unauthenticated())
                .andReturn();*/

        Assertions.assertTrue(result.getResponse()
            .getContentAsString()
            .contains("Bad credentials"));
    }
}
