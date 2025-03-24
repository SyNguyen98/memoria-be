package org.chika.memoria;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeIn;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

@SpringBootApplication
@EnableMongoRepositories
@SecurityScheme(type = SecuritySchemeType.HTTP, name = "bearer-jwt", description = "Bearer Token", bearerFormat = "JWT", scheme = "bearer", in = SecuritySchemeIn.HEADER, paramName = "Authorization")
@OpenAPIDefinition(info = @Info(title = "MEMORIA API", version = "1.0.0"), security = @SecurityRequirement(name = "bearer-jwt"))
public class MemoriaApplication {

	public static void main(String[] args) {
		SpringApplication.run(MemoriaApplication.class, args);
	}
}
