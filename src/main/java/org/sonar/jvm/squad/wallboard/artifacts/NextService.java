package org.sonar.jvm.squad.wallboard.artifacts;

import java.util.List;
import java.util.Map;

import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
@Lazy
public class NextService {
  public static final String HOST = "https://next.sonarqube.com/sonarqube";
  public static final String PLUGINS_INSTALLED_ENDPOINT = HOST + "/api/plugins/installed";

  private static final Map<String, String> ANALYZER_TO_PLUGIN_KEY = Map.of(
    "sonar-java", "java"
  );
  public final RestTemplate rest;
  private String sonarQubeToken;

  NextService(RestTemplate rest, String sonarQubeToken) {
    this.rest = rest;
    this.sonarQubeToken = sonarQubeToken;
  }

  public String getInstalledVersion(String name) {
    String pluginKey = ANALYZER_TO_PLUGIN_KEY.get(name.trim().toLowerCase());
    if (pluginKey == null) {
      throw new IllegalArgumentException("Could not match analyzer name with known plugin (" + name + ")");
    }

    HttpHeaders headers = new HttpHeaders();
    headers.setBearerAuth(sonarQubeToken);
    HttpEntity<Void> requestEntity = new HttpEntity<>(headers);

    ResponseEntity<Map> response = rest.exchange(
      PLUGINS_INSTALLED_ENDPOINT,
      HttpMethod.GET,
      requestEntity,
      Map.class
    );
    Map<String, Object> body = response.getBody();
    if (body == null) {
      return "N/A";
    }
    List<Object> plugins = (List<Object>) body.get("plugins");
    return plugins.stream()
      .map(plugin -> (Map<String, String>) plugin)
      .filter(plugin -> pluginKey.equals(plugin.get("key")))
      .findFirst()
      .map(plugin -> plugin.get("version"))
      .orElse(null);
  }
}
