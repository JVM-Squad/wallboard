package org.sonar.jvm.squad.wallboard.community;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class CommunityController {
  @Value("${squad.community.group}")
  String communityGroup;

  private final CommunityService communityService;

  public CommunityController(CommunityService service){
    this.communityService = service;
  }


  @GetMapping("/community")
  public String members(Model model){
    CommunityService.CommunityData.AssignedGroupTopics assignedTopics= communityService.assignedTopics(communityGroup);
    model.addAttribute("assignedTopics", assignedTopics);
    return "widgets/community";
  }

}
