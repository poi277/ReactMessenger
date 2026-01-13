package com.Messenger.Messenger.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.Messenger.Messenger.info.MessagePost;
import com.Messenger.Messenger.info.MessengerUser;

@Service
public class MessengerPostService {

	@Autowired
	private MessengerUserService messengerUserService;


	public boolean canViewPost(MessagePost post, String viewerUuid) {
		if (post.getPostRange() == null)
			return false;
		MessengerUser postUser = post.getMessengerUser();
		if (postUser == null)
			return false;

		switch (post.getPostRange()) {
		case publicpost:
			return true;
		case friendpost:
			return postUser.getUuid().equals(viewerUuid)
					|| (viewerUuid != null && messengerUserService.isFriend(postUser.getUuid(), viewerUuid));
		case privatepost:
			return viewerUuid != null && postUser.getUuid().equals(viewerUuid);
		default:
			return false;
		}
	}

}
