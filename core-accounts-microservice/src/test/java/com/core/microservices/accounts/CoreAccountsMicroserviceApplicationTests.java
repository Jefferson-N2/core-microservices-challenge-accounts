package com.core.microservices.accounts;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@AutoConfigureWebTestClient
@ActiveProfiles("test")
class CoreAccountsMicroserviceApplicationTests {

	@Test
	void contextLoads() {
	}

}
