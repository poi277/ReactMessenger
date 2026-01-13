package com.Messenger.Messenger.chating;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/chat")
public class ChatController {
	@Autowired
	ChatMessageRepository chatMessageRepository;
	@Autowired
	ChatRoomMemberRepository chatRoomMemberRepository;
	@Autowired
	ChatRoomRepository chatRoomRepository;
	@Autowired
	ChatService chatService;

	@GetMapping("/info/{receiveruuid}")
	public ResponseEntity<ChatReciverInfoDTO> getReciverInfo(@PathVariable String receiveruuid) {
		ChatReciverInfoDTO ChatReciverInfoDTO = chatService.getReceiverInfo(receiveruuid);
		return ResponseEntity.ok(ChatReciverInfoDTO);
	}
	
	@GetMapping("/one-to-one")
	public ResponseEntity<List<ChatMessageDTO>> getChat(@RequestParam String senderuuid,
			@RequestParam String receiveruuid) {
		OneToOneMessageRequest One = new OneToOneMessageRequest();
		One.setSenderuuid(senderuuid);
		One.setReceiveruuid(receiveruuid);
		List<ChatMessageDTO> messages = chatService.getOneToOneMessages(One);
		return ResponseEntity.ok(messages);
	}


	@PostMapping("/one-to-one")
	public ResponseEntity<ChatMessageDTO> sendChat(@RequestBody OneToOneMessageRequest OneToOneMessageRequest) {
		ChatMessageDTO message = chatService.sendOneToOneMessage(OneToOneMessageRequest);
		return ResponseEntity.ok(message);
	}
}
