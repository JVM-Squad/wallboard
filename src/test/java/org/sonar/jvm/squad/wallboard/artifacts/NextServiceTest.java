package org.sonar.jvm.squad.wallboard.artifacts;

import java.io.IOException;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.sonar.jvm.squad.wallboard.client.RestConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ResourceLoader;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.test.web.client.match.MockRestRequestMatchers;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

@SpringBootTest(classes = {RestConfig.class, TestNextConfiguration.class, NextService.class})
class NextServiceTest {
  @Autowired
  private NextService service;

  @Autowired
  ResourceLoader resourceLoader;

  @Nested
  class getInstalledVersion {
    @Test
    void throws_an_IllegalArgumentException_when_a_plugin_name_cannot_be_matched() {
      assertThatThrownBy(() -> service.getInstalledVersion("unknown"))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("Could not match analyzer name with known plugin (unknown)");
    }

    @Test
    void returns_the_expected_version() throws IOException {
      MockRestServiceServer mockService = MockRestServiceServer.bindTo(service.rest).build();
      mockService.expect(MockRestRequestMatchers.requestTo(NextService.PLUGINS_INSTALLED_ENDPOINT))
        .andExpect(MockRestRequestMatchers.method(HttpMethod.GET))
        .andExpect(MockRestRequestMatchers.header("Authorization", "Bearer squ_blip_blop"))
        .andRespond(withSuccess(fromClasspath("plugins.json"), MediaType.APPLICATION_JSON));
      assertThat(service.getInstalledVersion("sonar-java"))
        .isEqualTo("7.25 (build 32124)");
    }
  }

  String fromClasspath(String path) throws IOException {
    return resourceLoader.getResource("classpath:" + path).getContentAsString(UTF_8);
  }
}