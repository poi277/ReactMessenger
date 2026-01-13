package com.Messenger.Messenger.service;

import org.springframework.stereotype.Service;

import com.Messenger.Messenger.info.MessagePost;
import com.Messenger.Messenger.info.MessengerUser;
import com.Messenger.Messenger.info.PostLike;
import com.Messenger.Messenger.repository.PostLikeRepository;

import jakarta.transaction.Transactional;

@Service
public class PostLikeService {
	private final PostLikeRepository postLikeRepository;

	public PostLikeService(PostLikeRepository postLikeRepository) {
		this.postLikeRepository = postLikeRepository;
	}

	@Transactional
	public boolean toggleLike(MessagePost post, MessengerUser user) {
		boolean liked = postLikeRepository.existsByMessagePostAndUser(post, user);

		if (liked) {
			postLikeRepository.deleteByMessagePostAndUser(post, user);
		} else {
			PostLike postLike = new PostLike();
			postLike.setMessagePost(post);
			postLike.setUser(user);
			postLikeRepository.save(postLike);
		}

		// 좋아요 개수 직접 조회
		long likeCount = postLikeRepository.countByMessagePost(post);
		post.setLikeCount((int) likeCount);

		return liked ? false : true;
	}

}
