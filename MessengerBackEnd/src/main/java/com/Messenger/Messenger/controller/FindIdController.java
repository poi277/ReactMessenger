package com.Messenger.Messenger.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.Messenger.Messenger.info.EmailRequest;
import com.Messenger.Messenger.info.VerificationRequest;
import com.Messenger.Messenger.service.FindIdService;
import com.Messenger.Messenger.service.MailService;
import com.Messenger.Messenger.service.MessengerUserService;


@RestController
@RequestMapping("/find-id")
public class FindIdController {

    @Autowired
    private FindIdService findIdService;
    @Autowired
	private MailService mailService;
	@Autowired
	private MessengerUserService messengerUserService;
	@Autowired
	PasswordEncoder passwordEncoder;


	@PostMapping("/send-code")
    public ResponseEntity<?> sendFindIdCode(@RequestBody EmailRequest request) {
		if (!messengerUserService.existsByEmail(request.getEmail())) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body("해당 이메일로 등록된 유저가 없습니다.");
		}
		if (messengerUserService.isOAuthUser(request.getEmail())) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body("소셜로그인 유저입니다.");
		}
        try {
            findIdService.sendVerificationCode(request.getEmail());
            return ResponseEntity.ok("인증 코드 발송 완료");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("메일 발송 실패");
        }
    }

	@PostMapping("verify-code")
    public ResponseEntity<?> verifyCodeAndReturnId(@RequestBody VerificationRequest request) {
        boolean verified = findIdService.verifyCode(request.getEmail(), request.getCode());
        if (verified) {
			List<String> idlist = new ArrayList<>();
			String id = messengerUserService.findIdByEmail(request.getEmail());
			if (id != null) {
				idlist.add(id);
			}
			System.out.println(idlist);
			return ResponseEntity.ok(idlist);
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("인증 실패 또는 만료된 코드");
        }
    }


}