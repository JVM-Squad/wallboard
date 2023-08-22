package org.sonar.jvm.squad.wallboard.mend;

import java.io.IOException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@SpringBootTest(classes = {MendServiceTest.TestMendConfig.class, MendService.class})
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

    @Bean
    RestTemplate restTemplate() {
      return mock(RestTemplate.class);
    }
  }

  @Autowired
  MendService mendService;

  @Value("classpath:mend/login-response.json")
  Resource loginResponse;

  @Test
  void test_login() throws IOException {
    assertThat(mendService).isNotNull();

    MendConfig.Credentials credentials = mendService.credentials;
    assertThat(credentials).isNotNull();
    assertThat(credentials.userEmail()).isEqualTo("paul.smith@sonarsource.com");

    RestTemplate rest = mendService.rest;

    when(rest.exchange(
      eq("https://fake-api.mend.com/api/v2.0/login"),
      eq(HttpMethod.POST),
      any(),
      eq(String.class)))
      .thenReturn(new ResponseEntity<>(loginResponse.getContentAsString(UTF_8), HttpStatus.OK));

    MendService.Login.Response login = mendService.login();

    assertThat(login).isNotNull();
    MendService.Login.RetVal retVal = login.retVal();
    assertThat(retVal).isNotNull();
    assertThat(retVal.orgUuid()).isEqualTo("92ce3e6e-9057-693d-3239-13093a539784");
    assertThat(retVal.jwtToken()).isEqualTo("JniBeE28mGiQVhrkRvY6z.PhoJXWsWtVhEAjMsrTfSZFzR4TfcpVteLiJpMhXome9XxzjidWehPRh79pQfVuwg88dSyL8E3S6RbT5LDi");
  }

}
