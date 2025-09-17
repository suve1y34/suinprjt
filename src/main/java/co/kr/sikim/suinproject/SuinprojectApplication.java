package co.kr.sikim.suinproject;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.UserDetailsServiceAutoConfiguration;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;

@SpringBootApplication(exclude = { UserDetailsServiceAutoConfiguration.class })
@MapperScan(basePackages = "co.kr.sikim.suinproject.mapper")
public class SuinprojectApplication extends SpringBootServletInitializer {

	// 톰캣(WAR)에서 쓰는 엔트리포인트
	@Override
	protected SpringApplicationBuilder configure(SpringApplicationBuilder builder) {
		return builder.sources(SuinprojectApplication.class);
	}

	public static void main(String[] args) {
		SpringApplication.run(SuinprojectApplication.class, args);
	}

}