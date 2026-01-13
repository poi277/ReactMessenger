package com.Messenger.Messenger.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.Messenger.Messenger.info.MessagePhoto;

public interface MessagePhotoRepository extends JpaRepository<MessagePhoto, Long> {
}