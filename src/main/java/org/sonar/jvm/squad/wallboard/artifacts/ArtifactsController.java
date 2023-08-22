package org.sonar.jvm.squad.wallboard.artifacts;

import java.util.List;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
public class ArtifactsController {
  ArtifactsService service;

  ArtifactsController(ArtifactsService service) {
    this.service = service;
  }

  @GetMapping("/artifacts-sync/{name}")
  public String getSync(@PathVariable String name, Model model) {
    List<Artifact> artifacts = service.findByName(name);
    model.addAttribute("analyzerName", name);
    model.addAttribute("artifacts", artifacts);
    return "artifacts-sync";
  }
}
