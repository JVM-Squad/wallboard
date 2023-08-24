package org.sonar.jvm.squad.wallboard;

import java.io.IOException;
import org.springframework.core.io.ResourceLoader;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.test.web.client.ResponseActions;
import org.springframework.test.web.client.response.DefaultResponseCreator;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.content;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.header;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.headerDoesNotExist;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

public class TestUtils {

  public static ResponseActions expectGET(MockRestServiceServer server, String expectedUri) {
    return server.expect(requestTo(expectedUri))
      .andExpect(method(HttpMethod.GET))
      .andExpect(header("Accept", "application/json"))
      .andExpect(headerDoesNotExist("Content-Type"))
      .andExpect(content().string(""));
  }

  public static ResponseActions expectPOST(MockRestServiceServer server, String expectedUri, String expectedBody) {
    return server.expect(requestTo(expectedUri))
      .andExpect(method(HttpMethod.POST))
      .andExpect(header("Content-Type", "application/json"))
      .andExpect(header("Accept", "application/json"))
      .andExpect(content().string(expectedBody));
  }

  public static DefaultResponseCreator jsonResponse(ResourceLoader loader, String path) throws IOException {
    return withSuccess(fromClasspath(loader, path), MediaType.APPLICATION_JSON);
  }

  public static String fromClasspath(ResourceLoader loader, String path) throws IOException {
    return loader.getResource("classpath:" + path).getContentAsString(UTF_8);
  }

}
