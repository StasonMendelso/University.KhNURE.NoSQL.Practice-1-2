package ua.nure.st.kpp.example.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import ua.nure.st.kpp.example.demo.dao.MongoDbDAOConfig;
import ua.nure.st.kpp.example.demo.dao.MySqlDAOConfig;

@SpringBootApplication
@EnableConfigurationProperties({MySqlDAOConfig.class, MongoDbDAOConfig.class})
public class DemoApplication {

	public static void main(String[] args) {
		SpringApplication.run(DemoApplication.class, args);
	}

}
