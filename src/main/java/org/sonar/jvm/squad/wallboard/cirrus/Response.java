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
    List<Notification> notifications,
    List<Task> tasks) {

    public String duration() {
      int hours = durationInSeconds / 3600;
      int minutes = (durationInSeconds % 3600) / 60;
      int remainingSeconds = durationInSeconds % 60;

      String formattedTime = "";
      if (hours > 0) {
        formattedTime += hours + " hours, ";
      }
      if (minutes > 0 || hours > 0) {
        formattedTime += minutes + " minutes, ";
      }
      formattedTime += remainingSeconds + " seconds";

      return formattedTime;
    }
  }

  public record Notification(String message, String level) {
  }

  public record Task(String name, String status) {
  }

}
