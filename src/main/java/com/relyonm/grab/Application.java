package com.relyonm.grab;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import jakarta.annotation.PostConstruct;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.ClassPathResource;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import java.io.IOException;

@SpringBootApplication
@EnableAsync
@EnableScheduling
public class Application {

  public static void main(String[] args) {
    SpringApplication.run(Application.class, args);
  }

  @Bean
  public CorsFilter corsFilter() {
    var config = new CorsConfiguration();
    config.addAllowedOriginPattern("*"); // allow all origins
    config.addAllowedHeader("*");        // allow all headers
    config.addAllowedMethod("*");        // allow all HTTP methods
    config.setAllowCredentials(true);    // allow cookies/authorization headers if needed

    var source = new UrlBasedCorsConfigurationSource();
    source.registerCorsConfiguration("/**", config);

    return new CorsFilter(source);
  }

  @PostConstruct
  public void initialize() throws IOException {
    var serviceAccount = new ClassPathResource("firebase-credentials.json").getInputStream();

    var options = FirebaseOptions
      .builder()
      .setCredentials(GoogleCredentials.fromStream(serviceAccount))
      .build();

    FirebaseApp.initializeApp(options);
  }
}
