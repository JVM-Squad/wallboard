package org.sonar.jvm.squad.wallboard.mend;

import java.io.IOException;
import org.junit.jupiter.api.Test;
import org.sonar.jvm.squad.wallboard.client.JsonUtils;
import org.sonar.jvm.squad.wallboard.client.RestConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ResourceLoader;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.content;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.header;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

@SpringBootTest(classes = {MendServiceTest.TestMendConfig.class, RestConfig.class, MendService.class})
class MendServiceTest {

  @Configuration
  static class TestMendConfig {
    @Bean
    MendConfig.Credentials credentials() {
      return new MendConfig.Credentials(
        "2c255cad1bea5744ce44aac20b29dae3c6e6801a206cb726ebb41",
        "https://fake-api.mend.com",
        "paul.smith@sonarsource.com",
        "94d8168f091b7e2dd7ba7827d3f70e644925facb2169b3bcd5f4a");
    }
  }

  @Autowired
  MendService mendService;

  @Autowired
  ResourceLoader resourceLoader;

  @Test
  void test_login() throws IOException {
    assertThat(mendService).isNotNull();

    MendConfig.Credentials credentials = mendService.credentials;
    assertThat(credentials).isNotNull();
    assertThat(credentials.userEmail()).isEqualTo("paul.smith@sonarsource.com");

    MockRestServiceServer server = MockRestServiceServer.bindTo(mendService.rest).build();
    server.expect(requestTo("https://fake-api.mend.com/api/v2.0/login"))
      .andExpect(method(HttpMethod.POST))
      .andExpect(header("Content-Type", "application/json"))
      .andExpect(content().string(JsonUtils.removeIndentation("""
        {
          "email":"paul.smith@sonarsource.com",
          "orgToken":"2c255cad1bea5744ce44aac20b29dae3c6e6801a206cb726ebb41",
          "userKey":"94d8168f091b7e2dd7ba7827d3f70e644925facb2169b3bcd5f4a"
        }
        """)))
      .andRespond(withSuccess(fromClasspath("mend/login-response.json"), MediaType.APPLICATION_JSON));

    MendService.Login.Response login = mendService.login();

    server.verify();

    assertThat(login).isNotNull();
    MendService.Login.RetVal retVal = login.retVal();
    assertThat(retVal).isNotNull();
    assertThat(retVal.orgUuid()).isEqualTo("92ce3e6e-9057-693d-3239-13093a539784");
    assertThat(retVal.jwtToken()).isEqualTo("JniBeE28mGiQVhrkRvY6z.PhoJXWsWtVhEAjMsrTfSZFzR4TfcpVteLiJpMhXome9XxzjidWehPRh79pQfVuwg88dSyL8E3S6RbT5LDi");
  }

  String fromClasspath(String path) throws IOException {
    return resourceLoader.getResource("classpath:" + path).getContentAsString(UTF_8);
  }

}
