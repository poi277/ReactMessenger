package com.Messenger.Messenger.basic;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import com.Messenger.Messenger.service.MessengerSessionService;


//✅ 3. Security 설정 클래스 수정
@Configuration
@EnableWebSecurity
public class BasicAuthenticationSecurityConfiguration {

	private final UserDetailsService userDetailsService;
	@Autowired
	private MessengerSessionService messengerSessionService;
	@Autowired
	private CustomOAuth2UserService customOAuth2UserService; // 추가

	public BasicAuthenticationSecurityConfiguration(UserDetailsService userDetailsService) {
		this.userDetailsService = userDetailsService;
	}
	@Bean
	public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
	    return http
				.cors().and().csrf(csrf -> csrf.disable())
				.authorizeHttpRequests(auth -> auth.requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
						// .requestMatchers("/userlogout").hasRole("USER")
						.requestMatchers("/userlogout", "/userlogin", "/register", "/register/**",
								"/allpostlist", "/uploads/**",
								"/post/**", "/comment/{postid}", "/userpostlist/**", "/sessioncheck",
								"/post/{postId}/like", "/searchName", "/find-id/**",
								"/password/**", "/hello", "/profile/**")
						.permitAll().anyRequest().authenticated())
				.sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED))
				.headers(headers -> headers.frameOptions().disable())
				.oauth2Login(oauth2 -> oauth2
					    .userInfoEndpoint(userInfo -> userInfo.userService(customOAuth2UserService))
						.defaultSuccessUrl(BaseURLCollector.baseFrontendurl + "/", true))
				.build();
	}

	@Bean
	public CorsConfigurationSource corsConfigurationSource() {
		CorsConfiguration configuration = new CorsConfiguration();
		configuration.addAllowedOriginPattern(BaseURLCollector.baseFrontendurl);
		configuration.addAllowedMethod("*");
		configuration.addAllowedHeader("*");
		configuration.setAllowCredentials(true);
		UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
		source.registerCorsConfiguration("/**", configuration);
		return source;
	}

	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	@Bean
	public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
		return config.getAuthenticationManager();
	}
}

