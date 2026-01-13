package com.Messenger.Messenger.controller;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.Messenger.Messenger.basic.BaseURLCollector;
import com.Messenger.Messenger.info.MessagePhoto;
import com.Messenger.Messenger.info.MessagePost;
import com.Messenger.Messenger.info.MessengerUser;
import com.Messenger.Messenger.repository.MessagePhotoRepository;
import com.Messenger.Messenger.repository.MessagePostRepository;
import com.Messenger.Messenger.repository.MessengerUserRepository;
import com.Messenger.Messenger.service.MessengerUserService;

import jakarta.servlet.http.HttpServletRequest;

@RequestMapping("/photo")
@RestController
public class PhotoController {
	@Autowired
	private MessagePhotoRepository messagePhotoRepository;
	@Autowired
	private MessagePostRepository messagePostRepository;
	@Autowired
	private MessengerUserRepository messengerUserRepository;
	@Autowired
	private MessengerUserService messengeruserService;

	private static final Path UPLOAD_DIR = Paths.get("uploads");
	private static final Path PROFILE_DIR = UPLOAD_DIR.resolve("profile");
	private static final Path RECYCLE_DIR = Paths.get("recycle");
	private static final Path RECYCLE_PROFILE_DIR = RECYCLE_DIR.resolve("profile");

	public PhotoController() {
	}

	@PostMapping("/{postId}")
	public ResponseEntity<String> uploadPhotoToPost(@PathVariable Long postId,
			@RequestParam("file") MultipartFile file, HttpServletRequest request) {
		if (!messengeruserService.SessionOnline(request)) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("사용자 정보 없음");
		}
		try {
			MessagePost post = messagePostRepository.findById(postId)
					.orElseThrow(() -> new RuntimeException("게시글을 찾을 수 없습니다. postId=" + postId));

			if (!Files.exists(UPLOAD_DIR)) {
				Files.createDirectories(UPLOAD_DIR);
			}

			String filename = UUID.randomUUID() + "_" + file.getOriginalFilename();
			Path filePath = UPLOAD_DIR.resolve(filename);
			Files.write(filePath, file.getBytes());

			String fileUrl = BaseURLCollector.basebackendurl + "/uploads/" + filename;

			MessagePhoto photo = new MessagePhoto();
			photo.setPhotoUrl(fileUrl);
			photo.setMessagePost(post);
			messagePhotoRepository.save(photo);

			return ResponseEntity.ok(fileUrl);
		} catch (IOException e) {
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("파일 업로드 실패");
		}
	}

	@DeleteMapping("/{postId}/{photoId}")
	public ResponseEntity<?> deletePhoto(@PathVariable Long postId, @PathVariable Long photoId,
			HttpServletRequest request) {
		if (!messengeruserService.SessionOnline(request)) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("사용자 정보 없음");
		}
		MessagePhoto photo = messagePhotoRepository.findById(photoId)
				.orElseThrow(() -> new RuntimeException("사진을 찾을 수 없습니다. photoId=" + photoId));

		if (!Long.valueOf(photo.getMessagePost().getId()).equals(postId)) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
		}

		String photoUrl = photo.getPhotoUrl();
		try {
			String filename = photoUrl.substring(photoUrl.lastIndexOf('/') + 1);
			Path sourcePath = UPLOAD_DIR.resolve(filename);

			if (!Files.exists(RECYCLE_DIR)) {
				Files.createDirectories(RECYCLE_DIR);
			}
			Path targetPath = RECYCLE_DIR.resolve(filename);

			if (Files.exists(sourcePath)) {
				Files.move(sourcePath, targetPath);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

		messagePhotoRepository.delete(photo);
		return ResponseEntity.noContent().build();
	}

	@PostMapping("/myinfo/{uuid}")
	public ResponseEntity<String> uploadUserPhoto(@RequestParam("file") MultipartFile file, @PathVariable String uuid,
			HttpServletRequest request) {
		if (!messengeruserService.SessionOnline(request)) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("사용자 정보 없음");
		}
		try {
			// 파일 저장
			if (!Files.exists(PROFILE_DIR)) {
				Files.createDirectories(PROFILE_DIR);
			}
			String filename = UUID.randomUUID() + "_" + file.getOriginalFilename();
			Path filePath = PROFILE_DIR.resolve(filename);
			Files.write(filePath, file.getBytes());

			String fileUrl = BaseURLCollector.basebackendurl + "/uploads/profile/" + filename;

			// 유저 조회 후 기존 프로필 사진 recycle로 이동
			Optional<MessengerUser> userOpt = messengerUserRepository.findByUuid(uuid);
			if (userOpt.isEmpty()) {
				return ResponseEntity.notFound().build();
			}
			MessengerUser user = userOpt.get();

			String oldPhotoUrl = user.getPhotoURL();
			if (oldPhotoUrl != null && !oldPhotoUrl.isBlank()) {
				String oldFilename = oldPhotoUrl.substring(oldPhotoUrl.lastIndexOf('/') + 1);
				Path oldFilePath = PROFILE_DIR.resolve(oldFilename);

				if (!Files.exists(RECYCLE_PROFILE_DIR)) {
					Files.createDirectories(RECYCLE_PROFILE_DIR);
				}
				Path recyclePath = RECYCLE_PROFILE_DIR.resolve(oldFilename);
				if (Files.exists(oldFilePath)) {
					Files.move(oldFilePath, recyclePath);
				}
			}

			user.setPhotoURL(fileUrl);
			messengerUserRepository.save(user);
			return ResponseEntity.ok(fileUrl);

		} catch (IOException e) {
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("파일 업로드 실패");
		}
	}



}

