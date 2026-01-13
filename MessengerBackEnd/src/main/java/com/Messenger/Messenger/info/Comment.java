package com.Messenger.Messenger.info;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;

@Entity
public class Comment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String content;
    private LocalDateTime createdDate;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private MessengerUser messengerUser;

    @ManyToOne
    @JoinColumn(name = "post_id")
    private MessagePost messagePost;

    // 부모 댓글 (null이면 최상위 댓글)
    @ManyToOne
    @JoinColumn(name = "parent_id")
    private Comment parent;

    // 자식 댓글들 (대댓글들)
    @OneToMany(mappedBy = "parent") // cascade, orphanRemoval 제거
    private List<Comment> children = new ArrayList<>();

    public Comment() {}

    public Comment(String content, MessengerUser messengerUser, MessagePost messagePost, Comment parent) {
        this.content = content;
        this.messengerUser = messengerUser;
        this.messagePost = messagePost;
        this.parent = parent;
        this.createdDate = LocalDateTime.now();
    }

    // Getter / Setter
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public LocalDateTime getCreatedDate() { return createdDate; }
    public void setCreatedDate(LocalDateTime createdDate) { this.createdDate = createdDate; }

    public MessengerUser getMessengerUser() { return messengerUser; }
    public void setMessengerUser(MessengerUser messengerUser) { this.messengerUser = messengerUser; }

    public MessagePost getMessagePost() { return messagePost; }
    public void setMessagePost(MessagePost messagePost) { this.messagePost = messagePost; }

    public Comment getParent() { return parent; }
    public void setParent(Comment parent) { this.parent = parent; }

    public List<Comment> getChildren() { return children; }
    public void setChildren(List<Comment> children) { this.children = children; }

}
