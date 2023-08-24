package org.sonar.jvm.squad.wallboard.github;

import java.util.List;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class GithubController {

  private final GithubService githubService;

  public GithubController(GithubService githubService){
    this.githubService = githubService;
  }

  @GetMapping("/github")
  public String mend(Model model) {
    List<GithubService.ReleaseSummary.Release> releases = githubRepos().stream().map(githubService::getRelease).toList();
    model.addAttribute("sonarJavaReleases", releases);
    return "widgets/github";
  }

  private static List<String> githubRepos(){
    return List.of("sonar-java", "slang-enterprise", "sonar-kotlin", "sonar-xml", "sonar-jacoco", "sonar-scanner-maven", "sonar-scanner-gradle");
  }

}
