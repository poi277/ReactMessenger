package com.Messenger.Messenger.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.Messenger.Messenger.DTO.CommentDTO;
import com.Messenger.Messenger.info.MessagePost;
import com.Messenger.Messenger.info.MessengerUser;
import com.Messenger.Messenger.repository.CommentRepository;
import com.Messenger.Messenger.repository.MessagePostRepository;
import com.Messenger.Messenger.repository.MessengerUserRepository;
import com.Messenger.Messenger.service.CommentService;
import com.Messenger.Messenger.service.MessengerSessionService;
import com.Messenger.Messenger.service.MessengerUserService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

@RequestMapping("/comment")
@RestController
public class CommentController {

	@Autowired
	private MessengerUserRepository messengeruserRepository;
	@Autowired
	private MessengerUserService messengeruserService;
	@Autowired
	private MessagePostRepository messagePostRepository;
	@Autowired
	private MessengerSessionService messengerSessionService;
	@Autowired
	private CommentRepository commentRepository;
	@Autowired
	private CommentService commentService;

	public CommentController() {
	}

	@GetMapping("/{postid}")
	public ResponseEntity<?> getCommentsByPost(@PathVariable long postid) {
		// 게시글 존재 여부 체크
		Optional<MessagePost> postOpt = messagePostRepository.findById(postid);
		if (postOpt.isEmpty()) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body("게시글을 찾을 수 없습니다.");
		}
		// 서비스 호출
		List<CommentDTO> commentDTO = commentService.getCommentsByPost(postid);
		return ResponseEntity.ok(commentDTO);
	}


	@PostMapping("/{postid}")
	public ResponseEntity<?> writeComment(@PathVariable long postid, @RequestBody CommentDTO dto,
			HttpServletRequest request) {
		// 세션 로그인 체크
		if (!messengeruserService.SessionOnline(request)) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("로그인 필요");
		}
		HttpSession session = request.getSession(false);
		String userUuid = (String) session.getAttribute("USER_UUID");
		Optional<MessengerUser> useropt = messengeruserRepository.findByUuid(userUuid);
		if (useropt.isEmpty()) {
			return ResponseEntity.status(HttpStatus.FORBIDDEN).body("UUID가 일치하지않습니다");
		}
		try {
			CommentDTO savedDto = commentService.addComment(dto, userUuid);
			return ResponseEntity.ok(savedDto);
		} catch (RuntimeException e) {
			return ResponseEntity.badRequest().body(e.getMessage());
		}
	}

	/**
	 * 댓글 삭제 (soft delete) content를 NULL 로 변경 (프론트에서 null일 경우 "삭제된 메시지입니다." 표시)
	 */
	@PutMapping("/{commentId}")
	public ResponseEntity<?> updateComment(@PathVariable Long commentId, @RequestBody CommentDTO dto,
			HttpServletRequest request) {
		if (!messengeruserService.SessionOnline(request)) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("로그인 필요");
		}
		HttpSession session = request.getSession(false);
		String userUuid = (String) session.getAttribute("USER_UUID");
		Optional<MessengerUser> useropt = messengeruserRepository.findByUuid(userUuid);
		if (useropt.isEmpty()) {
			return ResponseEntity.status(HttpStatus.FORBIDDEN).body("UUID가 일치하지않습니다");
		}
		try {
			CommentDTO updated = commentService.updateComment(commentId, dto, userUuid);
			return ResponseEntity.ok(updated);
		} catch (RuntimeException e) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
		}
	}

	@DeleteMapping("/{commentId}")
	public ResponseEntity<?> deleteComment(@PathVariable Long commentId, HttpServletRequest request) {
		HttpSession session = request.getSession(false);
		String userUuid = (String) session.getAttribute("USER_UUID");
		Optional<MessengerUser> useropt = messengeruserRepository.findByUuid(userUuid);
		if (useropt.isEmpty()) {
			return ResponseEntity.status(HttpStatus.FORBIDDEN).body("UUID가 일치하지않습니다");
		}
		try {
			commentService.deleteComment(commentId);
			return ResponseEntity.ok("댓글이 삭제 처리되었습니다.");
		} catch (RuntimeException e) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
		}
	}
}

