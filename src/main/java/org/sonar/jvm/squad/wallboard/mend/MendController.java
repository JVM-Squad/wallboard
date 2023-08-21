package org.sonar.jvm.squad.wallboard.mend;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class MendController {

  private final MendService mendService;

  public MendController(MendService mendService) {
    this.mendService = mendService;
  }

  @GetMapping("/mend")
  public String mend(Model model) {
    MendService.Login.Response login = mendService.login();
    model.addAttribute("alertSummary", mendService.getOrganizationAlertTypesSummary(login));
    model.addAttribute("librarySummary", mendService.getOrganizationVulnerableLibrarySummary(login));
    return "mend";
  }

}
