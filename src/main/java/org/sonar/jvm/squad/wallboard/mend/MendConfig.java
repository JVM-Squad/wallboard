package org.sonar.jvm.squad.wallboard.mend;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.nio.file.Path;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MendConfig {

  public record Credentials(
    String organizationApiKey,
    String apiBaseUrlV2,
    String userEmail,
    String userKey) {
  }

  @Bean
  public Credentials credentials() throws IOException {
    Path path = Path.of("private-credentials", "mend-credentials.json");
    return new ObjectMapper().readValue(path.toFile(), Credentials.class);
  }

}
