package org.sonar.jvm.squad.wallboard.github;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.sonar.jvm.squad.wallboard.client.RestConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.header;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;


@SpringBootTest(classes = {GithubServiceTest.TestGithubConfig.class, GithubService.class, RestConfig.class, ObjectMapper.class})
public class GithubServiceTest {

  @Configuration
  static class TestGithubConfig {

    @Bean
    public GithubConfig.Credentials credentials(){
      return new GithubConfig.Credentials("2c255cad1bea5744ce44aac20b29dae3c6e6801a206cb726ebb4");
    }

    @Bean
    public MockRestServiceServer server(GithubService service){
      return  MockRestServiceServer.bindTo(service.rest).build();
    }

  }


  @Autowired
  private GithubService githubService;
  @Autowired
  private MockRestServiceServer server;

  @Autowired
  private ObjectMapper objectMapper;

  @Test
  public void test_release() throws Exception {
    String repo = "sonar-java";
    String releaseNumber = "10.2";
    String releaseDate = "2023-01-13";

    String data =
      objectMapper.writeValueAsString(new GithubService.Release("", releaseNumber, releaseDate));

    server
      .expect(requestTo("https://api.github.com/repos/sonarsource/sonar-java/releases/latest"))
      .andExpect(method(HttpMethod.GET))
      .andExpect(header("Authorization", "Bearer " + "2c255cad1bea5744ce44aac20b29dae3c6e6801a206cb726ebb4"))
      .andExpect(header("Content-Type", "application/json"))
      .andRespond(withSuccess(data, MediaType.APPLICATION_JSON));

    GithubService.Release release = githubService.getRelease(repo);

    server.verify();
    assertThat(release).isNotNull();
    assertThat(release.repoName()).isEqualTo(repo);
    assertThat(release.name()).isEqualTo(releaseNumber);
    assertThat(release.published_at()).isEqualTo(releaseDate);
  }
}
