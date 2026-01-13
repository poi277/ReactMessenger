package com.Messenger.Messenger.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.Messenger.Messenger.DTO.MessengerUserLoginDTO;
import com.Messenger.Messenger.DTO.MessengerUserRegisterDTO;
import com.Messenger.Messenger.DTO.UserInfoDTO;
import com.Messenger.Messenger.Friend.FriendRepository;
import com.Messenger.Messenger.config.UUIDUtil;
import com.Messenger.Messenger.info.MessengerUser;
import com.Messenger.Messenger.repository.MessengerUserRepository;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

@Service
public class MessengerUserService {
	private final MessengerUserRepository messengeruserRepository;
	private final PasswordEncoder passwordEncoder;
	@Autowired
	private AuthenticationManager authenticationManager;
	@Autowired
	private FriendRepository friendRepository;

	public MessengerUserService(MessengerUserRepository userRepository) {
		this.messengeruserRepository = userRepository;
		this.passwordEncoder = new BCryptPasswordEncoder(); // 또는 @Bean으로 등록해서 주입받기
	}


	public void RegisterUserService(MessengerUserRegisterDTO dto) {
		if (messengeruserRepository.existsById(dto.getId())) {
			throw new IllegalArgumentException("이미 존재하는 사용자 ID입니다.");
		}
		String encodedPassword = passwordEncoder.encode(dto.getPassword());

		// UUID 생성
		String uuid = UUIDUtil.generateUniqueUuid(messengeruserRepository);

		MessengerUser user = new MessengerUser(dto.getId(), encodedPassword, dto.getName(), uuid, dto.getEmail(),
				"Messenger");
		messengeruserRepository.save(user);
	}

	public MessengerUser loginService(MessengerUserLoginDTO dto) {
		Optional<MessengerUser> messengerUserOpt = messengeruserRepository.findById(dto.getId());
		if (messengerUserOpt.isEmpty()) {
			throw new IllegalArgumentException("해당 유저의 정보가 없습니다.");
		}
		Authentication authentication = authenticationManager
				.authenticate(new UsernamePasswordAuthenticationToken(dto.getId(), dto.getPassword()));

		// SecurityContext에 인증 정보 저장
		SecurityContextHolder.getContext().setAuthentication(authentication);

		return messengerUserOpt.get();
	}


	public void UpdateUserInfo(MessengerUser messengerUser, UserInfoDTO userInfoDTO) {
		if (userInfoDTO.getName() != null && userInfoDTO.getName().length() > 8) {
			throw new IllegalArgumentException("이름은 8자 이하여야 합니다.");
		}
		if (userInfoDTO.getIntroduce() != null && userInfoDTO.getIntroduce().length() > 30) {
			throw new IllegalArgumentException("소개는 30자 이하여야 합니다.");
		}
		messengerUser.setName(userInfoDTO.getName());
		messengerUser.setPhotoURL(userInfoDTO.getPhotoURL());
		messengerUser.setIntroduce(userInfoDTO.getIntroduce());

		messengeruserRepository.save(messengerUser);
	}


	public String findIdByEmail(String email) {
		List<MessengerUser> users = messengeruserRepository.findAllByEmail(email);
		Optional<MessengerUser> messengerUserOpt = users.stream().filter(s -> "Messenger".equals(s.getPlatform()))
				.findFirst();
		return messengerUserOpt.map(MessengerUser::getId).orElse(null);
	}


	public boolean existsByEmail(String email) {
		return messengeruserRepository.existsByEmail(email);
	}

	public boolean SessionOnline(HttpServletRequest request) {
		HttpSession session = request.getSession(false); // 이미 존재하는 세션 가져오기
		if (session == null || session.getAttribute("USER_UUID") == null) {
			return false;
		}
		return true;
	}

	public boolean isOAuthUser(String id) {
		Optional<MessengerUser> userOpt = messengeruserRepository.findById(id);
		if (userOpt.isPresent()) {
			MessengerUser user = userOpt.get();
			String platform = user.getPlatform();
			// platform이 "Messenger"면 일반 회원(false), 그 외(예: "Kakao", "Google")면 소셜(true)
			return !"Messenger".equals(platform);
		}
		return false; // 이메일이 없는 경우 false
	}


	public boolean isFriend(String userUuid, String postUuid) {
		if (userUuid == null || postUuid == null)
			return false;
		// 양방향 체크
		return friendRepository.existsByUserUuidAndFriendUuid(userUuid, postUuid)
				|| friendRepository.existsByUserUuidAndFriendUuid(postUuid, userUuid);
	}

}
