package com.BankApplication.bankapplication;

import io.swagger.v3.oas.annotations.ExternalDocumentation;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.info.License;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@OpenAPIDefinition(
		info = @Info(
				title = "Secure Bank App",
				description = "Backend Rest APIs for Bank App",
				version = "v1.0",
				contact = @Contact(
						name = "Kent",
						email = "kentk1701@gmail.com"
				),
				license = @License(
						name = "Spring boot Secure App"
				)
		),
		externalDocs = @ExternalDocumentation(
				description = "Spring boot Secure App"
		)
)
public class BankApplication {

	public static void main(String[] args) {
		SpringApplication.run(BankApplication.class, args);
	}

}
