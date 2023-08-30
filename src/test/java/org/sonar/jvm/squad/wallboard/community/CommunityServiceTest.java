package org.sonar.jvm.squad.wallboard.community;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.util.Set;
import org.junit.jupiter.api.Test;
import org.sonar.jvm.squad.wallboard.client.RestConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ResourceLoader;
import org.springframework.http.HttpMethod;
import org.springframework.test.web.client.MockRestServiceServer;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.sonar.jvm.squad.wallboard.TestUtils.fromClasspath;
import static org.sonar.jvm.squad.wallboard.TestUtils.jsonResponse;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.header;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;

@SpringBootTest(classes = {CommunityServiceTest.TestCommunityConfig.class, CommunityService.class, RestConfig.class, ObjectMapper.class})
class CommunityServiceTest {

  @Configuration
  static class TestCommunityConfig {

    @Bean
    public CommunityConfig.Credentials credentials(){
      return new  CommunityConfig.Credentials("2c255cad1bea5744ce44aac20b29dae3c6e6801a206cb726ebb4");
    }

    @Bean
    public MockRestServiceServer server(CommunityService service){
      return  MockRestServiceServer.bindTo(service.rest).build();
    }

  }

  @Autowired
  private CommunityService communityService;
  @Autowired
  private ObjectMapper objectMapper;
  @Autowired
  private MockRestServiceServer server;

  @Autowired
  private ResourceLoader loader;

  @Test
  void testAssignedTopics() throws IOException {
    String expectedJson = fromClasspath(loader, "community/expected-assigned-groups.json");
    CommunityService.CommunityData.AssignedGroupTopics expectedTopics = objectMapper.readValue(expectedJson, CommunityService.CommunityData.AssignedGroupTopics.class);

    server
      .expect(requestTo("https://community.sonarsource.com/topics/group-topics-assigned/JVM_Squad.json"))
      .andExpect(method(HttpMethod.GET))
      .andExpect(header("cookie", "_t=2c255cad1bea5744ce44aac20b29dae3c6e6801a206cb726ebb4"))
      .andRespond(jsonResponse(loader, "community/assigned-topics-response.json"));

    CommunityService.CommunityData.AssignedGroupTopics topics = communityService.assignedTopics("JVM_Squad");

    server.verify();

    assertThat(Set.copyOf(topics.assignedTopics())).isEqualTo(Set.copyOf(expectedTopics.assignedTopics()));
    assertThat(topics.total()).isEqualTo(expectedTopics.total());
  }
}
