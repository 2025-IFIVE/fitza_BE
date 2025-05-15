package com.ifive.fitza;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = "com.ifive.fitza")
public class FitzaApplication {

	public static void main(String[] args) {
		SpringApplication.run(FitzaApplication.class, args);
	}

}
