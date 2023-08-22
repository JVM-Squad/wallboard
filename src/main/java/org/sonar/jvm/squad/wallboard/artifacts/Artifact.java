package org.sonar.jvm.squad.wallboard.artifacts;

enum BuildType {
  MAIN,
  DOGFOOD,
  RELEASE
}

public record Artifact(String analyzer, String version, BuildType buildType, String url) {
}
