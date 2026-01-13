package com.Messenger.Messenger.service;

import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.Messenger.Messenger.repository.MessengerUserRepository;

@Service
public class FindIdService {

    private Map<String, VerificationCodeInfo> codeStore = new ConcurrentHashMap<>();

    @Autowired
    private JavaMailSender mailSender;
	@Autowired
	private MessengerUserService messengerUserService;
	@Autowired
	private MessengerUserRepository messengerUserRepository;

	private final PasswordEncoder passwordEncoder;

	public FindIdService(PasswordEncoder passwordEncoder) {
		this.passwordEncoder = passwordEncoder;
	}

    public void sendVerificationCode(String email) {
        String code = String.format("%06d", new Random().nextInt(999999));
        codeStore.put(email, new VerificationCodeInfo(code, System.currentTimeMillis()));
        sendCodeEmail(email, code);
    }

    public boolean verifyCode(String email, String code) {
        VerificationCodeInfo info = codeStore.get(email);
        return info != null && info.getCode().equals(code)
               && System.currentTimeMillis() - info.getTimestamp() < 5 * 60 * 1000;
    }

    private void sendCodeEmail(String to, String code) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject("[아이디 찾기 인증 코드]");
        message.setText("인증 코드: " + code + "\n\n※ 이 코드는 5분간 유효합니다.");
        mailSender.send(message);
    }

    static class VerificationCodeInfo {
        private String code;
        private long timestamp;

        public VerificationCodeInfo(String code, long timestamp) {
            this.code = code;
            this.timestamp = timestamp;
        }

        public String getCode() { return code; }
        public long getTimestamp() { return timestamp; }
    }


}