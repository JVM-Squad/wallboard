package org.sonar.jvm.squad.wallboard;

import java.util.List;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@SpringBootApplication
@Controller
public class WallboardApplication {

  public static void main(String[] args) {
    SpringApplication.run(WallboardApplication.class, args);
  }

  @GetMapping({"/"})
  public String index(Model model) {
    model.addAttribute("widgetRowsAndColsUrls", List.of(
      List.of("/mend", "/todo"),
      List.of("/todo", "/todo")
    ));
    return "index";
  }

  @GetMapping("/todo")
  public String todo() {
    return "widgets/todo";
  }

}
