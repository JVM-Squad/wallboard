package org.sonar.jvm.squad.wallboard.artifacts;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
@Lazy
public class ArtifactsService {
  RestTemplate restTemplate;

  ArtifactsService(RestTemplate restTemplate) {
    this.restTemplate = restTemplate;
  }

  public List<Artifact> all() {
    return List.of(
      new Artifact(
        "sonar-java",
        "7.24.0.32100",
        BuildType.RELEASE,
        "https://repox.jfrog.io/artifactory/sonarsource/org/sonarsource/java/sonar-java-plugin/7.24.0.32100/sonar-java-plugin-7.24.0.32100.jar"
      ),
      new Artifact(
        "sonar-java",
        "7.25.0.32116",
        BuildType.MAIN,
        "https://repox.jfrog.io/repox/sonarsource/org/sonarsource/java/sonar-java-plugin/7.25.0.32116/sonar-java-plugin-7.25.0.32116.jar"
      ),
      new Artifact(
        "sonar-java",
        "7.25.0.32117",
        BuildType.DOGFOOD,
        "https://repox.jfrog.io/repox/sonarsource/org/sonarsource/java/sonar-java-plugin/7.25.0.32117/sonar-java-plugin-7.25.0.32117.jar"
      )

    );
  }

  public List<Artifact> findByName(String name) {
    return all().stream()
      .filter(artifact -> artifact.analyzer().equals(name))
      .collect(Collectors.toList());
  }
}
