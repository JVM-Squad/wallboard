package org.sonar.jvm.squad.wallboard.client;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.ResponseEntity;

public final class JsonUtils {

  public static final ObjectMapper MAPPER = new ObjectMapper()
    .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

  private JsonUtils() {
    // utility class
  }

  public static String prettyPrint(String badlyFormattedJson) {
    try {
      return MAPPER.readTree(badlyFormattedJson).toPrettyString();
    } catch (JsonProcessingException e) {
      return badlyFormattedJson;
    }
  }

  public static String removeIndentation(String badlyFormattedJson) {
    try {
      return MAPPER.readTree(badlyFormattedJson).toString();
    } catch (JsonProcessingException e) {
      return badlyFormattedJson;
    }
  }

  public static <T> T responseAs(ResponseEntity<String> response, Class<T> valueType) {
    if (response.getStatusCode().is2xxSuccessful()) {
      try {
        return MAPPER.readValue(response.getBody(), valueType);
      } catch (JsonProcessingException e) {
        throw new IllegalStateException(
          "JsonProcessingException for " + valueType.getName() + " exception " + e.getMessage() + " from " + JsonUtils.prettyPrint(response.getBody()));
      }
    }
    throw new IllegalStateException("Invalid status code " + response.getStatusCode() + ": " + JsonUtils.prettyPrint(response.getBody()));
  }

}
