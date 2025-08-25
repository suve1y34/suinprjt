package co.kr.sikim.suinproject;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.UserDetailsServiceAutoConfiguration;

@SpringBootApplication(exclude = { UserDetailsServiceAutoConfiguration.class })
@MapperScan(basePackages = "co.kr.sikim.suinproject.mapper")
public class SuinprojectApplication {

	public static void main(String[] args) {
		SpringApplication.run(SuinprojectApplication.class, args);
	}

}
