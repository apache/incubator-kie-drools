package org.kie.kogito.cloud.workitems.httpcalls;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages={"org.kie.kogito.**"})
public class HttpCallsApplication {

	public static void main(String[] args) {
		SpringApplication.run(HttpCallsApplication.class, args);
	}

}
