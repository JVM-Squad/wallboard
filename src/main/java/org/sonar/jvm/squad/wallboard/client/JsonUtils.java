package org.sonar.jvm.squad.wallboard.client;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class JsonUtils {

  public static String prettyPrint(String badlyFormattedJson) {
    try {
      return new ObjectMapper().readTree(badlyFormattedJson).toPrettyString();
    } catch (JsonProcessingException e) {
      return badlyFormattedJson;
    }
  }

}
