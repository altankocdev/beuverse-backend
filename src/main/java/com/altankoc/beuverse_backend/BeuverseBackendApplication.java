package com.altankoc.beuverse_backend;

import com.altankoc.beuverse_backend.core.config.AwsProperties;
import com.altankoc.beuverse_backend.core.config.JwtProperties;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

@OpenAPIDefinition(
		info = @Info(
				title = "Beuverse API",
				version = "1.0",
				description = "Üniversite öğrencilerine özel sosyal medya platformu"
		)
)
@SpringBootApplication
@EnableConfigurationProperties({JwtProperties.class, AwsProperties.class})
@EnableScheduling
@EnableAsync
public class BeuverseBackendApplication {

	public static void main(String[] args) {
		SpringApplication.run(BeuverseBackendApplication.class, args);
	}
}