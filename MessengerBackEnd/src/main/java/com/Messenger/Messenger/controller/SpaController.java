package com.Messenger.Messenger.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

//예: SpaController
@Controller
public class SpaController {

	@RequestMapping(value = "/{path:[^\\.]*}")
	public String redirect() {
		return "forward:/index.html";
	}
}
