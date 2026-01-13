package com.Messenger.Messenger.chating;

import java.util.Set;

public class GroupChatRoomCreateRequest {
	private Set<String> participantuuid; // 방에 들어갈 유저 uuid

	public Set<String> getParticipantuuid() {
		return participantuuid;
	}

	public void setParticipantuuid(Set<String> participantuuid) {
		this.participantuuid = participantuuid;
	}

}