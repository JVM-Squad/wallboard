package org.sonar.jvm.squad.wallboard.sonarcloud;

import java.util.Comparator;
import java.util.List;
import java.util.Set;
import org.sonar.jvm.squad.wallboard.sonarcloud.SonarCloudService.InstalledPlugins.Plugin;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import static org.sonar.jvm.squad.wallboard.client.JsonUtils.responseAs;
import static org.sonar.jvm.squad.wallboard.client.RestUtils.headersForGET;

@Service
@Lazy
public class SonarCloudService {

  public static final String END_POINT = "https://sonarcloud.io/";

  public final RestTemplate rest;

  public SonarCloudService(RestTemplate rest) {
    this.rest = rest;
  }

  interface InstalledPlugins {
    record Response(List<Plugin> plugins) {
    }
    record Plugin(String key, String name, String description, String version, String license, String organizationName,
      String organizationUrl, String homepageUrl, String issueTrackerUrl, String implementationBuild,
      String documentationPath, Long updatedAt, String filename, Boolean sonarLintSupported, String hash) {
      public String fileKey() {
        return filename.replaceFirst("^sonar-", "").replaceFirst("-plugin-.*$", "");
      }

      public String fileVersion() {
        return filename.replaceFirst("^.*-plugin-", "").replaceFirst("\\.jar$", "");
      }

      public String fileKeyAndVersion() {
        return fileKey() + " " + fileVersion();
      }
    }
  }

  public List<Plugin> getInstalledPlugins(Set<String> pluginNamesFilter) {
    ResponseEntity<String> response = rest.exchange(
      END_POINT + "api/plugins/installed", HttpMethod.GET, headersForGET(), String.class);
    return responseAs(response, InstalledPlugins.Response.class).plugins().stream()
      .filter(plugin -> pluginNamesFilter.contains(plugin.key()))
      .sorted(Comparator.comparing(Plugin::filename))
      .toList();
  }

}
