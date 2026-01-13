import { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import Header from "../UI/Header";
import { AllPostListApi, toggleLikeApi } from "../api/ApiService";
import FriendSidebar from "../UI/FriendSidebar"; 
import "../Css/UserHomePage.css"; 
import { useUserAuth } from "../api/AuthProvider";
import { FaHeart, FaRegHeart } from "react-icons/fa";

export default function UserHomePage() {
  const navigate = useNavigate();
  const [posts, setPosts] = useState([]);
  const { user, isLogin } = useUserAuth();

  const toggleLike = async (postId) => {
    if (!isLogin) return alert("로그인 후 이용 가능합니다.");

    // UI 먼저 반영
    const previousPosts = [...posts]; // 롤백용
    setPosts(posts =>
      posts.map(post =>
        post.id === postId
          ? { 
              ...post, 
              liked: !post.liked, 
              likeCount: post.liked ? post.likeCount - 1 : post.likeCount + 1 
            }
          : post
      )
    );

    try {
      await toggleLikeApi(postId);
    } catch (err) {
      console.error("좋아요 토글 실패:", err);
      setPosts(previousPosts); // 실패 시 롤백
      alert("좋아요 처리에 실패했습니다.");
    }
  };

  useEffect(() => {
    AllPostListApi()
      .then(res => {
        const postsData = res.data.map(post => ({
          ...post,
          liked: post.likes,
          currentPhotoIndex: 0
        }));
        setPosts(postsData);
      })
      .catch(err => console.error("게시글 목록 불러오기 실패:", err));
  }, []);

  const handlePostClick = (userUuid, postId) => {
    navigate(`/messenger/${userUuid}/${postId}`);
  };

  const changePhotoIndex = (postId, index) => {
    setPosts(posts =>
      posts.map(post =>
        post.id === postId
          ? { 
              ...post, 
              currentPhotoIndex: (post.currentPhotoIndex + index + post.photos.length) % post.photos.length 
            }
          : post
      )
    );
  };

  return (
    <div className="display-wrapper">
      <Header />
      <div className="feed-wrapper">
        <div className="feed-container">
          {posts.map(post => (
            <div
              key={post.id}
              className="post-card"
              onClick={() => handlePostClick(post.userUuid, post.id)}
            >
              <div className="post-header">
                <div className="profile-pic">
                  <img
                    src={post.profilePhotoUrl || "/default-profile.png"}
                    alt={post.name}/>
                </div>
                <div className="post-user-info">
                  <span className="username">{post.name}</span>
                  <span className="post-time">
                   {new Date(post.createdDate).toLocaleString()}
                  </span>
                </div>
              </div>

              <div className="post-content">{post.context}</div>

              {post.photos && post.photos.length > 0 && (
                <div className="post-image-wrapper">
                  <img
                    className="post-image-single"
                    src={post.photos[post.currentPhotoIndex].photoUrl}
                    alt=""
                  />

                  {post.photos.length > 1 && (
                    <>
                      <button
                        className="photo-nav-btn left"
                        onClick={e => { e.stopPropagation(); changePhotoIndex(post.id, -1); }}
                      >
                        ◀
                      </button>

                      <button
                        className="photo-nav-btn right"
                        onClick={e => { e.stopPropagation(); changePhotoIndex(post.id, 1); }}
                      >
                        ▶
                      </button>

                      <div className="photo-indicators">
                        {post.photos.map((photo, index) => (
                          <span
                            key={index}
                            className={`indicator-dot ${index === post.currentPhotoIndex ? "active" : ""}`}
                          ></span>
                        ))}
                      </div>
                    </>
                  )}
                </div>
              )}

              <div className="post-actions">
                <div className="post-actions-left">
                  <button
                    className="action-btn"
                    onClick={e => { e.stopPropagation(); toggleLike(post.id); }}
                  >
                    {post.liked ? <FaHeart color="#ed4956" /> : <FaRegHeart color="#262626" />}
                  </button>
                  <span className="likes-count">좋아요 {post.likeCount}개</span>
                </div>
                <div className="post-actions-right">
                  <span className="comment-count">댓글 {post.commentCount ?? 0}개</span>
                </div>
              </div>
            </div>
          ))}
        </div>
          <FriendSidebar />
      </div>
    </div>
  );
}
