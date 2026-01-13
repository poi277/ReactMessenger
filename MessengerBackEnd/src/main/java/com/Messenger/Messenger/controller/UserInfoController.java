package com.Messenger.Messenger.controller;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.Messenger.Messenger.DTO.UserInfoDTO;
import com.Messenger.Messenger.DTO.UserSessionDTO;
import com.Messenger.Messenger.info.MessengerUser;
import com.Messenger.Messenger.repository.MessagePhotoRepository;
import com.Messenger.Messenger.repository.MessengerUserRepository;
import com.Messenger.Messenger.service.MessengerUserService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

@RestController
@RequestMapping("/myinfo")
public class UserInfoController {
	@Autowired
	private MessagePhotoRepository messagePhotoRepository;
	@Autowired
	private MessengerUserRepository messengerUserRepository;
	@Autowired
	private MessengerUserService messengerUserService;

	UserInfoController() {
	}

	@GetMapping("/{uuid}")
	public ResponseEntity<UserInfoDTO> GetUserInfo(@PathVariable String uuid) {
		Optional<MessengerUser> messengerUserOpt = messengerUserRepository.findByUuid(uuid);

		if (messengerUserOpt.isPresent()) {
			UserInfoDTO userInfoDTO = new UserInfoDTO(messengerUserOpt.get());
			return ResponseEntity.ok(userInfoDTO);
		} else {
			return ResponseEntity.notFound().build();
		}
	}

	@PutMapping("/{uuid}")
	public ResponseEntity<?> updateUserInfo(@PathVariable String uuid, @RequestBody UserInfoDTO userInfoDTO,
			HttpServletRequest request) {

		Optional<MessengerUser> messengerUserOpt = messengerUserRepository.findByUuid(uuid);
		if (messengerUserOpt.isEmpty()) {
			return ResponseEntity.notFound().build();
		}

		try {
			messengerUserService.UpdateUserInfo(messengerUserOpt.get(), userInfoDTO);

			UserInfoDTO updatedDto = new UserInfoDTO(messengerUserOpt.get());

			// 세션 갱신
			HttpSession session = request.getSession(true);
			UserSessionDTO userSession = new UserSessionDTO(updatedDto.getUuid(), updatedDto.getName(),
					updatedDto.getPhotoURL());
			session.setAttribute("USER", userSession);
			return ResponseEntity.ok(updatedDto);
		} catch (IllegalArgumentException e) {
			return ResponseEntity.badRequest().body(e.getMessage());
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("서버 오류 발생");
		}
	}

}

