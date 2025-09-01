package mip.mva.sp.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

@Configuration
public class CorsConfig {

    @Value("${spring.cors.mapping-pattern}")
    private String mappingPattern;

    @Value("${spring.cors.allowed-origins}")
    private List<String> allowedOrigins;

    @Value("${spring.cors.allowed-methods}")
    private List<String> allowedMethods;

    @Value("${spring.cors.allowed-headers}")
    private List<String> allowedHeaders;

    @Value("${spring.cors.allow-credentials}")
    private boolean allowCredentials;

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(allowedOrigins);
        configuration.setAllowedMethods(allowedMethods);
        configuration.setAllowedHeaders(allowedHeaders);
        configuration.setAllowCredentials(allowCredentials);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration(mappingPattern, configuration);

        return source;
    }

    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping(mappingPattern)
                        .allowedOrigins(allowedOrigins.toArray(new String[allowedOrigins.size()]))
                        .allowedMethods(allowedMethods.toArray(new String[allowedMethods.size()]))
                        .allowedHeaders(allowedHeaders.toArray(new String[allowedHeaders.size()]))
                        .allowCredentials(allowCredentials);
            }
        };
    }
}
