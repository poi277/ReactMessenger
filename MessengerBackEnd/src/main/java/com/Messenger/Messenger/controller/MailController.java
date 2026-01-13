package com.Messenger.Messenger.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.Messenger.Messenger.info.EmailRequest;
import com.Messenger.Messenger.info.VerificationRequest;
import com.Messenger.Messenger.service.MailService;
import com.Messenger.Messenger.service.MessengerUserService;

@RestController
@RequestMapping("/register")
public class MailController {

	@Autowired
	MessengerUserService messengerUserService;
	@Autowired
	MailService mailService;

	@PostMapping("/send-verification-code")
	public ResponseEntity<?> RegisterSendVerificationCode(@RequestBody EmailRequest request) {
		String email = request.getEmail();

		if (email == null || email.trim().isEmpty()) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("이메일은 필수입니다.");
		}
		if (messengerUserService.existsByEmail(request.getEmail())) {
			return ResponseEntity.status(HttpStatus.CONFLICT).body("해당 이메일로 이미 등록된 유저가 있습니다.");
		}
		try {
			String code = mailService.generateAndSendCode(email);
			return ResponseEntity.ok("인증 코드가 전송되었습니다.");
		} catch (IllegalArgumentException e) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("잘못된 이메일 형식");
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("이메일 전송 중 오류가 발생했습니다. 잠시 후 다시 시도해주세요.");
		}
	}

	@PostMapping("/verify-code")
	public ResponseEntity<?> RegisterVerifyCode(@RequestBody VerificationRequest request) {
		boolean isValid = mailService.verifyCode(request.getEmail(), request.getCode());
		if (isValid) {
			return ResponseEntity.ok("인증 성공");
		} else {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("인증 코드가 유효하지 않습니다.");
		}
	}

}
