package com.Messenger.Messenger.service;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class MailService {

	@Autowired
	private JavaMailSender mailSender;
	private Map<String, String> verificationCodes = new HashMap<>();

//	public void sendVerificationMail(String toEmail, String token) {
//		String subject = "회원가입 이메일 인증";
//		String link = "http://localhost:3000/register/signup/verify?token=" + token;
//		String body = "회원가입을 완료하려면 아래 링크를 클릭하세요:\n" + link;
//
//		SimpleMailMessage message = new SimpleMailMessage();
//		message.setTo(toEmail);
//		message.setSubject(subject);
//		message.setText(body);
//		message.setFrom("a941749200@gmail.com"); // 발신자 (너의 Gmail 주소)
//
//		mailSender.send(message);
//	}
//
//	public void sendFindIdMail(String toEmail, String token) {
//		String subject = "아이디 찾기 이메일 인증";
//		String link = "http://localhost:3000/find-id/verify?token=" + token; // 프론트 주소로 수정하세요
//		String body = "아이디 찾기 인증을 완료하려면 아래 링크를 클릭하세요:\n" + link;
//
//		SimpleMailMessage message = new SimpleMailMessage();
//		message.setTo(toEmail);
//		message.setSubject(subject);
//		message.setText(body);
//		message.setFrom("a941749200@gmail.com"); // 본인 이메일 주소로 설정
//
//		mailSender.send(message);
//	}

	public String generateAndSendCode(String email) {
		if (!isValidEmail(email)) {
			throw new IllegalArgumentException("잘못된 이메일 형식입니다.");
		}

		// 인증 코드 생성 (6자리 숫자)
		String code = String.format("%06d", new Random().nextInt(999999));

		// 이메일 본문
		String subject = "이메일 인증 코드";
		String body = "인증 코드: " + code + "\n해당 코드를 입력란에 입력해주세요.";

		// 메일 생성 및 발송
		SimpleMailMessage message = new SimpleMailMessage();
		message.setTo(email);
		message.setSubject(subject);
		message.setText(body);
		message.setFrom("a941749200@gmail.com"); // 본인 이메일로 설정
		mailSender.send(message);

		// 코드 저장 (테스트용 Map, 실제 서비스는 Redis 등 사용)
		verificationCodes.put(email, code);

		return code;
	}

	public boolean verifyCode(String email, String code) {
		String storedCode = verificationCodes.get(email);
		return storedCode != null && storedCode.equals(code);
	}

	public void removeCode(String email) {
		verificationCodes.remove(email);
	}

	private boolean isValidEmail(String email) {
		return email != null && email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$");
	}
}
