package com.matheussilvadev;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

@SpringBootApplication
@EntityScan(basePackages = {"com.matheussilvadev.model"})// Mapear Entidades
@ComponentScan(basePackages = {"com.matheussilvadev.*"}) //Facilitar injeção de dependências
@EnableJpaRepositories(basePackages = {"com.matheussilvadev.repository"}) // Mapear Entidades
@EnableTransactionManagement //Gerência de Transação
@EnableWebMvc //Ativa Recursos MVC
@RestController //Ativa Rest
@EnableAutoConfiguration //
public class SpringbootApiApplication {

	public static void main(String[] args) {
		SpringApplication.run(SpringbootApiApplication.class, args);
	}

}
