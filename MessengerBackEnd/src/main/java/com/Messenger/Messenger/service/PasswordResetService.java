package com.Messenger.Messenger.service;

import java.util.Optional;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.Messenger.Messenger.info.MessengerUser;
import com.Messenger.Messenger.repository.MessengerUserRepository;

@Service
public class PasswordResetService {

	private final MessengerUserRepository messengerUserRepository;
	private final MailService mailService;
	private PasswordEncoder passwordEncoder;

	public PasswordResetService(MessengerUserRepository messengerUserRepository, MailService mailService,
			PasswordEncoder passwordEncoder) {
		this.messengerUserRepository = messengerUserRepository;
		this.mailService = mailService;
		this.passwordEncoder = passwordEncoder;
	}

	// 인증 코드 발송
	public void sendResetCode(String Id, String email) {
		Optional<MessengerUser> userOpt = messengerUserRepository.findById(Id);
		if (userOpt.isEmpty()) {
			throw new RuntimeException("아이디가 존재하지 않습니다.");
		}
		boolean userEmailMatch = userOpt.isPresent() && email.equals(userOpt.get().getEmail());

		if (!userEmailMatch) {
			throw new RuntimeException("이메일이 일치하지 않습니다.");
		}
		mailService.generateAndSendCode(email);
	}


	// 인증 코드 검증
	public boolean verifyResetCode(String Id, String email, String code) {
		Optional<MessengerUser> userOpt = messengerUserRepository.findById(Id);
		if (userOpt.isEmpty()) {
			throw new RuntimeException("아이디가 존재하지 않습니다.");
		}
		boolean userEmailMatch = userOpt.isPresent() && userOpt.get().getEmail().equals(email);

		if (!userEmailMatch) {
			throw new RuntimeException("이메일이 일치하지 않습니다.");
		}

		return mailService.verifyCode(email, code);
	}

	// 비밀번호 재설정
	public void resetPassword(String userId, String newPassword) {
		Optional<MessengerUser> useropt = messengerUserRepository.findById(userId);

		if (useropt.isPresent()) {
			MessengerUser user = useropt.get();
			user.setPassword(passwordEncoder.encode(newPassword));
			messengerUserRepository.save(user);
			mailService.removeCode(user.getEmail());
		} else {
			throw new RuntimeException("해당 아이디를 가진 사용자가 존재하지 않습니다.");
		}
	}
}

