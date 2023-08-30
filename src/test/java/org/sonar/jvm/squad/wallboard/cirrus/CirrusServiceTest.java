package org.sonar.jvm.squad.wallboard.cirrus;

import java.io.IOException;
import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.Test;
import org.sonar.jvm.squad.wallboard.client.RestConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ResourceLoader;
import org.springframework.test.web.client.MockRestServiceServer;

import static org.assertj.core.api.Assertions.assertThat;
import static org.sonar.jvm.squad.wallboard.TestUtils.expectPOST;
import static org.sonar.jvm.squad.wallboard.TestUtils.jsonResponse;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.header;

@SpringBootTest(classes = {CirrusServiceTest.TestCirrusConfig.class, RestConfig.class, CirrusService.class})
class CirrusServiceTest {

  @Configuration
  static class TestCirrusConfig {
    @Bean
    CirrusConfig.Credentials credentials() {
      return new CirrusConfig.Credentials("2c255cad1bea5744ce44aac20b29dae3c6e6801a206cb726ebb41");
    }
  }

  @Autowired
  CirrusService cirrusService;

  @Autowired
  ResourceLoader loader;

  static Response RESPONSE = new Response(
    new Response.Data(new Response.OwnerRepository("1234", "sonar", "my_repo", new Response.Builds(List.of(new Response.Edge(new Response.Node("4321", "change title", 3456,
      "COMPLETED", "main", List.of(), List.of())))))));

  @Test
  void test_cirrus_data() throws IOException {
    MockRestServiceServer server = MockRestServiceServer.bindTo(cirrusService.rest).build();
    expectPOST(server, "https://api.cirrus-ci.com/graphql", """
      {
      "query": "query q($platform: String!, $owner: String!, $name: String!, $branch: String) {
                  ownerRepository(platform: $platform, owner: $owner, name: $name) {
                    id
                    owner
                    name
                    builds(last: 1, branch: $branch) {
                      edges {
                        node {
                          id
                          changeMessageTitle
                          durationInSeconds
                          status
                          branch
                          ...ErroredBuildStatus
                          ...FailedBuildStatus
                        }
                      }
                    }
                  }
                }
                fragment ErroredBuildStatus on Build {
                        notifications {
                          message
                          level
                        }
                }
                fragment FailedBuildStatus on Build {
                        tasks {
                          name
                          status
                          notifications {
                            message
                            level
                          }
                        }
                }
                ",
      "variables": {
         "platform": "github",
         "owner": "SonarSource",
         "name": "my-project",
         "branch": "master" }
      }
      }
      """)
      .andExpect(header("cookie", cirrusService.credentials.cirrusCookie()))
      .andRespond(jsonResponse(loader, "cirrus/cirrus-data-response.json"));

    List<Response> summary = cirrusService.getCirrusData(Set.of("my-project"));

    server.verify();
    assertThat(summary)
      .isNotNull()
      .isNotEmpty();
    assertThat(summary.get(0)).isEqualTo(RESPONSE);
  }

}
