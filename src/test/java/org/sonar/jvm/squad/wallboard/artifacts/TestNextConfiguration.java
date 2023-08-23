package org.sonar.jvm.squad.wallboard.artifacts;

import java.io.IOException;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

@TestConfiguration
class TestNextConfiguration extends NextConfiguration{
  @Override
  @Bean
  String sonarQubeToken() throws IOException {
    return "squ_blip_blop";
  }
}
