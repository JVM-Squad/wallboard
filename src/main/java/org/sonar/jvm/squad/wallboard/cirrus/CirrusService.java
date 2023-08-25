package org.sonar.jvm.squad.wallboard.cirrus;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import static org.sonar.jvm.squad.wallboard.client.JsonUtils.responseAs;
import static org.sonar.jvm.squad.wallboard.client.RestUtils.entityWithProperties;

@Service
@Lazy
public class CirrusService {

  private static final String API_URL = "https://api.cirrus-ci.com/graphql";

  public final RestTemplate rest;

  private final CirrusConfig.Credentials credentials;

  public CirrusService(RestTemplate rest, CirrusConfig.Credentials credentials) {
    this.rest = rest;
    this.credentials = credentials;
  }

  public List<Response> getCirrusData(Set<String> projectNames) {
    String data = """
      {
      "query": "query q($platform: String!, $owner: String!, $name: String!, $branch: String) {
                  ownerRepository(platform: $platform, owner: $owner, name: $name) {
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
                          branch
                          ...ErroredBuildStatus
                          ...FailedBuildStatus
                        }
                      }
                    }
                  }
                }
                fragment ErroredBuildStatus on Build {
                        notifications {
                          message
                          level
                        }
                }
                fragment FailedBuildStatus on Build {
                        tasks {
                          name
                          status
                          notifications {
                            message
                            level
                          }
                        }
                }
                ",
      "variables": {
         "platform": "github",
         "owner": "SonarSource",
         "name": "%s",
         "branch": "master" }
      }
      }
      """;
    List<Response> responseData = new ArrayList<>();
    projectNames.forEach(name -> {
      ResponseEntity<String> response = getResponse(data, name);
      responseData.add(responseAs(response, Response.class));
    });

    return responseData;
  }

  private ResponseEntity<String> getResponse(String data, String... params) {
    return rest.exchange(
      API_URL,
      HttpMethod.POST,
      getCirrusEntity(String.format(data, params)),
      String.class);
  }

  private <T> HttpEntity<T> getCirrusEntity(T body) {
    String cirrusToken = System.getenv("CIRRUS_CI_API_TOKEN");
    String cirrusCookie = credentials.cirrusCookie();
    if (cirrusToken != null && !cirrusToken.isEmpty()) {
      return entityWithProperties(body, Map.of("Authorization", "Bearer " + cirrusToken));
    } else if (cirrusCookie != null && !cirrusCookie.isEmpty()) {
      return entityWithProperties(body, Map.of("cookie", cirrusCookie));
    } else {
      throw new IllegalStateException("Missing CIRRUS_CI_API_TOKEN or CIRRUS_COOKIE environment variable.");
    }
  }

}
