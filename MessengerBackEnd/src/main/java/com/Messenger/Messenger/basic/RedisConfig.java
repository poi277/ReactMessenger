//package com.Messenger.Messenger.basic;
//
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
//import org.springframework.data.redis.connection.lettuce.LettuceClientConfiguration;
//import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
//import org.springframework.session.data.redis.config.annotation.web.http.EnableRedisHttpSession;
//
//@Configuration
//@EnableRedisHttpSession(maxInactiveIntervalInSeconds = 300)
//public class RedisConfig {
//
//	@Value("${spring.redis.host}")
//	private String host;
//
//	@Value("${spring.redis.port}")
//	private int port;
//
//	@Bean
//	public LettuceConnectionFactory redisConnectionFactory() {
//		RedisStandaloneConfiguration redisConfig = new RedisStandaloneConfiguration(host, port);
//		LettuceClientConfiguration clientConfig = LettuceClientConfiguration.builder().useSsl()
//				.disablePeerVerification() // 필요시
//				.build();
//		return new LettuceConnectionFactory(redisConfig, clientConfig);
//	}
//}
//
