package org.sonar.jvm.squad.wallboard.sonarcloud;

import java.io.IOException;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.Test;
import org.sonar.jvm.squad.wallboard.client.RestConfig;
import org.sonar.jvm.squad.wallboard.sonarcloud.SonarCloudService.InstalledPlugins.Plugin;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ResourceLoader;
import org.springframework.test.web.client.MockRestServiceServer;

import static org.assertj.core.api.Assertions.assertThat;
import static org.sonar.jvm.squad.wallboard.TestUtils.expectGET;
import static org.sonar.jvm.squad.wallboard.TestUtils.jsonResponse;
import static org.springframework.test.web.client.MockRestServiceServer.bindTo;

@SpringBootTest(classes = {RestConfig.class, SonarCloudService.class})
class SonarCloudServiceTest {

  @Autowired
  SonarCloudService sonarCloudService;

  @Autowired
  ResourceLoader loader;

  @Test
  void plugin_versions() throws IOException {
    MockRestServiceServer server = bindTo(sonarCloudService.rest).build();

    expectGET(server, "https://sonarcloud.io/api/plugins/installed")
      .andRespond(jsonResponse(loader, "sonarcloud/api-plugins-installed.json"));

    List<Plugin> installedPlugins = sonarCloudService.getInstalledPlugins(Set.of("go", "jacoco", "java", "kotlin", "ruby", "sonarscala", "xml"));

    server.verify();
    assertThat(installedPlugins).hasSize(7);
    assertThat(installedPlugins).extracting(Plugin::filename).containsExactly(
      "sonar-go-plugin-1.13.0.4374.jar",
      "sonar-jacoco-plugin-1.3.0.1538.jar",
      "sonar-java-plugin-7.23.0.32023.jar",
      "sonar-kotlin-plugin-2.16.0.2832.jar",
      "sonar-ruby-plugin-1.13.0.4374.jar",
      "sonar-scala-plugin-1.13.0.4374.jar",
      "sonar-xml-plugin-2.9.0.4055.jar");
    assertThat(installedPlugins).extracting(Plugin::fileKeyAndVersion).containsExactly(
      "go 1.13.0.4374",
      "jacoco 1.3.0.1538",
      "java 7.23.0.32023",
      "kotlin 2.16.0.2832",
      "ruby 1.13.0.4374",
      "scala 1.13.0.4374",
      "xml 2.9.0.4055");
  }

}
