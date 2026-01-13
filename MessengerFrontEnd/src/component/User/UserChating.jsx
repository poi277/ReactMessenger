import { useState, useEffect } from "react";
import { useParams } from "react-router-dom";
import { GetOnetoOneChatApi, PostOnetoOneChatApi,GetReceiverApi } from "../api/ApiService";
import { useUserAuth } from "../api/AuthProvider";
import "../Css/UserChating.css";

export default function UserChating() {
  const { uuid, frienduuid } = useParams();
  const [messages, setMessages] = useState([]);
  const [content, setContent] = useState("");
  const { user } = useUserAuth();
  const [receiverName,setreciverName] = useState("");
  const [receiverOnline,setreciverOnline] = useState(false);

  // 수신자 불러오기
  function getReceiverInfo() {
    GetReceiverApi(frienduuid)
    .then((response) => {
      console.log(response)
      setreciverName(response.data.receiverName);
      setreciverOnline(response.data.receiveronline);
      })
      .catch((error) => {
        console.error("채팅 불러오기 실패:", error);
      });
  }

  // 채팅 불러오기
  function fetchMessages() {
    GetOnetoOneChatApi(uuid, frienduuid)
      .then((data) => {
        setMessages(data);
      })
      .catch((error) => {
        console.error("채팅 불러오기 실패:", error);
      });
  }

  // 메시지 전송
  function postMessage() {
    if (!content.trim()) return;

    PostOnetoOneChatApi(uuid, frienduuid, content)
      .then((data) => {
        setMessages((prev) => [...prev, data]);
        setContent("");
      })
      .catch((error) => {
        console.error("메시지 전송 실패:", error);
      });
  }

  const handleKeyPress = (e) => {
    if (e.key === 'Enter' && !e.shiftKey) {
      e.preventDefault();
      postMessage();
    }
  };
  useEffect(() => {
    fetchMessages();
    getReceiverInfo();
  }, [uuid, frienduuid]);

  return (
      <div className="chat-wrapper">
        <div className="chat-container">
          <div className="chat-card">
            {/* 채팅 헤더 */}
            <div className="chat-header">
              <div className="chat-title">{receiverName}</div>
             <div className={`chat-status ${receiverOnline ? 'chat-status-online' : 'chat-status-offline'}`}>
              {receiverOnline ? '온라인' : '오프라인'}
</div>

            </div>

            {/* 메시지 목록 */}
            <div className="messages-container">
              {messages.length > 0 ? (
                messages.map((msg) => {
                  const isMine = msg.senderuuid === user.uuid;
                  return (
                    <div
                      key={msg.id}
                      className={`message-wrapper ${isMine ? 'my-message' : 'other-message'}`}
                    >
                      {/* 상대 메시지일 때만 프로필 보이기 */}
                      {!isMine && (
                        <img
                          src={msg.profilePhotoURL || "/default-profile.png"}
                          alt={msg.senderName}
                          className="message-profile-img"
                        />
                      )}

                      {/* 메시지 본문 */}
                      <div className="message-content-wrapper">
                        {!isMine && (
                          <div className="message-sender-name">
                            {msg.senderName ?? "알 수 없음"}
                          </div>
                        )}
                        <div className={`message-bubble ${isMine ? 'my-bubble' : 'other-bubble'}`}>
                          {msg.content}
                        </div>
                        <div className="message-timestamp">
                          {msg.timestamp}
                        </div>
                      </div>
                    </div>
                  );
                })
              ) : (
                <div className="no-messages">메시지가 없습니다.</div>
              )}
            </div>

            {/* 입력창 */}
            <div className="message-input-container">
              <div className="input-wrapper">
                <input
                  type="text"
                  value={content}
                  onChange={(e) => setContent(e.target.value)}
                  onKeyPress={handleKeyPress}
                  placeholder="메시지를 입력하세요..."
                  className="message-input"
                  maxLength={30}
                />
                <button 
                  onClick={postMessage} 
                  className={`send-btn ${content.trim() ? 'active' : ''}`}
                  disabled={!content.trim()}
                >
                  <svg width="20" height="20" viewBox="0 0 24 24" fill="none">
                    <path d="M2 21L23 12L2 3V10L17 12L2 14V21Z" fill="currentColor"/>
                  </svg>
                </button>
              </div>
              <div className="char-count-message">
                {content.length} / 30
              </div>
            </div>
          </div>
        </div>
      </div>
  );
}