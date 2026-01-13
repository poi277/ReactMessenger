package com.Messenger.Messenger.basic;

import org.springframework.context.annotation.Configuration;

@Configuration
public class BaseURLCollector {
//	public static final String securityselect = "JwtCookie";
	public static final String securityselect = "session";

	public static final String baseFrontendurl = "https://localhost:3000"; // 개발용
	public static final String basebackendurl = "http://localhost:5000"; // 개발용

//	public static final String baseFrontendurl = "https://poimessenger.com";
//	public static final String basebackendurl = "https://api.poimessenger.com";
	// // 배포용
}