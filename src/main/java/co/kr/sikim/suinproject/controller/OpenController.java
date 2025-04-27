package co.kr.sikim.suinproject.controller;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;


@RestController
public class OpenController {
    @GetMapping("/")
    public String hello() {
        return "Hello Suin";
    }
    
}
