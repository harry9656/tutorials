package com.baeldung;

import org.junit.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.web.WebAppConfiguration;

@ExtendWith(SpringExtension.class)
@ContextConfiguration({ "/RedirectionWebSecurityConfig.xml" })
@WebAppConfiguration
public class SpringContextTest {
	@Test
	public void whenSpringContextIsBootstrapped_thenNoExceptions() {
	}
}
