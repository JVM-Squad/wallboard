package org.sonar.jvm.squad.wallboard.cirrus;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.nio.file.Path;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;

@Configuration
public class CirrusConfig {

  public record Credentials(String cirrusCookie) {
  }

  @Bean
  @Lazy
  public Credentials cirrusCredentials() throws IOException {
    Path path = Path.of("private-credentials", "cirrus-credentials.json");
    return new ObjectMapper().readValue(path.toFile(), Credentials.class);
  }

}
