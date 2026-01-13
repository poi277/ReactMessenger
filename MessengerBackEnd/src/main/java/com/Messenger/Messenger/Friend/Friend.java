package com.Messenger.Messenger.Friend;

import com.Messenger.Messenger.info.MessengerUser;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "friend")
public class Friend {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id") // 컬럼명 맞게 조정
	private MessengerUser user;
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "friend_id")
	private MessengerUser friend;

	public Friend() {

	}
	public Friend(Long id, MessengerUser user, MessengerUser friend) {
		super();
		this.id = id;
		this.user = user;
		this.friend = friend;
	}
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public MessengerUser getUser() {
		return user;
	}

	public void setUser(MessengerUser user) {
		this.user = user;
	}

	public MessengerUser getFriend() {
		return friend;
	}

	public void setFriend(MessengerUser friend) {
		this.friend = friend;
	}

}
