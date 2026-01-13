package com.Messenger.Messenger.jwt;

import java.security.Key;
import java.util.Date;

import org.springframework.security.oauth2.jwt.JwtException;
import org.springframework.stereotype.Component;

import com.Messenger.Messenger.info.MessengerUser;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

@Component
public class JwtUtil {

	private static final String SECRET_KEY = "mysecretkeymysecretkeymysecretkey12"; // 최소 32바이트
	private static final long EXPIRATION_TIME = 1000 * 60 * 60; // 1시간
	private static final long Hour24 = 1000 * 60 * 60 * 24;

	private final Key key = Keys.hmacShaKeyFor(SECRET_KEY.getBytes());



	public String generatedToken(MessengerUser user, final long Time, String roles) {
		JwtBuilder builder = Jwts.builder().setSubject(String.valueOf(user.getId())).claim("Id", user.getId())
				.claim("Name", user.getName())
				.claim("roles", roles) // 👈 역할 부여
				.setIssuedAt(new Date())
				// .setExpiration(new Date(System.currentTimeMillis() + Time))
				.signWith(key, SignatureAlgorithm.HS256);
		if (Time > 0) {
			builder.setExpiration(new Date(System.currentTimeMillis() + Time));
		}
		return builder.compact();
	}

	// 어드민 토큰 발급 생성
	public String generateTokenStudentsMainHomePage(MessengerUser student) {
		return generatedToken(student, EXPIRATION_TIME, "USER");
	}


	// 🔹 토큰에서 기본키 추출
	public String extractUsername(String token) {
		return parseClaims(token).getSubject();
	}

	// 🔹 토큰 유효성 검사
	public boolean validateToken(String token) {
		try {
			parseClaims(token);
			return true;
		} catch (JwtException | IllegalArgumentException e) {
			return false;
		}
	}

	// 🔹 Claims 추출 (재사용)
	private Claims parseClaims(String token) {
		return Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token).getBody();
	}

	public int extractStudentId(String token) {
		return parseClaims(token).get("studentId", Integer.class);
	}

	public String extractStudentName(String token) {
		return parseClaims(token).get("studentName", String.class);
	}

	public String extractSubject(String token) {
		return Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token).getBody().getSubject();
	}


//					StdentsInfo student = studentsRepository.findById(Integer.parseInt(request.getUsername())).get();
//토큰 생성시 사용예 	String token = jwtUtil.generateToken(student);
}