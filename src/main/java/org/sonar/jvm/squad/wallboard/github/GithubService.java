package org.sonar.jvm.squad.wallboard.github;

import java.time.ZonedDateTime;
import java.util.concurrent.CompletableFuture;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import static org.sonar.jvm.squad.wallboard.client.JsonUtils.responseAs;
import static org.sonar.jvm.squad.wallboard.client.RestUtils.headersForGET;

@Service
public class GithubService {

  public final RestTemplate rest;
  private final GithubConfig.Credentials credentials;

  public GithubService(GithubConfig.Credentials credentials, RestTemplate rest) {
    this.rest = rest;
    this.credentials = credentials;
  }

  interface ReleaseSummary {
    record Response(String name, String published_at) {
    }
    record Release(String repoName, String displayName, String releaseNumber, ZonedDateTime date) {
    }
  }

  @Async
  public CompletableFuture<ReleaseSummary.Release> getReleaseAsync(String displayName, String repo) {
    return CompletableFuture.completedFuture(getRelease(displayName, repo));
  }

  public ReleaseSummary.Release getRelease(String displayName, String repo) {
    ResponseEntity<String> response = rest.exchange(
      "https://api.github.com/repos/sonarsource/" + repo + "/releases/latest",
      HttpMethod.GET,
      headersForGET(credentials.githubToken()),
      String.class);

    ReleaseSummary.Response release = responseAs(response, ReleaseSummary.Response.class);

    return new ReleaseSummary.Release(
      repo,
      displayName,
      release.name,
      ZonedDateTime.parse(release.published_at));
  }
}
