package cl.latam.contactabilitytest;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.annotation.Bean;

import cl.latam.contactabilitytest.features.contactability.ContactabilityConsoleRunner;

@SpringBootApplication
public class ContactabilityTestApplication {
	public static void main(String[] args) {
		new SpringApplicationBuilder(ContactabilityTestApplication.class)
				.web(WebApplicationType.NONE)
				.run(args);
	}

	@Bean
	CommandLineRunner runner(ContactabilityConsoleRunner contactabilityConsoleRunner) {
		return contactabilityConsoleRunner;
	}
}
