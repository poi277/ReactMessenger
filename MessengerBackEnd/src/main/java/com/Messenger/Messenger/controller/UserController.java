package com.Messenger.Messenger.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.Messenger.Messenger.DTO.MessengerUserLoginDTO;
import com.Messenger.Messenger.DTO.MessengerUserRegisterDTO;
import com.Messenger.Messenger.DTO.UserNameDTO;
import com.Messenger.Messenger.DTO.UserSessionDTO;
import com.Messenger.Messenger.info.MessengerUser;
import com.Messenger.Messenger.repository.MessagePostRepository;
import com.Messenger.Messenger.repository.MessengerUserRepository;
import com.Messenger.Messenger.service.MailService;
import com.Messenger.Messenger.service.MessengerSessionService;
import com.Messenger.Messenger.service.MessengerUserService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@RestController
public class UserController {

	private final MessengerUserRepository messengeruserRepository;
	private final MessengerUserService messengeruserService;
	private final MessagePostRepository messagePostRepository;
	@Autowired
	private MessengerSessionService messengerSessionService;
	@Autowired
	private MailService mailService;

	UserController(MessengerUserRepository messengeruserRepository, MessengerUserService messengeruserService,
			MessagePostRepository messagePostRepository) {
		this.messengeruserRepository = messengeruserRepository;
		this.messengeruserService = messengeruserService;
		this.messagePostRepository = messagePostRepository;
	}

	@GetMapping("/hello")
	public String hello() {
		return "hello";
	}

	@PostMapping("/userlogin")
	public ResponseEntity<?> login(@RequestBody MessengerUserLoginDTO dto, HttpServletRequest request) {
		try {
			MessengerUser user = messengeruserService.loginService(dto);
			HttpSession session = request.getSession(true); // 없으면 새로 생성

			SecurityContext securityContext = SecurityContextHolder.getContext();
			session.setAttribute("SPRING_SECURITY_CONTEXT", securityContext);
			UserSessionDTO userSession = new UserSessionDTO(user.getUuid(), user.getName(), user.getPhotoURL());
			session.setAttribute("USER", userSession);
			session.setAttribute("USER_UUID", user.getUuid());
			messengerSessionService.login(user.getUuid());
			return ResponseEntity.ok("로그인 성공");
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
		}
	}
	@PostMapping("/userlogout")
	public ResponseEntity<String> logout(HttpServletRequest request, HttpServletResponse response) {
		HttpSession session = request.getSession(false);
	    if (session != null) {
			// OAuth2 로그인도 포함해서 DTO 가져오기
			UserSessionDTO userSession = (UserSessionDTO) session.getAttribute("USER");
			if (userSession != null) {
				messengerSessionService.logout(userSession.getUuid());
			}
	    }
		// SecurityContext, 세션, 쿠키 모두 무효화
		SecurityContextLogoutHandler logoutHandler = new SecurityContextLogoutHandler();
		logoutHandler.setInvalidateHttpSession(true);
		logoutHandler.setClearAuthentication(true);
		logoutHandler.logout(request, response, null);
//		 SESSION 쿠키 삭제
//		Cookie cookie = new Cookie("SESSION", null);
//		cookie.setPath("/");
//		cookie.setHttpOnly(true);
//		cookie.setMaxAge(0);
//		response.addCookie(cookie);

		return ResponseEntity.ok("로그아웃 성공");
	}

	@GetMapping("/searchName")
	public ResponseEntity<?> searchUserName(@RequestParam String name) {
		if (name == null || name.isEmpty()) {
			return ResponseEntity.badRequest().body("검색어를 입력하세요");
		}
		// 이름으로 검색
		List<MessengerUser> users = messengeruserRepository.findByNameContainingIgnoreCase(name);
		// DTO로 변환
		List<UserNameDTO> dtoList = UserNameDTO.toDTOList(users);
		return ResponseEntity.ok(dtoList);
	}

	@GetMapping("/sessioncheck")
	public ResponseEntity<?> getCurrentUser(HttpServletRequest request) {
		HttpSession session = request.getSession(false);
		if (session == null)
			return ResponseEntity.status(HttpStatus.FORBIDDEN).body("로그인 필요");
		UserSessionDTO user = (UserSessionDTO) session.getAttribute("USER");
		if (user == null)
			return ResponseEntity.status(HttpStatus.FORBIDDEN).body("로그인 필요");
		messengerSessionService.heartbeat(user.getUuid());
		return ResponseEntity.ok(user);
	}


	@PostMapping("/register")
	public ResponseEntity<?> registerUser(@RequestBody MessengerUserRegisterDTO dto) {
		if (!dto.getPassword().equals(dto.getConfirmPassword())) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("비밀번호가 일치하지 않습니다.");
		}

		if (dto.getId().isEmpty() || dto.getName().isEmpty() || dto.getPassword().isEmpty()) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("정보를 모두 기입해주세요.");
		}
		try {
			messengeruserService.RegisterUserService(dto);
			return ResponseEntity.ok("회원가입 성공!");
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("회원가입 실패: " + e.getMessage());
		}
	}

}