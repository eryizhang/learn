package com.springl.learn.thymleaf;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping("/hello")
@Controller
public class WebController {

    @RequestMapping("/main")
    public String hello(@ModelAttribute(value = "formBean") FormBean formBean) {
        return "hello";

    }

    @RequestMapping("/send")
    public String send(@ModelAttribute(value = "formBean") FormBean formBean) {

        System.out.println(formBean.getAddrs().length);
        return "hello";

    }
}
