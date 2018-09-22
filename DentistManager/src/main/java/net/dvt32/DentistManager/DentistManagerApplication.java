package net.dvt32.DentistManager;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

@SpringBootApplication
public class DentistManagerApplication {

	public static void main(String[] args) {
		ApplicationContext context = SpringApplication.run(DentistManagerApplication.class, args);
	}
	
}
