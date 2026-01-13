package com.Messenger.Messenger.controller;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import com.Messenger.Messenger.DTO.ProfileDTO;
import com.Messenger.Messenger.info.MessengerUser;
import com.Messenger.Messenger.repository.MessagePostRepository;
import com.Messenger.Messenger.repository.MessengerUserRepository;

@Controller
@RequestMapping("/profile")
public class Profilecontroller {
	@Autowired
	private MessengerUserRepository messengerUserRepository;
	@Autowired
	private MessagePostRepository messagePostRepository;
	Profilecontroller()
	{
	}

	@GetMapping("/{uuid}")
	public ResponseEntity<ProfileDTO> getprofile(@PathVariable String uuid) {
		Optional<MessengerUser> useropt = messengerUserRepository.findByUuid(uuid);

		if (useropt.isEmpty()) {
			return ResponseEntity.notFound().build();
		}

		long postCount = messagePostRepository.countByMessengerUser_Uuid(uuid);

		ProfileDTO profileDTO = new ProfileDTO(useropt.get().getPhotoURL(), useropt.get().getName(),
				useropt.get().getIntroduce(), postCount);

		return ResponseEntity.ok(profileDTO);
	}

}
