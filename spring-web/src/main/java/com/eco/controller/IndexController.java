package com.eco.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import lombok.extern.log4j.Log4j;

@Controller
@Log4j
public class IndexController {

	@GetMapping("/")
	public String mainPage() {
		return "index";
	}
}
