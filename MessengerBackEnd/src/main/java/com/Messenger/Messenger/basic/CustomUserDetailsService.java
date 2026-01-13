package com.Messenger.Messenger.basic;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.Messenger.Messenger.info.MessengerUser;
import com.Messenger.Messenger.repository.MessengerUserRepository;

@Service
public class CustomUserDetailsService implements UserDetailsService {

	private final MessengerUserRepository messengerUserRepository;

	public CustomUserDetailsService(MessengerUserRepository messengerUserRepository) {
		this.messengerUserRepository = messengerUserRepository;
	}

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		MessengerUser user = messengerUserRepository.findById(username)
				.orElseThrow(() -> new UsernameNotFoundException("사용자를 찾을 수 없습니다: " + username));

		System.out.println("CustomUserDetailsService 호출됨: " + username);

		return org.springframework.security.core.userdetails.User.builder().username(user.getId())
				.password(user.getPassword()) // 암호화된 비밀번호여야 함
				.roles("USER") // 필요시 권한 설정
				.build();
	}
}
