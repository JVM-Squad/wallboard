package org.sonar.jvm.squad.wallboard.github;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class GithubService {

  public final RestTemplate rest;

  public GithubService(RestTemplate rest){
    this.rest = rest;
  }

  @JsonIgnoreProperties(ignoreUnknown = true)
  public record Release(String repoName, String name, String published_at) {
  }

  public Release getRelease(String repo){
    Release release = rest.getForObject(
      "https://api.github.com/repos/sonarsource/"+repo+"/releases/latest",
      Release.class
    );
    return new Release(repo, release.name, release.published_at);
  }
}
