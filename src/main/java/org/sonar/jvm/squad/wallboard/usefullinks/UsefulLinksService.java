package org.sonar.jvm.squad.wallboard.usefullinks;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class UsefulLinksService {

  @Value("${squad.useful.links}")
  List<String> usefulLinks;

  public record Link(String link, String text) {
  }

  public List<Link> getLinks() {
    List<Link> links = new ArrayList<>();

    usefulLinks.forEach(text -> {
      String[] split = text.split("\\|");
      Link link = new Link(split[0], split[1]);
      links.add(link);
    });

    return links;
  }
}
