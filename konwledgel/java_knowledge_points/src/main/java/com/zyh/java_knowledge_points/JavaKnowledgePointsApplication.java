package com.zyh.java_knowledge_points;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

@SpringBootApplication
public class JavaKnowledgePointsApplication {

	public static void main(String[] args) {
		SpringApplication.run(JavaKnowledgePointsApplication.class, args);
	}

	@RequestMapping
	public String index(Model model){
		System.out.println("index");
		model.addAttribute("welcome","Hello world!");
		return "hello";
	}

}
