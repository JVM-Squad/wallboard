package org.sonar.jvm.squad.wallboard.cirrus;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.sonar.jvm.squad.wallboard.client.JsonUtils;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import static org.sonar.jvm.squad.wallboard.client.JsonUtils.responseAs;
import static org.sonar.jvm.squad.wallboard.client.RestUtils.entityWithProperties;

@Service
public class CirrusService {

  private static final String API_URL = "https://api.cirrus-ci.com/graphql";

  public final RestTemplate rest;

  public CirrusService(RestTemplate rest) {
    this.rest = rest;
  }

  public List<Response> getCirrusData(Set<String> projectNames) {
    String data = """
      {
      "query": "query OwnerRepositoryQuery($platform: String!, $owner: String!, $name: String!, $branch: String) {
        ownerRepository(platform: $platform, owner: $owner, name: $name) {
          ...RepositoryBuildList_repository
          id
        }
      }

      fragment RepositoryBuildList_repository on Repository {
        id
        owner
        name
        builds(last: 1, branch: $branch) {
          edges {
            node {
              id
              changeMessageTitle
              durationInSeconds
              status
              ...BuildBranchNameChip_build
              ...BuildChangeChip_build
              ...BuildStatusChip_build
            }
          }
        }
      }

      fragment BuildBranchNameChip_build on Build {
        id
        branch
        repository {
          owner
          name
          id
        }
      }

      fragment BuildChangeChip_build on Build {
        id
        changeIdInRepo
      }

      fragment BuildStatusChip_build on Build {
        id
        status
        durationInSeconds
      }",
      "variables": {
         "platform": "github",
         "owner": "SonarSource",
         "name": "%s",
         "branch": "master" }
      }
      """;

    List<Response> responseData = new ArrayList<>();
    projectNames.forEach(name -> {
      ResponseEntity<String> response = getResponse(name, data);
      responseData.add(responseAs(response, Response.class));
    });

    return responseData;
  }

  public String getLastDefaultBranchBuild(String id) {
    String data = """
      {
      "query": "query q($id: ID!) { repository(id: $id) { lastDefaultBranchBuild { tasks { name status } } } }",
      "variables": {"id": %s }
      }
      """;

    ResponseEntity<String> response = getResponse(id, data);

    return JsonUtils.prettyPrint(response.getBody());
  }

  public String getBuild(String id) {
    String data = """
      {
      "query": "query q($id: ID!) { build(id: $id) { id branch status tasks { name status } } }",
      "variables": { "id": %s }
      }
      """;

    ResponseEntity<String> response = getResponse(id, data);

    return JsonUtils.prettyPrint(response.getBody());
  }

  private ResponseEntity<String> getResponse(String id, String data) {
    return rest.exchange(
      API_URL,
      HttpMethod.POST,
      getCirrusEntity(String.format(data, id)),
      String.class);
  }

  private <T> HttpEntity<T> getCirrusEntity(T body) {
    String cirrusToken = System.getenv("CIRRUS_CI_API_TOKEN");
    String cirrusCookie = System.getenv("CIRRUS_COOKIE");
    if (cirrusToken != null && !cirrusToken.isEmpty()) {
      return entityWithProperties(body, Map.of("Authorization", "Bearer " + cirrusToken));
    } else if (cirrusCookie != null && !cirrusCookie.isEmpty()) {
      return entityWithProperties(body, Map.of("cookie", cirrusCookie));
    } else {
      throw new IllegalStateException("Missing CIRRUS_CI_API_TOKEN or CIRRUS_COOKIE environment variable.");
    }
  }

}
