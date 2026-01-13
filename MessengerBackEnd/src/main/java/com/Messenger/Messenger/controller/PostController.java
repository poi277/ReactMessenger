package com.Messenger.Messenger.controller;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

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

import com.Messenger.Messenger.DTO.MessagePhotoDTO;
import com.Messenger.Messenger.DTO.MessagePostDTO;
import com.Messenger.Messenger.info.MessagePhoto;
import com.Messenger.Messenger.info.MessagePost;
import com.Messenger.Messenger.info.MessengerUser;
import com.Messenger.Messenger.info.PostLike;
import com.Messenger.Messenger.repository.CommentRepository;
import com.Messenger.Messenger.repository.MessagePostRepository;
import com.Messenger.Messenger.repository.MessengerUserRepository;
import com.Messenger.Messenger.service.MessengerPostService;
import com.Messenger.Messenger.service.MessengerUserService;
import com.Messenger.Messenger.service.PostLikeService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

@RestController
@RequestMapping("/post")
public class PostController {
	private final PostLikeService postLikeService;
	private final MessagePostRepository messagePostRepository;
	@Autowired
	private MessengerUserRepository messengerUserRepository;
	@Autowired
	private MessengerUserService messengeruserService;
	@Autowired
	private CommentRepository commentRepository;
	@Autowired
	private MessengerPostService messengerPostService;

	public PostController(PostLikeService postLikeService, MessagePostRepository postRepository) {
		this.postLikeService = postLikeService;
		this.messagePostRepository = postRepository;
	}

	@PostMapping("/{postId}/like")
	public ResponseEntity<Map<String, Object>> toggleLike(@PathVariable Long postId,
			HttpServletRequest request) {
		HttpSession session = request.getSession(false);
		if (session == null) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "로그인 필요"));
		}
		String userUuid = (String) session.getAttribute("USER_UUID");
		if (userUuid == null) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "로그인 필요"));
		}
		MessengerUser messengerUser = messengerUserRepository.findByUuid(userUuid)
				.orElseThrow(() -> new RuntimeException("사용자 없음"));
		MessagePost post = messagePostRepository.findById(postId).orElseThrow(() -> new RuntimeException("게시글 없음"));
		boolean likes = postLikeService.toggleLike(post, messengerUser);

		// 좋아요 개수는 엔티티에서 연관관계 매핑이 되어 있으면 getLikes().size(),
		// 아니면 likeCount 필드 사용
		int likeCount = post.getLikeCount(); // 또는 post.getLikes().size()

		Map<String, Object> response = new HashMap<>();
		response.put("likes", likes);
		response.put("likeCount", likeCount);

		return ResponseEntity.ok(response);
	}

	@GetMapping("/allpostlist")
	public List<MessagePostDTO> AllPostList(HttpServletRequest request) {
		HttpSession session = request.getSession(false);
		String userUuid = (session != null) ? (String) session.getAttribute("USER_UUID") : null;

		List<MessagePost> posts = messagePostRepository.findAll();

		return posts.stream().filter(post -> messengerPostService.canViewPost(post, userUuid)) // 서비스로 위임
				.map(post -> {
					String profilePhotoUrl = post.getMessengerUser() != null ? post.getMessengerUser().getPhotoURL()
							: null;
					int commentCount = commentRepository.countByMessagePostId(post.getId());
					return MessagePostDTO.toDTO(post, userUuid, profilePhotoUrl, commentCount);
				}).collect(Collectors.toList());
	}



	@GetMapping("/userpostlist/{uuid}")
	public List<MessagePostDTO> userPostList(@PathVariable String uuid, HttpServletRequest request) {
		HttpSession session = request.getSession(false);
		String userUuid = (session != null) ? (String) session.getAttribute("USER_UUID") : null;

		List<MessagePost> posts = messagePostRepository.findByMessengerUser_Uuid(uuid);

		return posts.stream().filter(post -> messengerPostService.canViewPost(post, userUuid))
				.map(post -> {
					int commentCount = commentRepository.countByMessagePostId(post.getId());
					String profilePhotoUrl = post.getMessengerUser() != null ? post.getMessengerUser().getPhotoURL()
							: null;
					return MessagePostDTO.toDTO(post, userUuid, profilePhotoUrl, commentCount);
				}).collect(Collectors.toList());
	}


	@GetMapping("/userphotolist/{uuid}")
	public List<MessagePhotoDTO> userPhotoList(@PathVariable String uuid, HttpServletRequest request) {
		HttpSession session = request.getSession(false);
		String viewerUuid = (session != null) ? (String) session.getAttribute("USER_UUID") : null;

		List<MessagePost> posts = messagePostRepository.findByMessengerUser_Uuid(uuid);
		List<MessagePhotoDTO> photos = new ArrayList<>();

		for (MessagePost post : posts) {
			if (!messengerPostService.canViewPost(post, viewerUuid))
				continue;
			for (MessagePhoto photo : post.getPhotos()) {
				photos.add(new MessagePhotoDTO(photo));
			}
		}

		return photos;
	}



	@GetMapping("/userlikepost/{uuid}")
	public List<MessagePostDTO> userlikepostList(@PathVariable String uuid, HttpServletRequest request) {
	    HttpSession session = request.getSession(false);
	    String userUuid = (session != null) ? (String) session.getAttribute("USER_UUID") : null;

	    Optional<MessengerUser> messengerUserOpt = messengerUserRepository.findByUuid(uuid);
	    if (messengerUserOpt.isEmpty()) {
	        return new ArrayList<>();
	    }
	    MessengerUser messengerUser = messengerUserOpt.get();
		String profilePhotoURL = messengerUser.getPhotoURL();
	    List<PostLike> userLikes = messengerUser.getLikes();

		List<MessagePostDTO> postDTOs = userLikes.stream().map(PostLike::getMessagePost)
				.filter(post -> post != null && messengerPostService.canViewPost(post, userUuid)) // 권한 체크
				.map(post -> MessagePostDTO.toDTO(post, userUuid, profilePhotoURL)) // DTO 변환
				.collect(Collectors.toList());
	    return postDTOs;
	}


	@GetMapping("/{postid}")
	public ResponseEntity<?> userPost(@PathVariable Long postid, HttpServletRequest request) {
		Optional<MessagePost> postOpt = messagePostRepository.findById(postid);
		HttpSession session = request.getSession(false);
		String currentUserUuid = (session != null) ? (String) session.getAttribute("USER_UUID") : null;
		if (postOpt.isEmpty()) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body("게시글을 찾을 수 없습니다.");
		}
		MessagePost post = postOpt.get();
		String profilePhotoURL = post.getMessengerUser() != null ? post.getMessengerUser().getPhotoURL() : null;

		if (!messengerPostService.canViewPost(post, currentUserUuid)) {
			return ResponseEntity.status(HttpStatus.FORBIDDEN).body("게시글을 볼 수 있는 권한이 없습니다.");
		}
		MessagePostDTO dto = MessagePostDTO.toDTO(post, currentUserUuid, profilePhotoURL);
		return ResponseEntity.ok(dto);
	}


	@PostMapping("/write")
	public ResponseEntity<Long> writePost(@RequestBody MessagePostDTO dto, HttpServletRequest request) {
		if (!messengeruserService.SessionOnline(request)) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
		}
		HttpSession session = request.getSession(false);
		String userUuid = (String) session.getAttribute("USER_UUID");
		Optional<MessengerUser> userOpt = messengerUserRepository.findByUuid(userUuid);
		if (userOpt.isEmpty()) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
		}
		MessengerUser user = userOpt.get();

		MessagePost post = new MessagePost();
		post.setTitle(dto.getTitle());
		post.setContext(dto.getContext());
		post.setLikeCount(0);
		post.setCreatedDate(LocalDateTime.now());
		post.setMessengerUser(user);
		post.setPostRange(dto.getPostRange());
		messagePostRepository.save(post);
		// ✅ 글 ID 리턴
		return ResponseEntity.ok(post.getId());
	}

	@PutMapping("/{postid}")
	public ResponseEntity<?> editPost(@PathVariable Long postid, @RequestBody MessagePostDTO dto,
			HttpServletRequest request) {
		if (!messengeruserService.SessionOnline(request)) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("사용자 정보 없음");
		}
		HttpSession session = request.getSession(false);
		String userUuid = (String) session.getAttribute("USER_UUID");
		// 기존 게시글 조회
		Optional<MessagePost> postOpt = messagePostRepository.findById(postid);
		if (postOpt.isEmpty()) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body("게시글을 찾을 수 없습니다.");
		}
		MessagePost post = postOpt.get();
		if (!post.getMessengerUser().getUuid().equals(userUuid)) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("권한이 없습니다.");
		}
		post.setTitle(dto.getTitle());
		post.setContext(dto.getContext());
		post.setPostRange(dto.getPostRange());
		messagePostRepository.save(post);
		return ResponseEntity.ok(dto);
	}

	@DeleteMapping("/{postid}")
	public ResponseEntity<?> deletePost(@PathVariable Long postid, HttpServletRequest request) {
		if (!messengeruserService.SessionOnline(request)) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("사용자 정보 없음");
		}
		Optional<MessagePost> postOpt = messagePostRepository.findById(postid);
		if (postOpt.isEmpty()) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body("게시글을 찾을 수 없습니다.");
		}
		MessagePost post = postOpt.get();
		HttpSession session = request.getSession(false);
		String userUuid = (String) session.getAttribute("USER_UUID");
		if (post.getMessengerUser().getUuid().equals(userUuid)) {
			messagePostRepository.deleteById(postid);
			return ResponseEntity.ok("게시물을 삭제했습니다");
		} else {
			return ResponseEntity.status(HttpStatus.FORBIDDEN).body("권한이 없습니다.");
		}
	}

}
