package com.Messenger.Messenger.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.session.web.http.CookieHttpSessionIdResolver;
import org.springframework.session.web.http.CookieSerializer;
import org.springframework.session.web.http.DefaultCookieSerializer;
	
@Configuration
public class SessionCookieConfig {

	// 세션 쿠키 설정
	@Bean
	public CookieSerializer cookieSerializer() {
		DefaultCookieSerializer serializer = new DefaultCookieSerializer();
		serializer.setCookieName("SESSION");
		serializer.setUseHttpOnlyCookie(true);

		// HTTPS 환경
		serializer.setUseSecureCookie(true);
		serializer.setSameSite("None");

		// 세션 종료 시 삭제
		serializer.setCookieMaxAge(-1);
		return serializer;
	}



	@Bean
	public CookieHttpSessionIdResolver httpSessionIdResolver() {
		CookieHttpSessionIdResolver resolver = new CookieHttpSessionIdResolver();
		resolver.setCookieSerializer(cookieSerializer());
		return resolver;
	}

}

