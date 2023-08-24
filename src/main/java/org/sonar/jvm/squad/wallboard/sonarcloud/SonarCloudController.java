package org.sonar.jvm.squad.wallboard.sonarcloud;

import java.util.Set;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@Lazy
public class SonarCloudController {

  private static final Set<String> JVM_SQUAD_PLUGINS = Set.of("go", "jacoco", "java", "kotlin", "ruby", "sonarscala", "xml");

  private final SonarCloudService sonarCloudService;

  public SonarCloudController(SonarCloudService sonarCloudService) {
    this.sonarCloudService = sonarCloudService;
  }

  @GetMapping("/sonarcloud")
  public String mend(Model model) {
    model.addAttribute("plugins", sonarCloudService.getInstalledPlugins(JVM_SQUAD_PLUGINS));
    return "widgets/sonarcloud";
  }

}
