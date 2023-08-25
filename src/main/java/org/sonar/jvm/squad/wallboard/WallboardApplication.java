package org.sonar.jvm.squad.wallboard;

import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@SpringBootApplication
@Controller
@EnableAsync
public class WallboardApplication {

  public static void main(String[] args) {
    SpringApplication.run(WallboardApplication.class, args);
  }

  @GetMapping({"/"})
  public String index(Model model) {
    model.addAttribute("widgetRowsAndColsUrls", List.of(
      List.of("/mend", "/github"),
      List.of("/sonarcloud", "/cirrus")
    ));
    return "index";
  }

  @GetMapping("/todo")
  public String todo() {
    return "widgets/todo";
  }

  @Bean
  public Executor taskExecutor() {
    return Executors.newCachedThreadPool();
  }

}
