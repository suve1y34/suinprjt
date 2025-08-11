package co.kr.sikim.suinproject.controller;

import org.springframework.web.bind.annotation.RestController;

import co.kr.sikim.suinproject.mapper.TestMapper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;


@RestController
public class OpenController {
    @Autowired
    private final TestMapper testMapper;

    public OpenController(TestMapper testMapper) {
        this.testMapper = testMapper;
    }
    
    @GetMapping("/")
    public String hello() {
        return "Hello Suin";
    }
}
