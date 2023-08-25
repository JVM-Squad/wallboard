package org.sonar.jvm.squad.wallboard.github;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class GithubController {

  private final GithubService githubService;

  public GithubController(GithubService githubService){
    this.githubService = githubService;
  }

  private record Repo(String displayName, String name){}

  @GetMapping("/github")
  public String mend(Model model)  {
      var releases = githubRepos()
      .stream()
      .map(repo ->  githubService.getReleaseAsync(repo.displayName, repo.name))
      .toList();



    model.addAttribute("formatter", DateTimeFormatter.ofPattern("d-MMM-yyyy"));
    model.addAttribute("sonarJavaReleases", releases.stream().map(CompletableFuture::join).toList());
    return "widgets/github";
  }

  private static List<Repo> githubRepos() {
    return List.of(new Repo("java", "sonar-java"), new Repo("go,ruby,scala,apex","slang-enterprise"), new Repo("kotlin", "sonar-kotlin"), new Repo("xml", "sonar-xml"),
      new Repo("jacoco", "sonar-jacoco"), new Repo("maven", "sonar-scanner-maven"), new Repo("gradle", "sonar-scanner-gradle"));
  }

}
