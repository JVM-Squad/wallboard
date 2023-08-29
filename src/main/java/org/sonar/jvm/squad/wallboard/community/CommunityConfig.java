package org.sonar.jvm.squad.wallboard.community;


import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.nio.file.Path;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;

@Configuration
public class CommunityConfig {

  public record Credentials(String discourseToken){

  }

  @Bean
  @Lazy
  public CommunityConfig.Credentials communityCredentials() throws IOException {
    Path path = Path.of("private-credentials", "community-credentials.json");
    return new ObjectMapper().readValue(path.toFile(), CommunityConfig.Credentials.class);
  }
}
