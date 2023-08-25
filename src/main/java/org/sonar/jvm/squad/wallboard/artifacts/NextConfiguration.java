package org.sonar.jvm.squad.wallboard.artifacts;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;

@Configuration
@Lazy
public class NextConfiguration {
  @Bean
  String sonarQubeToken() throws IOException {
    File path = Path.of("private-credentials", "sonarqube.token").toFile();
    try (InputStream in = new FileInputStream(path)) {
      return new String(in.readAllBytes(), StandardCharsets.UTF_8);
    }
  }
}
