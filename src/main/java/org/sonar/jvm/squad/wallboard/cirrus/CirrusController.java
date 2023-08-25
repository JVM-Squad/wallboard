package org.sonar.jvm.squad.wallboard.cirrus;

import java.util.Set;

import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@Lazy
public class CirrusController {

  public static final String WIDGETS_CIRRUS = "widgets/cirrus";
  private final CirrusService cirrusService;

  public CirrusController(CirrusService cirrusService) {
    this.cirrusService = cirrusService;
  }

  @GetMapping("/cirrus")
  public String getCirrusData(@RequestParam(value = "projectNames",
    defaultValue = "sonar-java,sonar-kotlin,slang-enterprise,sonar-xml,sonar-jacoco,sonar-scanner-maven,sonar-scanner-gradle") Set<String> projectNames, Model model) {
    model.addAttribute("data", cirrusService.getCirrusData(projectNames));
    return WIDGETS_CIRRUS;
  }

}
