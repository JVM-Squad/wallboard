package org.sonar.jvm.squad.wallboard.community;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import static org.sonar.jvm.squad.wallboard.client.JsonUtils.responseAs;
import static org.sonar.jvm.squad.wallboard.client.RestUtils.headersForGET;

@Service
@Lazy
public class CommunityService {
  public final RestTemplate rest;
  private final CommunityConfig.Credentials credentials;

  public CommunityService(CommunityConfig.Credentials communityCredentials, RestTemplate rest) {
    this.credentials = communityCredentials;
    this.rest = rest;
  }

  interface CommunityData {
    record Member(int id, String username) {
    }
    record Topic(int id, String title, String assignTo) {
    }
    record AssignedTopics(String assignTo, long number) {
    }
    record AssignedGroupTopics(int total, List<AssignedTopics> assignedTopics) {
    }

  }

  private static CommunityData.Topic jsonToTopic(Map<String, Object> jsonTopic) {
    int id = (int) jsonTopic.get("id");
    String title = (String) jsonTopic.get("title");
    String assignTo = "error : owner of the topic not found";

    if (jsonTopic.containsKey("assigned_to_user")) {
      assignTo = (String) ((Map<?, ?>) jsonTopic.get("assigned_to_user")).get("name");
    } else if (jsonTopic.containsKey("assigned_to_group")) {
      assignTo = "unassigned";
    }

    return new CommunityData.Topic(id, title, assignTo);
  }

  public CommunityData.AssignedGroupTopics assignedTopics(String group) {

    ResponseEntity<String> response = rest.exchange(
      "https://community.sonarsource.com/topics/group-topics-assigned/" + group + ".json",
      HttpMethod.GET,
      headersForGET(Map.of("cookie", "_t=" + credentials.discourseToken())),
      String.class);

    Map<?, ?> json = responseAs(response, Map.class);
    List<Map<String, Object>> jsonTopics = (List<Map<String, Object>>) ((Map) json.get("topic_list")).get("topics");
    List<CommunityData.Topic> topics = jsonTopics
      .stream()
      .map(CommunityService::jsonToTopic)
      .toList();

    int total = topics.size();
    List<CommunityData.AssignedTopics> assignedTopics = topics
      .stream()
      .collect(Collectors.groupingBy(topic -> topic.assignTo, Collectors.counting()))
      .entrySet()
      .stream().map(entry -> new CommunityData.AssignedTopics(entry.getKey(), entry.getValue()))
      .toList();

    return new CommunityData.AssignedGroupTopics(total, assignedTopics);
  }

}
