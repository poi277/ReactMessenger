package com.Messenger.Messenger.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.Messenger.Messenger.DTO.PasswordChangeDTO;
import com.Messenger.Messenger.DTO.PasswordResetRequestDTO;
import com.Messenger.Messenger.DTO.PasswordVerifyDTO;
import com.Messenger.Messenger.service.MessengerUserService;
import com.Messenger.Messenger.service.PasswordResetService;


@RestController
@RequestMapping("/password")
public class PasswordResetController {
	@Autowired
	private PasswordResetService passwordResetService;
	@Autowired
	private MessengerUserService messengerUserService;

	public PasswordResetController() {
	}

	@PostMapping("/code")
	public ResponseEntity<?> sendResetCode(@RequestBody PasswordResetRequestDTO dto) {
		if (messengerUserService.isOAuthUser(dto.getId())) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body("소셜로그인 유저입니다.");
		}
		try {
			passwordResetService.sendResetCode(dto.getId(), dto.getEmail());
			return ResponseEntity.status(HttpStatus.ACCEPTED).body("소셜로그인 유저입니다.");
		} catch (RuntimeException e) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
		}
	}

	@PostMapping("/verify")
	public ResponseEntity<?> verifyCode(@RequestBody PasswordVerifyDTO dto) {
		if (messengerUserService.isOAuthUser(dto.getEmail())) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body("소셜로그인 유저입니다.");
		}
		try {
			boolean result = passwordResetService.verifyResetCode(dto.getId(), dto.getEmail(), dto.getCode());
			if (result)
				return ResponseEntity.ok("인증 성공");
			else
				return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("코드가 올바르지 않거나 유효하지 않습니다.");
		} catch (RuntimeException e) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
		}
	}


	@PostMapping("/change")
	public ResponseEntity<?> resetPassword(@RequestBody PasswordChangeDTO dto) {
		if (dto.getNewPassword() == null || dto.getNewPassword().isEmpty()) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("비밀번호를 입력하지 않았습니다.");
		}
		if (!dto.getNewPassword().equals(dto.getConfirmPassword())) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("비밀번호가 같지 않습니다.");
		}

		try {
			passwordResetService.resetPassword(dto.getId(), dto.getNewPassword());
			return ResponseEntity.ok("비밀번호 변경 완료");
		} catch (RuntimeException e) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
		}
	}
}
