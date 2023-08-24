package org.sonar.jvm.squad.wallboard.cirrus;

import java.util.List;

public record Response(Data data) {

  public record Data(OwnerRepository ownerRepository) {
  }

  public record OwnerRepository(
    String id,
    String owner,
    String name,
    Builds builds) {
  }

  public record Builds(List<Edge> edges) {
  }

  public record Edge(Node node) {
  }

  public record Node(
    String id,
    String changeMessageTitle,
    int durationInSeconds,
    String status,
    String branch,
    Repository repository,
    String changeIdInRepo) {
  }

  public record Repository(
    String owner,
    String name,
    String id) {
  }

}
