package co.kr.sikim.suinproject;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan(basePackages = "co.kr.sikim.suinproject.mapper")
public class SuinprojectApplication {

	public static void main(String[] args) {
		SpringApplication.run(SuinprojectApplication.class, args);
	}

}
