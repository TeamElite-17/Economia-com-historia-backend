package com.isptec.economiahistoriaapi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class EconomiaHistoriaApiApplication {

	public static void main(String[] args) {
		SpringApplication.run(EconomiaHistoriaApiApplication.class, args);
	}

}
