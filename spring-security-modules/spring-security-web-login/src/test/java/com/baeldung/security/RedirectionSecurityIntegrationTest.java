package com.baeldung.security;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrlPattern;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

@ContextConfiguration({ "/RedirectionWebSecurityConfig.xml" })
@WebMvcTest
class RedirectionSecurityIntegrationTest {

    @Autowired
    private WebApplicationContext context;

    @Autowired
    private UserDetailsService userDetailsService;

    private MockMvc mvc;
    private UserDetails userDetails;

    @BeforeEach
    void setup() {
        mvc = MockMvcBuilders.webAppContextSetup(context)
            .apply(springSecurity())
            .build();
        userDetails = userDetailsService.loadUserByUsername("user1");
    }

    @Test
    void givenSecuredResource_whenAccessUnauthenticated_thenRequiresAuthentication() throws Exception {
        mvc.perform(get("/secured"))
            .andExpect(status().is3xxRedirection())
            .andExpect(redirectedUrlPattern("**/login"));

    }

    @Test
    void givenCredentials_whenAccessSecuredResource_thenSuccess() throws Exception {
        mvc.perform(get("/secured").with(user(userDetails)))
            .andExpect(status().isOk());
    }

    @Test
    void givenAccessSecuredResource_whenAuthenticated_thenRedirectedBack() throws Exception {
        MockHttpServletRequestBuilder securedResourceAccess = get("/secured");
        MvcResult unauthenticatedResult = mvc.perform(securedResourceAccess)
            .andExpect(status().is3xxRedirection())
            .andReturn();

        MockHttpSession session = (MockHttpSession) unauthenticatedResult.getRequest()
            .getSession();
        String loginUrl = unauthenticatedResult.getResponse()
            .getRedirectedUrl();
        mvc.perform(post(loginUrl).param("username", userDetails.getUsername())
                .param("password", userDetails.getPassword())
                .session(session)
                .with(csrf()))
            .andExpect(status().is3xxRedirection())
            .andExpect(redirectedUrlPattern("**/secured?continue"))
            .andReturn();

        mvc.perform(securedResourceAccess.session(session))
            .andExpect(status().isOk());

    }

}
