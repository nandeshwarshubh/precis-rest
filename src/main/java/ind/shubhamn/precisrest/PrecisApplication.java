package ind.shubhamn.precisrest;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.TimeZone;

@SpringBootApplication
public class PrecisApplication {

	public static void main(String[] args) {
		// Set default timezone to UTC to avoid PostgreSQL timezone issues
		// This fixes the "Asia/Calcutta" timezone error with PostgreSQL 17+
		TimeZone.setDefault(TimeZone.getTimeZone("UTC"));

		SpringApplication.run(PrecisApplication.class, args);
	}

}
