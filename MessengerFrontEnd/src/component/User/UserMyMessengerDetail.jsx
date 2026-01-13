import { useEffect, useState } from "react";
import { useNavigate, useParams } from "react-router-dom";
import Header from "../UI/Header";
import Profile  from "../UI/Profile";
import FriendSidebar from "../UI/FriendSidebar";
import { useUserAuth } from "../api/AuthProvider";
import {
  UserPostApi,
  PostDeleteApi,
  GetCommentsByPostApi,
  WriteCommentApi,
  toggleLikeApi,
  DeleteCommentApi,
  UpdateCommentApi  
} from "../api/ApiService";
import { FaHeart, FaRegHeart } from "react-icons/fa";
import "../Css/UserMyMessengerDetail.css";

/* 댓글 트리 유틸 */
function countAllComments(list) {
  let count = 0;
  list.forEach(c => {
    count++;
    if (Array.isArray(c.children) && c.children.length > 0) {
      count += countAllComments(c.children);
    }
  });
  return count;
}

function buildCommentTree(list) {
  if (!Array.isArray(list)) return [];
  const map = new Map();
  const roots = [];
  list.forEach(c => map.set(c.id, { ...c, children: [] }));
  list.forEach(c => {
    const node = map.get(c.id);
    const pId = c.parentId ?? null;
    if (pId !== null && map.has(pId)) map.get(pId).children.push(node);
    else roots.push(node);
  });
  return roots;
}

function normalizeComments(data) {
  const arr = Array.isArray(data) ? data : [];
  const hasChildren = arr.some(c => Array.isArray(c.children));
  return hasChildren ? arr : buildCommentTree(arr);
}

/* 댓글 컴포넌트 (재귀) */
function CommentItem({ comment, postid, user, isLogin, onWrite, onDelete, onUpdate, depth = 0 }) {
  const [replyOpen, setReplyOpen] = useState(false);
  const [replyText, setReplyText] = useState("");
  const [isEditing, setIsEditing] = useState(false);
  const [editText, setEditText] = useState(comment.content ?? "");
  const isOwner = isLogin && user && comment.author === user.uuid;

  const handleReplySubmit = () => {
    if (!replyText.trim()) return alert("댓글을 입력해주세요.");
    onWrite(replyText, comment.id);
    setReplyText("");
    setReplyOpen(false);
  };

  const handleEditSubmit = () => {
    if (!editText.trim()) return alert("댓글을 입력해주세요.");
    onUpdate(comment.id, editText);
    setIsEditing(false);
  };

  const handleEditCancel = () => {
    setEditText(comment.content ?? "");
    setIsEditing(false);
  };

  return (
    <div className="comment-item" style={{ marginLeft: depth * 20 }}>
      <div className="comment-main-row">
        <strong>{comment.name}</strong>: {isEditing ? (
          <div style={{ display: 'inline-flex', alignItems: 'center', gap: '8px' }}>
            <input 
              value={editText} 
              onChange={e => setEditText(e.target.value)} 
              style={{ width: "300px" }}
              onKeyDown={e => e.key === 'Enter' && handleEditSubmit()}
            />
            <button onClick={handleEditSubmit} style={{ fontSize: '12px' }}>완료</button>
            <button onClick={handleEditCancel} style={{ fontSize: '12px' }}>취소</button>
          </div>
        ) : comment.content ?? <i>삭제된 댓글입니다</i>}
      </div>
      <div className="comment-sub-row">
        <span>{new Date(comment.createdDate).toLocaleString()}</span>
        {isOwner && !isEditing && comment.content && (
          <div className="comment-buttons">
            <button onClick={() => setIsEditing(true)}>수정</button>
            <button onClick={() => onDelete(comment.id)} className="delete-btn">삭제</button>
          </div>
        )}
      </div>
      {depth < 1 && !replyOpen && (
        <button style={{ fontSize: 12, marginTop: 4 }} onClick={() => setReplyOpen(true)}>답글</button>
      )}
      {replyOpen && (
        <div style={{ marginTop: 4 }}>
          <input 
            type="text" 
            value={replyText} 
            onChange={e => setReplyText(e.target.value)} 
            placeholder="답글 입력"
            onKeyDown={e => e.key === 'Enter' && handleReplySubmit()}
          />
          <button onClick={handleReplySubmit}>작성</button>
          <button onClick={() => setReplyOpen(false)}>취소</button>
        </div>
      )}
      {Array.isArray(comment.children) && comment.children.map(child => (
        <CommentItem key={child.id} comment={child} postid={postid} user={user} isLogin={isLogin}
          onWrite={onWrite} onDelete={onDelete} onUpdate={onUpdate} depth={depth + 1} />
      ))}
    </div>
  );
}

export default function UserMyMessengerDetail() {
  const { postid } = useParams();
  const navigate = useNavigate();
  const { user, isLogin } = useUserAuth();

  const [post, setPost] = useState(null);
  const [comments, setComments] = useState([]);
  const [newComment, setNewComment] = useState("");
  const [likes, setLikes] = useState(false);
  const [likeCount, setLikeCount] = useState(0);
  const [currentPhotoIndex, setCurrentPhotoIndex] = useState(0);

  useEffect(() => {
    if (!postid) return;
    UserPostApi(postid)
      .then(res => {
        const data = res?.data ?? null;
        setPost(data);
        setLikes(data?.likes ?? false);
        setLikeCount(data?.likeCount ?? 0);
        setCurrentPhotoIndex(0);
      })
      .catch(() => setPost(null));
  }, [postid]);

  const fetchComments = () => {
    if (!postid) return;
    GetCommentsByPostApi(postid)
      .then(res => setComments(normalizeComments(res?.data ?? [])))
      .catch(err => console.error(err));
  };
  useEffect(() => { fetchComments(); }, [postid]);

  const handleWriteComment = async (content, parentId = null) => {
    if (!content.trim()) return alert("댓글을 입력해주세요.");
    try {
      await WriteCommentApi(postid, content, parentId);
      fetchComments();
      if (!parentId) setNewComment("");
    } catch (err) { 
      console.error(err);
      alert("댓글 작성에 실패했습니다.");
    }
  };

  const handleDeleteComment = async (commentId) => {
    if (!window.confirm("정말 댓글을 삭제하시겠습니까?")) return;
    try {
      await DeleteCommentApi(commentId);
      fetchComments(); // 댓글 목록 새로고침
      alert("댓글이 삭제되었습니다.");
    } catch (err) {
      console.error(err);
      alert("댓글 삭제에 실패했습니다.");
    }
  };

  const handleUpdateComment = async (commentId, content) => {
    try {
      await UpdateCommentApi(commentId, content);
      fetchComments(); // 댓글 목록 새로고침
      alert("댓글이 수정되었습니다.");
    } catch (err) {
      console.error(err);
      alert("댓글 수정에 실패했습니다.");
    }
  };

  const handleDeletePost = async () => {
    if (!window.confirm("정말 삭제하시겠습니까?")) return;
    try {
      await PostDeleteApi(postid);
      alert("삭제 완료");
      navigate(`/messenger/${user.uuid}`);
    } catch (err) { 
      console.error(err);
      alert("게시글 삭제에 실패했습니다.");
    }
  };

  const handleToggleLike = async () => {
    if (!isLogin) return alert("로그인 후 이용 가능합니다.");
    try {
      const res = await toggleLikeApi(postid);
      setLikes(res.data.likes);
      setLikeCount(res.data.likeCount);
    } catch (err) { 
      console.error(err);
      alert("좋아요 처리에 실패했습니다.");
    }
  };

  const changePhotoIndex = (index) => {
    if (!post?.photos) return;
    setCurrentPhotoIndex((currentPhotoIndex + index + post.photos.length) % post.photos.length);
  };

  if (!post) return <div><Header /><p>게시글이 없습니다.</p></div>;
  const isOwner = user && post.userUuid === user.uuid;

  return (
    <div>
      <Header />
      <div className="feed-wrapper">
          <Profile uuid={post.userUuid} />
        <div className="feed-container">
          <div className="post-and-comments">
            <div className="post-card">
              <div className="post-header">
                <div className="profile-pic">
                  {post.profilePhotoUrl ? <img src={post.profilePhotoUrl} alt="profile"/> : post.name?.charAt(0)}
                </div>
                <div className="post-user-info">
                  <span className="username">{post.name}</span>
                  <span className="post-time">{new Date(post.createdDate).toLocaleString()}</span>
                </div>
              </div>

              <div className="post-content">{post.context}</div>

              {post.photos?.length > 0 && (
                <div className="post-image-wrapper">
                  <img src={post.photos[currentPhotoIndex].photoUrl} alt="" className="post-image-single"/>
                  {post.photos.length > 1 && (
                    <>
                      <button className="photo-nav-btn left" onClick={() => changePhotoIndex(-1)}>◀</button>
                      <button className="photo-nav-btn right" onClick={() => changePhotoIndex(1)}>▶</button>
                      <div className="photo-indicators">
                        {post.photos.map((_, idx) => (
                          <span key={idx} className={`indicator-dot ${idx === currentPhotoIndex ? "active" : ""}`}></span>
                        ))}
                      </div>
                    </>
                  )}
                </div>
              )}

              <div className="post-actions">
                <div className="post-actions-left">
                  <button className="action-btn" onClick={handleToggleLike}>
                    {likes ? <FaHeart color="red"/> : <FaRegHeart color="gray"/>}
                  </button>
                  <span className="likes-count">좋아요 {likeCount}개</span>
                </div>

                <div className="post-actions-right">
                  {isOwner && (
                    <div className="post-owner-actions">
                      <button className="edit-btn" onClick={() => navigate(`/messenger/${user.uuid}/edit/${postid}`)}>수정</button>
                      <button className="delete-btn" onClick={handleDeletePost}>삭제</button>
                    </div>
                  )}
                  <span className="comment-count">댓글: {countAllComments(comments)}개</span>
                </div>
              </div>
            </div>

            <div className="comment-section">
              <div className="comment-input-wrapper">
                <input 
                  type="text" 
                  value={newComment} 
                  onChange={e => setNewComment(e.target.value)}
                  placeholder={isLogin ? "댓글을 입력하세요" : "로그인 후 작성 가능합니다."}
                  disabled={!isLogin}
                  onKeyDown={e => e.key === 'Enter' && handleWriteComment(newComment)}
                />
                <button onClick={() => handleWriteComment(newComment)} disabled={!isLogin || !newComment.trim()}>작성</button>
              </div>

              {comments.map(c => (
                <CommentItem 
                  key={c.id} 
                  comment={c} 
                  postid={postid} 
                  user={user} 
                  isLogin={isLogin}
                  onWrite={handleWriteComment} 
                  onDelete={handleDeleteComment}
                  onUpdate={handleUpdateComment}
                />
              ))}
            </div>
          </div>
        </div>
            <FriendSidebar />
      </div>
    </div>
  );
}