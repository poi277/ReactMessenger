package com.Messenger.Messenger.config;

import java.util.UUID;

import com.Messenger.Messenger.repository.MessengerUserRepository;

public class UUIDUtil {
	public static String generateUniqueUuid(MessengerUserRepository repo) {
		int maxRetries = 5;
		for (int i = 0; i < maxRetries; i++) {
			String uuid = UUID.randomUUID().toString();
			if (!repo.existsByUuid(uuid))
				return uuid;
		}
		throw new RuntimeException("UUID 중복으로 인한 생성 실패");
	}
}
