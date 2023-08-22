package org.sonar.jvm.squad.wallboard.github;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.nio.file.Path;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;

@Configuration
public class GithubConfig {

  public record Credentials(String githubToken){

  }

  @Bean
  @Lazy
  public Credentials githubCredentials() throws IOException {
    Path path = Path.of("private-credentials", "github-credentials.json");
    return new ObjectMapper().readValue(path.toFile(), GithubConfig.Credentials.class);
  }


}
