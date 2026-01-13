package com.Messenger.Messenger.basic;

import java.util.Map;
import java.util.UUID;

import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.Messenger.Messenger.DTO.UserSessionDTO;
import com.Messenger.Messenger.info.MessengerUser;
import com.Messenger.Messenger.repository.MessengerUserRepository;
import com.Messenger.Messenger.service.MessengerSessionService;

import jakarta.servlet.http.HttpSession;

@Service
public class CustomOAuth2UserService implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {

	private final MessengerUserRepository messengerUserRepository;
	private final MessengerSessionService messengerSessionService;

	public CustomOAuth2UserService(MessengerUserRepository messengerUserRepository,
			MessengerSessionService messengerSessionService) {
		this.messengerUserRepository = messengerUserRepository;
		this.messengerSessionService = messengerSessionService;
	}

	@Override
	public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
		OAuth2UserService<OAuth2UserRequest, OAuth2User> delegate = new DefaultOAuth2UserService();
		OAuth2User oAuth2User = delegate.loadUser(userRequest);
		String registrationId = userRequest.getClientRegistration().getRegistrationId(); // google or naver
		String email;
		String name;
		String oauthId; // 공급자별 unique id
		if ("google".equals(registrationId)) {
			oauthId = oAuth2User.getAttribute("sub"); // 구글 고유 id
			name = oAuth2User.getAttribute("name");
			email = oAuth2User.getAttribute("email");
		} else if ("naver".equals(registrationId)) {
			Map<String, Object> response = oAuth2User.getAttribute("response");
			oauthId = (String) response.get("id");
			name = (String) response.get("name");
			email = (String) response.get("email");

		} else if ("kakao".equals(registrationId)) {
			Map<String, Object> kakaoAccount = oAuth2User.getAttribute("kakao_account");
			Map<String, Object> profile = (Map<String, Object>) kakaoAccount.get("profile");

			oauthId = oAuth2User.getAttribute("id").toString();
			name = profile.get("nickname").toString();
			email = oauthId; // 이메일이 없으므로 oauthId 사용
		} else if ("discord".equals(registrationId)) {
			oauthId = oAuth2User.getAttribute("id").toString(); // Discord 고유 ID
			name = oAuth2User.getAttribute("username"); // Discord 사용자명
			email = oAuth2User.getAttribute("email"); // Discord 이메일 (선택사항)
		}
		else {
			throw new OAuth2AuthenticationException("지원하지 않는 소셜 로그인입니다.");
		}

		// DB 조회 및 신규 등록(이메일용 아래는 id용) 차이점은 이메일로 등록시
		// 아이디와 이메일 매치시 중복 생성 x
//		MessengerUser user = messengerUserRepository.findByEmail(email)
//				.orElseGet(() -> registerNewUser(oauthId, email, name, registrationId));


		String customid = checkCustomId(oauthId, email, registrationId);
		MessengerUser user = messengerUserRepository.findById(customid)
				.orElseGet(() -> registerNewUser(oauthId, email, name, registrationId));
		// 세션 저장
		UserSessionDTO userSession = new UserSessionDTO(user.getUuid(), user.getName(), user.getPhotoURL());
		HttpSession session = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest()
				.getSession();
		session.setAttribute("USER", userSession);
		session.setAttribute("USER_UUID", user.getUuid());
		// 로그인 세션 갱신
		messengerSessionService.login(user.getUuid());
		messengerSessionService.heartbeat(user.getUuid());

		return oAuth2User;
	}

	private MessengerUser registerNewUser(String oauthId, String email, String name, String registrationId) {
		MessengerUser user = new MessengerUser();
		System.out.println("새로 생성");
		// Kakao 등 이메일 없을 때 email 대신 oauthId 사용
		if (email == null || email.isEmpty()) {
			email = oauthId;
		}
		String emailPrefix;
		if (email.contains("@")) {
			emailPrefix = email.split("@")[0];
		} else {
			emailPrefix = oauthId; // Kakao는 email 없으니 oauthId 사용
		}
		String customId;
		if ("google".equals(registrationId)) {
			customId = "go_" + emailPrefix;
		} else if ("naver".equals(registrationId)) {
			customId = "na_" + emailPrefix;
		} else if ("kakao".equals(registrationId)) {
			customId = "ka_" + emailPrefix; // email이 oauthId일 수 있음
		} else if ("discord".equals(registrationId)) {
			customId = "ds_" + emailPrefix; // email이 oauthId일 수 있음
		} else {
			customId = "us_" + emailPrefix;
		}
		user.setId(customId);
		user.setEmail(email); // 이제 Kakao도 email 필드에 oauthId가 들어감
		user.setName(name);
		user.setPlatform(registrationId);
		user.setUuid(UUID.randomUUID().toString());

		return messengerUserRepository.save(user);
	}

	private String checkCustomId(String oauthId, String email, String registrationId) {
		// Kakao 등 이메일 없을 때 email 대신 oauthId 사용
		if (email == null || email.isEmpty()) {
			email = oauthId;
		}

		String emailPrefix;
		if (email.contains("@")) {
			emailPrefix = email.split("@")[0];
		} else {
			emailPrefix = oauthId; // Kakao는 email 없으니 oauthId 사용
		}

		String customId;
		if ("google".equals(registrationId)) {
			customId = "go_" + emailPrefix;
		} else if ("naver".equals(registrationId)) {
			customId = "na_" + emailPrefix;
		} else if ("kakao".equals(registrationId)) {
			customId = "ka_" + emailPrefix; // email이 oauthId일 수 있음
		} else if ("discord".equals(registrationId)) {
			customId = "ds_" + emailPrefix; // Discord 약어를 ds_로 변경
		} else {
			customId = "us_" + emailPrefix;
		}

		return customId;
	}


}
