package org.sonar.jvm.squad.wallboard;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@SpringBootApplication
@Controller
public class WallboardApplication {

  public static void main(String[] args) {
    SpringApplication.run(WallboardApplication.class, args);
  }

  @GetMapping("/hello")
  public String hello(@RequestParam(value = "myName", defaultValue = "Dorian") String name, Model model) {
    model.addAttribute("name", name);
    return "hello";
  }
}

