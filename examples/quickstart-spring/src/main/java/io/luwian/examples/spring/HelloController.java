
package io.luwian.examples.spring;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
class HelloController {
  @GetMapping("/hello")
  String hello() {
    return "Hello from Luwian (Spring scaffold).";
  }
}
