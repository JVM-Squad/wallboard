package org.sonar.jvm.squad.wallboard.usefullinks;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class UsefulLinksController {

  UsefulLinksService usefulLinksService;

  public UsefulLinksController(UsefulLinksService usefulLinksService) {
    this.usefulLinksService = usefulLinksService;
  }

  @GetMapping("/usefulLinks")
  public String getUsefulLinks(Model model) {
    model.addAttribute("links", usefulLinksService.getLinks());
    return "widgets/usefulLinks";
  }

}
