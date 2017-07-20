package com.sonatype.hello;

import java.util.Date;
import java.util.Random;

import com.sonatype.hello.dto.FooDto;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Just some code to prevent a similar match with other bare projects
 */
@Controller
public class FooController
{
  private final FooSleeper fooSleeper;

  public FooController() {
    this.fooSleeper = new FooSleeper(new Random().nextInt());
  }

  @RequestMapping("/unique")
  public String unique(Model model) throws InterruptedException {
    someMethod();
    model.addAttribute(new FooDto("foo", "bar", new Date()));
    return "unique";
  }

  private void someMethod() throws InterruptedException {
    for (int i = 0; i < 100; i++) {
      sleep(i);
    }
  }

  protected void sleep(final int milliseconds) throws InterruptedException {
    fooSleeper.sleep(milliseconds);
  }
}
