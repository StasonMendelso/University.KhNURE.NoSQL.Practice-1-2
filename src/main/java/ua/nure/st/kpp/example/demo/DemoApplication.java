package ua.nure.st.kpp.example.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import ua.nure.st.kpp.example.demo.dao.DAOConfig;

@SpringBootApplication
@EnableConfigurationProperties(DAOConfig.class)
public class DemoApplication {

	public static void main(String[] args) {
		SpringApplication.run(DemoApplication.class, args);
	}

}
