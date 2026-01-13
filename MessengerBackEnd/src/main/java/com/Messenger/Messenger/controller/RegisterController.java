package com.Messenger.Messenger.controller;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.Messenger.Messenger.DTO.DuplicateCheckIdDTO;
import com.Messenger.Messenger.basic.PasswordConfirm;
import com.Messenger.Messenger.info.MessengerUser;
import com.Messenger.Messenger.repository.MessengerUserRepository;

@RestController
@RequestMapping("/register")
public class RegisterController {

	@Autowired
	MessengerUserRepository messengerUserRepository;

	@PostMapping("/id")
	public ResponseEntity<?> DuplicateId(@RequestBody DuplicateCheckIdDTO id) {
		String trimmedId = id.getId().trim();
		if (trimmedId.isEmpty()) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("아이디를 입력해주세요.");
		}
		if (trimmedId.contains(" ")) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("아이디에는 공백을 포함할 수 없습니다.");
		}
		Optional<MessengerUser> user = messengerUserRepository.findById(trimmedId);
		if (user.isEmpty()) {
			return ResponseEntity.status(HttpStatus.ACCEPTED).body("가능한 아이디입니다.");
		}
		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("중복된 아이디입니다.");
	}


	@PostMapping("/password")
	public ResponseEntity<?> confirmPassword(@RequestBody PasswordConfirm dto) {
		String password = dto.getPassword();
		if (password.isEmpty()) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("비밀번호를 입력해주세요.");
		}
		if (password.contains(" ")) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("공백을 제거해주세요.");
		}

		if (dto.getPassword().equals(dto.getConfirmPassword())) {
			return ResponseEntity.status(HttpStatus.ACCEPTED).body("비밀번호가 일치합니다.");
		}
		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("비밀번호가 서로 다릅니다.");
	}
}
