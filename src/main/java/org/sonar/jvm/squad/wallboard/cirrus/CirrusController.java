package org.sonar.jvm.squad.wallboard.cirrus;

import java.util.Set;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class CirrusController {

  public static final String WIDGETS_CIRRUS = "widgets/cirrus";
  private final CirrusService cirrusService;

  public CirrusController(CirrusService cirrusService) {
    this.cirrusService = cirrusService;
  }

  @GetMapping("/cirrus")
  public String getCirrusData(@RequestParam(value = "projectNames", defaultValue = "sonar-java,sonar-kotlin,slang,go") Set<String> projectNames, Model model) {
    model.addAttribute("data", cirrusService.getCirrusData(projectNames));
    return WIDGETS_CIRRUS;
  }

  @GetMapping("/lastDefaultBranchBuild")
  public String getLastDefaultBranchBuild(@RequestParam(value = "id", defaultValue = "6321405351690240") String id, Model model) {
    model.addAttribute("data", cirrusService.getLastDefaultBranchBuild(id));
    return WIDGETS_CIRRUS;
  }

  @GetMapping("/build")
  public String getBuild(@RequestParam(value = "id", defaultValue = "6380171293360128") String id, Model model) {
    model.addAttribute("data", cirrusService.getBuild(id));
    return WIDGETS_CIRRUS;
  }

}
