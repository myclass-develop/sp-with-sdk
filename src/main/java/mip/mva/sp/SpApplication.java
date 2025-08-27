package mip.mva.sp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;

/**
 * @Project 모바일 운전면허증 서비스 구축 사업
 * @PackageName mip.mva.sp
 * @FileName SpApplication.java
 * @Author mgj
 * @Date 2022. 5. 1.
 * @Description Spring Boot Initializer
 * 
 * <pre>
 * ==================================================
 * DATE            AUTHOR           NOTE
 * ==================================================
 * 2024. 5. 28.    민기주           최초생성
 * </pre>
 */
@SpringBootApplication
public class SpApplication extends SpringBootServletInitializer {

	/**
	 * Spring boot 실행
	 * 
	 * @MethodName main
	 * @param args
	 */
	public static void main(String[] args) {
		SpringApplication.run(SpApplication.class, args);
	}

	@Override
	protected SpringApplicationBuilder configure(SpringApplicationBuilder builder) {
		return builder.sources(SpApplication.class);
	}

}