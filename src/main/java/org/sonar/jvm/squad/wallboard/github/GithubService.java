package org.sonar.jvm.squad.wallboard.github;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class GithubService {

  private final RestTemplate rest;
  private final GithubConfig.Credentials credentials;

  public GithubService(GithubConfig.Credentials credentials, RestTemplate rest){
    this.rest = rest;
    this.credentials = credentials;
  }

  @JsonIgnoreProperties(ignoreUnknown = true)
  public record Release(String repoName, String name, String published_at) {
  }

  public Release getRelease(String repo){
    Release release = rest.exchange(
      "https://api.github.com/repos/sonarsource/"+repo+"/releases/latest",
      HttpMethod.GET,
      new HttpEntity<>(headers(credentials.githubToken())),
      Release.class).getBody();

    return new Release(repo, release.name, release.published_at);
  }

  private static HttpHeaders headers(@Nullable String bearerToken) {
    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_JSON);
    if (bearerToken != null) {
      headers.setBearerAuth(bearerToken);
    }
    return headers;
  }
}
